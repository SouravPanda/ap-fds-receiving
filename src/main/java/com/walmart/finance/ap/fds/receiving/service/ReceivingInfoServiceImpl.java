package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.common.ReceivingUtils;
import com.walmart.finance.ap.fds.receiving.config.DefaultValuesConfigProperties;
import com.walmart.finance.ap.fds.receiving.config.ReceivingLineComparator;
import com.walmart.finance.ap.fds.receiving.config.ReceivingSummaryComparator;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingInfoLineResponseConverter;
import com.walmart.finance.ap.fds.receiving.dao.FreightDao;
import com.walmart.finance.ap.fds.receiving.dao.ReceivingLineDao;
import com.walmart.finance.ap.fds.receiving.dao.ReceivingSummaryDao;
import com.walmart.finance.ap.fds.receiving.dao.queryCriteria.FreightCriteria;
import com.walmart.finance.ap.fds.receiving.dao.queryCriteria.ReceivingLineCriteria;
import com.walmart.finance.ap.fds.receiving.dao.queryCriteria.ReceivingSummaryCriteria;
import com.walmart.finance.ap.fds.receiving.exception.*;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnIntegrationService;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnResponseData;
import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.model.*;
import com.walmart.finance.ap.fds.receiving.response.*;
import com.walmart.finance.ap.fds.receiving.runnableTask.FinancialTransactionTask;
import com.walmart.finance.ap.fds.receiving.runnableTask.ReceivingDetailsTask;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestCombinations;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestQueryParameters;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.*;

/**
 * Service layer to get the data from financial transaction API and respond with model response.
 */
@Service
public class ReceivingInfoServiceImpl implements ReceivingInfoService {
    public static final Logger log = LoggerFactory.getLogger(ReceivingInfoServiceImpl.class);

    @Setter
    @Value("${months.per.shard}")
    private Integer monthsPerShard;

    @Setter
    @Value("${months.to.display}")
    private Integer monthsToDisplay;

    @Autowired
    FinancialTxnIntegrationService financialTxnIntegrationService;

    @Autowired
    ReceivingLineDao receivingLineDao;

    @Autowired
    ReceivingSummaryDao receivingSummaryDao;

    @Autowired
    FreightDao freightDao;

    @Autowired
    private DefaultValuesConfigProperties defaultValuesConfigProperties;

    @Autowired
    ReceivingInfoLineResponseConverter receivingInfoLineResponseConverter;

    ExecutorService executorService = Executors.newFixedThreadPool(2);


    private List<ReceiveSummary> getSummaryData(Map<String, String> allRequestParams) {

        List<Criteria> criteria = ReceivingSummaryCriteria.getCriteriaForReceivingSummary(allRequestParams);
        List<ReceiveSummary>  receiveSummaryList = receivingSummaryDao.executeSummaryAggregation(criteria);

        return allRequestParams.get(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam())
                .equals(LOCATION_TYPE_WAREHOUSE)?
                mergeDuplicateSummaryRecords(receiveSummaryList) : receiveSummaryList;

    }

    /*************************** Version 1 Methods ***********************************/

    /**
     * Return the response with merge data of Fin Txn and Receiving.
     *
     * @param allRequestParams
     * @return
     */
    @Override
    public ReceivingResponse getInfoServiceDataV1(Map<String, String> allRequestParams) {

        ReceivingResponse successMessage = new ReceivingResponse();

        if (String.valueOf(allRequestParams.get(ReceivingConstants.SCENARIO)).equals(ReceivingInfoRequestCombinations.TRANSACTIONID_TRANSACTIONSEQNBR.name()) ||
            allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.INVOICEID.getQueryParam()) ||
                allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam()) ) {
            recvAndFinTxnSequentialRequestV1(allRequestParams, successMessage);
        } else {
            recvAndFinTxnParallelRequestV1(allRequestParams, successMessage);
        }
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.now());
        return successMessage;
    }

    public void recvAndFinTxnSequentialRequestV1(Map<String, String> allRequestParams, ReceivingResponse successMessage) {
        List<ReceivingInfoResponseV1> receivingInfoResponses;
        List<FinancialTxnResponseData> financialTxnResponseDataList = financialTxnIntegrationService.getFinancialTxnDetails(allRequestParams);
        if (CollectionUtils.isNotEmpty(financialTxnResponseDataList)) {
            receivingInfoResponses = getDataForFinancialTxnV1(financialTxnResponseDataList, allRequestParams);
            if (CollectionUtils.isEmpty(receivingInfoResponses)) {
                throw new NotFoundException("Receiving data not found for given search criteria.");
            }
        } else {
            log.warn("Financial Transaction data not found for given search criteria. Not checking Receiving data.");
            throw new NotFoundException("Financial Transaction data not found for given search criteria.");
        }
        successMessage.setData(receivingInfoResponses);
    }

    public void recvAndFinTxnParallelRequestV1(Map<String, String> allRequestParams, ReceivingResponse successMessage) {
        Future<List<ReceivingInfoResponseV1>> receivingFuture =
                executorService.submit(new ReceivingDetailsTask(allRequestParams, this,
                        MDC.getCopyOfContextMap()));

        Future<List<FinancialTxnResponseData>> finTxnFuture =
                executorService.submit(new FinancialTransactionTask(allRequestParams, financialTxnIntegrationService,
                        MDC.getCopyOfContextMap()));

        while (!receivingFuture.isDone() || !finTxnFuture.isDone()) {
            //Waiting
        }


        List<ReceivingInfoResponseV1> receivingInfoResponseV1List = null;

        List<FinancialTxnResponseData> financialTxnResponseDataList = null;

        try {
            receivingInfoResponseV1List = receivingFuture.get();
            log.info("No. of Records from Receiving - " + receivingInfoResponseV1List.size());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to fetch Receiving Data - ", e);
            throw new ReceivingException("Failed to fetch Receiving Data.");
        }

        try {
            financialTxnResponseDataList = finTxnFuture.get();
            log.info("No. of Records from Fin Txn - " + financialTxnResponseDataList.size());

        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to fetch Financial Txn Data - ", e);
            throw new ReceivingException("Failed to fetch Financial Txn Data.");
        }


        if (CollectionUtils.isEmpty(receivingInfoResponseV1List) && CollectionUtils.isEmpty(financialTxnResponseDataList)) {
            throw new NotFoundException("Receiving and Fin Txn data not found for given search criteria.");
        }


        List<ReceivingInfoResponseV1> updateReceivingInfoResponsesList = amalgamateReceivingInfoResponseV1(allRequestParams, receivingInfoResponseV1List, financialTxnResponseDataList);

        successMessage.setData(updateReceivingInfoResponsesList);
    }

    public List<ReceivingInfoResponseV1> amalgamateReceivingInfoResponseV1(Map<String, String> allRequestParams, List<ReceivingInfoResponseV1> receivingInfoResponseV1List, List<FinancialTxnResponseData> financialTxnResponseDataList) {
        Map<String, ReceivingInfoResponseV1> receivingInfoResponseV1Map =
                getReceivingInfoMap(receivingInfoResponseV1List);

        List<ReceivingInfoResponseV1> receivingInfoResponsesList = new ArrayList<>();
        List<String> receivingInfoResponsesKeyList = new ArrayList<>();

        for (FinancialTxnResponseData financialTxnResponseData : financialTxnResponseDataList) {
            Integer storeNumber = (financialTxnResponseData.getOrigStoreNbr() == null)
                    ? financialTxnResponseData.getStoreNumber() : financialTxnResponseData.getOrigStoreNbr();
            String id = (financialTxnResponseData.getPurchaseOrderId() == null ? 0 :
                    financialTxnResponseData.getPurchaseOrderId())
                    + ReceivingConstants.PIPE_SEPARATOR
                    + (StringUtils.isEmpty(financialTxnResponseData.getReceiveId()) ? "0" :
                    financialTxnResponseData.getReceiveId()) + ReceivingConstants.PIPE_SEPARATOR
                    + (storeNumber == null ? 0 : storeNumber) + ReceivingConstants.PIPE_SEPARATOR
                    + (financialTxnResponseData.getReceivingDate() == null ? "0" :
                    financialTxnResponseData.getReceivingDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate());
            ReceivingInfoResponseV1 receivingInfoResponseV1 =
                    receivingInfoResponseV1Map.containsKey(id) ?
                            ReceivingUtils.getRecvInfoRespV1Copy(receivingInfoResponseV1Map.get(id)) :
                            new ReceivingInfoResponseV1();
            if (receivingInfoResponseV1 != null) {

                receivingInfoResponsesKeyList.add(id);
                updateReceivingInfoResponseV1(financialTxnResponseData, receivingInfoResponseV1);
                receivingInfoResponsesList.add(receivingInfoResponseV1);
            }
        }
        receivingInfoResponsesKeyList.forEach(receivingInfoResponseV1Map::remove);
        receivingInfoResponsesList.addAll(new ArrayList<>(receivingInfoResponseV1Map.values()));

        List<ReceivingInfoResponseV1> updateReceivingInfoResponsesList = new ArrayList<>();

        for (ReceivingInfoResponseV1 receivingInfoResponseV1 : receivingInfoResponsesList) {
            if (!(allRequestParams.get(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam()) == null || allRequestParams.get(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam()).isEmpty()) ||
                    !(allRequestParams.get(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam()) == null || allRequestParams.get(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam()).isEmpty())) {
                if (!(StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.LINENUMBERFLAG.getQueryParam()))
                        && allRequestParams.get(ReceivingInfoRequestQueryParameters.LINENUMBERFLAG.getQueryParam()).equalsIgnoreCase("Y")) && CollectionUtils.isNotEmpty(receivingInfoResponseV1.getReceivingInfoLineResponses())) {
                    receivingInfoResponseV1.setReceivingInfoLineResponses(null);
                } else if (CollectionUtils.isEmpty(receivingInfoResponseV1.getReceivingInfoLineResponses())) {
                    continue;
                }
            }
            updateReceivingInfoResponsesList.add(receivingInfoResponseV1);
        }

        if (CollectionUtils.isEmpty(updateReceivingInfoResponsesList)) {
            throw new NotFoundException("Receiving and Fin Txn data not found for given search criteria.");
        }
        return updateReceivingInfoResponsesList;
    }


    private Map<String, ReceivingInfoResponseV1> getReceivingInfoMap(List<ReceivingInfoResponseV1> receivingInfoResponseV1List) {
        Map<String, ReceivingInfoResponseV1> receivingInfoResponseV1Map = new HashMap<>();
        for (ReceivingInfoResponseV1 receivingInfoResponseV1 : receivingInfoResponseV1List) {
            receivingInfoResponseV1Map.put(receivingInfoResponseV1.get_id(), receivingInfoResponseV1);
        }
        return receivingInfoResponseV1Map;
    }


    @Override
    public List<ReceivingInfoResponseV1> getReceivingInfoWoFinTxn(Map<String, String> allRequestParams) {
        List<ReceivingInfoResponseV1> receivingInfoResponses = new ArrayList<>();

        List<ReceiveSummary> receiveSummaries = getSummaryData(allRequestParams);
        Map<String, List<ReceivingLine>> receivingLineMap = new HashMap<>();
        Map<Long, FreightResponse> freightResponseMap = new HashMap<>();

        if (CollectionUtils.isNotEmpty(receiveSummaries)) {

            List<ReceivingLine> lineResponseList = getLineResponseList(allRequestParams,receiveSummaries);

            List<FreightResponse> freightResponseList = getFreightResponseList(receiveSummaries);

            Iterator<ReceivingLine> iteratorLine = lineResponseList.iterator();
            //Grouping lines according to SummaryReference
            while (iteratorLine.hasNext()) {
                ReceivingLine receivingLine = iteratorLine.next();
                if (receivingLineMap.containsKey(receivingLine.getSummaryReference())) {
                    receivingLineMap.get(receivingLine.getSummaryReference()).add(receivingLine);
                } else {
                    List<ReceivingLine> lineList = new ArrayList<>();
                    lineList.add(receivingLine);
                    receivingLineMap.put(receivingLine.getSummaryReference(), lineList);
                }
                iteratorLine.remove();
            }
            freightResponseMap = freightResponseList.stream().collect(Collectors.toMap(FreightResponse::getFreightId, freightResponse -> freightResponse));

        }

        Iterator<ReceiveSummary> iteratorSummary = receiveSummaries.iterator();
        while (iteratorSummary.hasNext()) {
            ReceiveSummary receiveSummary = iteratorSummary.next();
            ReceivingInfoResponseV1 receivingInfoResponseV1 = conversionToReceivingInfoV1(receiveSummary,
                    null
                    , receivingLineMap.containsKey(receiveSummary.get_id()) ? receivingLineMap.get(receiveSummary.get_id()) : new ArrayList<>()
                    , freightResponseMap.containsKey(receiveSummary.getFreightBillExpandId()) ? freightResponseMap.get(receiveSummary.getFreightBillExpandId()) : new FreightResponse()
                    , allRequestParams);
            receivingInfoResponses.add(receivingInfoResponseV1);
        }
        return receivingInfoResponses;
    }



    /**
     * Retrieve receiving response for all Fin Txn objects
     *
     * @param financialTxnResponseDataList
     * @param allRequestParams
     * @return
     */
    private List<ReceivingInfoResponseV1> getDataForFinancialTxnV1(List<FinancialTxnResponseData> financialTxnResponseDataList, Map<String, String> allRequestParams) {
        List<ReceivingInfoResponseV1> receivingInfoResponses;
        List<ReceiveSummary> allReceiveSummaries;
        List<Long> freightIds = new ArrayList<>();
        Map<FinancialTxnResponseData, ReceiveSummary> financialTxnReceivingMap = new HashMap<>();
        List<String> ids = new ArrayList<>();
        Set<String> partitionKeys = new HashSet<>();
        Map<String, ReceiveSummary> receiveSummaryMap = new HashMap<>();
        Map<String, List<ReceivingLine>> receivingLineMap = new HashMap<>();
        Map<Long, FreightResponse> freightResponseMap = new HashMap<>();

        for (FinancialTxnResponseData financialTxnResponseData : financialTxnResponseDataList) {

            Integer storeNumber = financialTxnResponseData.getOrigStoreNbr() == null
                    ? financialTxnResponseData.getStoreNumber() : financialTxnResponseData.getOrigStoreNbr();
            String id = (financialTxnResponseData.getPurchaseOrderId() == null ? 0 : financialTxnResponseData.getPurchaseOrderId()) + ReceivingConstants.PIPE_SEPARATOR
                    + (StringUtils.isEmpty(financialTxnResponseData.getReceiveId()) ? "0" : financialTxnResponseData.getReceiveId()) + ReceivingConstants.PIPE_SEPARATOR
                    + (storeNumber == null ? 0 : storeNumber) + ReceivingConstants.PIPE_SEPARATOR
                    + (financialTxnResponseData.getReceivingDate() == null ? "0" : financialTxnResponseData.getReceivingDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate());
            if (StringUtils.isNotEmpty(id) && !id.equalsIgnoreCase("0|0|0|0")) {
                ids.add(id);
            }

            if (storeNumber != null) {
                LocalDate receivingDate = null;
                if (financialTxnResponseData.getReceivingDate() != null) {
                    receivingDate =
                            financialTxnResponseData.getReceivingDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate();
                }
                partitionKeys.addAll(ReceivingUtils.getPartitionKeyList(receivingDate, allRequestParams,
                        storeNumber,  monthsPerShard,  monthsToDisplay));
            }
        }

        allReceiveSummaries = getSummaryData(ids, partitionKeys, allRequestParams);

        if (CollectionUtils.isNotEmpty(allReceiveSummaries)) {

            allReceiveSummaries.forEach(receiveSummary -> {
                receiveSummaryMap.put(receiveSummary.get_id(), receiveSummary);
            });

            List<ReceivingLine> lineResponseList = getLineResponseList(allRequestParams,allReceiveSummaries);

            List<FreightResponse> freightResponseList = getFreightResponseList(allReceiveSummaries);

            Iterator<ReceivingLine> iteratorLine = lineResponseList.iterator();
            //Grouping lines according to SummaryReference
            while (iteratorLine.hasNext()) {
                ReceivingLine receivingLine = iteratorLine.next();
                if (receivingLineMap.containsKey(receivingLine.getSummaryReference())) {
                    receivingLineMap.get(receivingLine.getSummaryReference()).add(receivingLine);
                } else {
                    List<ReceivingLine> lineList = new ArrayList<>();
                    lineList.add(receivingLine);
                    receivingLineMap.put(receivingLine.getSummaryReference(), lineList);
                }
                iteratorLine.remove();
            }
            freightResponseMap = freightResponseList.stream().collect(Collectors.toMap(FreightResponse::getFreightId, freightResponse -> freightResponse));

        }


        receivingInfoResponses = mergeTxnWithReceivingData(financialTxnResponseDataList,allRequestParams,receiveSummaryMap,financialTxnReceivingMap,receivingLineMap,freightResponseMap);

        if(receivingInfoResponses.isEmpty())
            throw new ContentNotFoundException(ReceivingErrors.RECEIVINGINFO.getParameterName(), ReceivingErrors.VALIDID.getParameterName());
        return receivingInfoResponses;
    }

    private List<FreightResponse> getFreightResponseList(List<ReceiveSummary> receiveSummaries) {

        List<Long> freightIds = FreightCriteria.getFreightCriteria(receiveSummaries);

        List<FreightResponse> freightResponseList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(freightIds)) {
            Query query = new Query(Criteria.where("_id").in(freightIds));
            log.info("query: " + query);
            freightResponseList = freightDao.executeQueryInFreight(query);
        }
        return freightResponseList;
    }

    private List<ReceivingInfoResponseV1> mergeTxnWithReceivingData(List<FinancialTxnResponseData> financialTxnResponseDataList, Map<String, String> allRequestParams, Map<String, ReceiveSummary> receiveSummaryMap, Map<FinancialTxnResponseData, ReceiveSummary> financialTxnReceivingMap, Map<String, List<ReceivingLine>> receivingLineMap,Map<Long, FreightResponse> freightResponseMap) {

        List<ReceivingInfoResponseV1> receivingInfoResponses = new ArrayList<>();
        for (FinancialTxnResponseData financialTxnResponseData : financialTxnResponseDataList) {
            Integer storeNumber = (financialTxnResponseData.getOrigStoreNbr() == null)
                    ? financialTxnResponseData.getStoreNumber() : financialTxnResponseData.getOrigStoreNbr();
            String id = (financialTxnResponseData.getPurchaseOrderId() == null ? 0 :
                    financialTxnResponseData.getPurchaseOrderId())
                    + ReceivingConstants.PIPE_SEPARATOR
                    + (StringUtils.isEmpty(financialTxnResponseData.getReceiveId()) ? "0" :
                    financialTxnResponseData.getReceiveId()) + ReceivingConstants.PIPE_SEPARATOR
                    + (storeNumber == null ? 0 : storeNumber) + ReceivingConstants.PIPE_SEPARATOR
                    + (financialTxnResponseData.getReceivingDate() == null ? "0" :
                    financialTxnResponseData.getReceivingDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate());

            if (receiveSummaryMap.containsKey(id)) {
                financialTxnReceivingMap.put(financialTxnResponseData, receiveSummaryMap.getOrDefault(id, new ReceiveSummary()));
            }


            ReceivingInfoResponseV1 receivingInfoResponseV1;
            if (financialTxnReceivingMap.containsKey(financialTxnResponseData)) {
                receivingInfoResponseV1 =
                        conversionToReceivingInfoV1(financialTxnReceivingMap.get(financialTxnResponseData)
                                , financialTxnResponseData
                                , receivingLineMap.containsKey(financialTxnReceivingMap.get(financialTxnResponseData).get_id())
                                        ? receivingLineMap.get(financialTxnReceivingMap.get(financialTxnResponseData).get_id()) : new ArrayList<>()
                                , freightResponseMap.containsKey(financialTxnReceivingMap.get(financialTxnResponseData).getFreightBillExpandId())
                                        ? freightResponseMap.get(financialTxnReceivingMap.get(financialTxnResponseData).getFreightBillExpandId()) : new FreightResponse()
                                , allRequestParams);
            } else {
                receivingInfoResponseV1 =
                        conversionToReceivingInfoV1(null
                                , financialTxnResponseData
                                , new ArrayList<>()
                                , new FreightResponse()
                                , allRequestParams);
            }
            if (!(allRequestParams.get(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam()) == null || allRequestParams.get(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam()).isEmpty()) ||
                    !(allRequestParams.get(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam()) == null || allRequestParams.get(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam()).isEmpty())) {
                if (!(StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.LINENUMBERFLAG.getQueryParam()))
                        && allRequestParams.get(ReceivingInfoRequestQueryParameters.LINENUMBERFLAG.getQueryParam()).equalsIgnoreCase("Y")) && !receivingInfoResponseV1.getReceivingInfoLineResponses().isEmpty()) {
                    receivingInfoResponseV1.setReceivingInfoLineResponses(null);
                } else if (receivingInfoResponseV1.getReceivingInfoLineResponses().isEmpty()) {
                    continue;
                }
            }
            receivingInfoResponses.add(receivingInfoResponseV1);
        }
        return receivingInfoResponses;
    }


    private List<ReceivingLine> getLineResponseList(Map<String,String> allRequestParams, List<ReceiveSummary> receiveSummaries) {

        Set<String> partitionNumbers = new HashSet<>();
        List<String> summaryReferences = new ArrayList<>();
        List<String> receivingControlNumberList = new ArrayList<>();

        receiveSummaries.forEach(receiveSummary -> {

            if (null != receiveSummary.getStoreNumber()) {
                partitionNumbers.addAll(ReceivingUtils
                        .getPartitionKeyList(receiveSummary.getReceivingDate(), allRequestParams, receiveSummary.getStoreNumber(),
                                monthsPerShard, monthsToDisplay));
            }
            if (null != receiveSummary.get_id()) {
                summaryReferences.add(receiveSummary.get_id());
            }
            if (receiveSummary.getReceivingControlNumber() != null) {
                receivingControlNumberList.add(receiveSummary.getReceivingControlNumber());
            }
        });

        List<Criteria> criteriaList = ReceivingLineCriteria.getCriteriaForReceivingInfoLine(allRequestParams,summaryReferences,receivingControlNumberList);

        List<ReceivingLine> receivingLineList = receivingLineDao.executeLineAggregation(criteriaList);

        return allRequestParams.get(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam())
                .equals(LOCATION_TYPE_WAREHOUSE)?
                mergeDuplicateLineRecords(receivingLineList) : receivingLineList;

    }

    public List<ReceivingLine> mergeDuplicateLineRecords(List<ReceivingLine> receivingLineList) {

        if (!receivingLineList.isEmpty()) {
            Collections.sort(receivingLineList, new ReceivingLineComparator());
        }

        Map<String, ReceivingLine> receivingLineMap = new HashMap<>();

        for (ReceivingLine receivingLine : receivingLineList) {
            if (receivingLineMap.containsKey(receivingLine.get_id())) {
                receivingLineMap.get(receivingLine.get_id()).merge(receivingLine);
            } else {
                receivingLineMap.put(receivingLine.get_id(), receivingLine);
            }
        }
        return new ArrayList<>(receivingLineMap.values());
    }

    private List<ReceiveSummary> getSummaryData(List<String> ids, Set<String> partitionKeys,
                                                Map<String, String> allRequestParams) {

        List<Criteria> criteriaList = ReceivingSummaryCriteria.getCriteriaForReceivingSummary(allRequestParams);

        if (CollectionUtils.isNotEmpty(ids) && CollectionUtils.isNotEmpty(partitionKeys)) {
            criteriaList.add(Criteria.where(ReceiveSummaryCosmosDBParameters.ID.getParameterName()).in(ids));
            criteriaList.add(Criteria.where(ReceivingConstants.RECEIVING_SHARD_KEY_FIELD).in(partitionKeys));
        }

        List<ReceiveSummary>  receiveSummaryList =  receivingSummaryDao.executeSummaryAggregation(criteriaList);

        return allRequestParams.get(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam())
                .equals(LOCATION_TYPE_WAREHOUSE)?
                mergeDuplicateSummaryRecords(receiveSummaryList) : receiveSummaryList;
    }

    public List<ReceiveSummary> mergeDuplicateSummaryRecords(List<ReceiveSummary> receiveSummaryList) {

        if (!receiveSummaryList.isEmpty()) {
            Collections.sort(receiveSummaryList, new ReceivingSummaryComparator());
        }

        Map<String, ReceiveSummary> receiveSummaryMap = new HashMap<>();

        for (ReceiveSummary receiveSummary : receiveSummaryList) {
            if (receiveSummaryMap.containsKey(receiveSummary.get_id())) {
                receiveSummaryMap.get(receiveSummary.get_id()).merge(receiveSummary);
            } else {
                receiveSummaryMap.put(receiveSummary.get_id(), receiveSummary);
            }
        }
        return new ArrayList<>(receiveSummaryMap.values());
    }

    private ReceivingInfoResponseV1 conversionToReceivingInfoV1(ReceiveSummary receiveSummary, FinancialTxnResponseData financialTxnResponseData, List<ReceivingLine> lineResponseList, FreightResponse freightResponse, Map<String, String> allRequestParams) {
        ReceivingInfoResponseV1 receivingInfoResponseV1 = new ReceivingInfoResponseV1();

        receivingInfoResponseV1.setLineCount(CollectionUtils.isNotEmpty(lineResponseList) ?
                new Long(lineResponseList.size()) : defaultValuesConfigProperties.getLineCount());
        receivingInfoResponseV1.setCarrierCode(freightResponse != null
                && StringUtils.isNotEmpty(freightResponse.getCarrierCode()) ?
                freightResponse.getCarrierCode() : defaultValuesConfigProperties.getCarrierCode());
        receivingInfoResponseV1.setTrailerNumber(freightResponse != null
                && StringUtils.isNotEmpty(freightResponse.getTrailerNbr()) ?
                freightResponse.getTrailerNbr() : defaultValuesConfigProperties.getTrailerNbr());

        if (receiveSummary != null) {
            receivingInfoResponseV1.set_id(receiveSummary.get_id());
            receivingInfoResponseV1.setControlNumber(StringUtils.isNotEmpty(receiveSummary.getReceivingControlNumber()) ?
                    receiveSummary.getReceivingControlNumber() : defaultValuesConfigProperties.getReceivingControlNumber());
            receivingInfoResponseV1.setTransactionType(receiveSummary.getTransactionType());
            receivingInfoResponseV1.setLocationNumber(receiveSummary.getStoreNumber());
            receivingInfoResponseV1.setPurchaseOrderId(receiveSummary.getPurchaseOrderId());
            receivingInfoResponseV1.setReceiptDate(
                    allRequestParams.get(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam())
                            .equals(LOCATION_TYPE_WAREHOUSE)? receiveSummary.getReceiveProcessDate() :
                    receiveSummary.getDateReceived().atZone(ZoneId.of("GMT")).toLocalDate());
            receivingInfoResponseV1.setReceiptNumber(StringUtils.isNotEmpty(receiveSummary.getReceiveId()) ?
                    receiveSummary.getReceiveId() : "0");
            if (receiveSummary.getTypeIndicator().equals('W')) {
                if (CollectionUtils.isNotEmpty(lineResponseList)) {
                    if (lineResponseList.get(0).getPoLineValue() != null && !lineResponseList.get(0).getPoLineValue().isEmpty()) {
                        receivingInfoResponseV1.setTotalCostAmount(BigDecimal.valueOf(lineResponseList.stream()
                                .filter(t -> t.getPoLineValue().containsKey(UOM_CODE_WH_EXCEPTION_RESOLUTION) &&
                                        t.getReceivedQuantity() != null &&
                                        t.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION).getCostAmount() != null)
                                .mapToDouble(t -> t.getReceivedQuantity() *
                                        t.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION).getCostAmount())
                                .sum()).setScale(2, RoundingMode.HALF_UP).doubleValue());
                        receivingInfoResponseV1.setTotalRetailAmount(BigDecimal.valueOf(
                                lineResponseList.stream()
                                        .filter(t -> t.getPoLineValue().containsKey(UOM_CODE_WH_EXCEPTION_RESOLUTION) &&
                                                t.getReceivedQuantity() != null &&
                                                t.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION).getRetailAmount() != null)
                                        .mapToDouble(t -> t.getReceivedQuantity() *
                                                t.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION).getRetailAmount())
                                        .sum()).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    } else {
                        receivingInfoResponseV1.setTotalCostAmount(BigDecimal.valueOf(lineResponseList.stream()
                                .filter(t -> t.getReceivedQuantity() != null && t.getCostAmount() != null)
                                .mapToDouble(t -> t.getReceivedQuantity() * t.getCostAmount())
                                .sum()).setScale(2, RoundingMode.HALF_UP).doubleValue());
                        receivingInfoResponseV1.setTotalRetailAmount(BigDecimal.valueOf(
                                lineResponseList.stream()
                                        .filter(t -> t.getReceivedQuantity() != null && t.getRetailAmount() != null)
                                        .mapToDouble(t -> t.getReceivedQuantity() * t.getRetailAmount())
                                        .sum()).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    }

                }
                if (receivingInfoResponseV1.getTotalCostAmount() == null) {
                    receivingInfoResponseV1.setTotalCostAmount(defaultValuesConfigProperties.getTotalCostAmount());
                }
                if (receivingInfoResponseV1.getTotalRetailAmount() == null) {
                    receivingInfoResponseV1.setTotalRetailAmount(defaultValuesConfigProperties.getTotalRetailAmount());
                }
            } else {
                receivingInfoResponseV1.setTotalCostAmount(receiveSummary.getTotalCostAmount() != null ?
                        receiveSummary.getTotalCostAmount() : defaultValuesConfigProperties.getTotalCostAmount());
                receivingInfoResponseV1.setTotalRetailAmount(receiveSummary.getTotalRetailAmount() != null ?
                        receiveSummary.getTotalRetailAmount() : defaultValuesConfigProperties.getTotalRetailAmount());
            }


            receivingInfoResponseV1.setBottleDepositAmount(receiveSummary.getBottleDepositAmount() != null ?
                    receiveSummary.getBottleDepositAmount() : defaultValuesConfigProperties.getBottleDepositAmount());

            receivingInfoResponseV1.setControlSequenceNumber(receiveSummary.getControlSequenceNumber() != null ?
                    receiveSummary.getControlSequenceNumber() : defaultValuesConfigProperties.getControlSequenceNumber());
            receivingInfoResponseV1.setReceiptStatus(receiveSummary.getBusinessStatusCode() != null ? receiveSummary.getBusinessStatusCode().toString() : null);
            receivingInfoResponseV1.setDepartmentNumber(NumberUtils.isDigits(receiveSummary.getDepartmentNumber()) ?
                    Integer.parseInt(receiveSummary.getDepartmentNumber()) :
                    defaultValuesConfigProperties.getDepartmentNumber());
            receivingInfoResponseV1.setDivisionNumber(receiveSummary.getBaseDivisionNumber() != null ?
                    receiveSummary.getBaseDivisionNumber() : defaultValuesConfigProperties.getBaseDivisionNumber());
            receivingInfoResponseV1.setVendorNumber(receiveSummary.getVendorNumber());
            if (receivingInfoResponseV1.getOrigStoreNbr() == null) {
                receivingInfoResponseV1.setOrigStoreNbr(receiveSummary.getStoreNumber());
            }
            if (receivingInfoResponseV1.getParentReceivingDate() == null) {
                receivingInfoResponseV1.setParentReceivingDate(receiveSummary.getReceivingDate());
            }
            if (receiveSummary.getMerchandises() != null) {
                receivingInfoResponseV1.setMerchandises(new ArrayList<>(receiveSummary.getMerchandises().values()));
            }

        }
        if ( ! ( allRequestParams.get(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam()) == null  || allRequestParams.get(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam()).isEmpty() ) ||
                ! ( allRequestParams.get(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam()) == null || allRequestParams.get(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam()).isEmpty())
                || ( StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.LINENUMBERFLAG.getQueryParam()))
                && allRequestParams.get(ReceivingInfoRequestQueryParameters.LINENUMBERFLAG.getQueryParam()).equalsIgnoreCase("Y"))) {
            ReceivingUtils.updateLineResponse(lineResponseList);
            List<ReceivingInfoLineResponse> lineInfoList = lineResponseList.stream().map(t -> receivingInfoLineResponseConverter.convert(t)).collect(Collectors.toList());
            receivingInfoResponseV1.setReceivingInfoLineResponses(lineInfoList);
        }
        if (financialTxnResponseData != null) {
            updateReceivingInfoResponseV1(financialTxnResponseData, receivingInfoResponseV1);
        }
        return receivingInfoResponseV1;
    }

    private void updateReceivingInfoResponseV1(FinancialTxnResponseData financialTxnResponseData, ReceivingInfoResponseV1 receivingInfoResponseV1) {
        if (ReceivingConstants.PROCESS_STATUS_CODE_FOR_AUTH_FIELDS.contains(financialTxnResponseData.getProcessStatusCode())) {
            receivingInfoResponseV1.setAuthorizedBy(financialTxnResponseData.getAuthorizedBy());
            receivingInfoResponseV1.setAuthorizedDate(financialTxnResponseData.getAuthorizedDate() != null ?
                    financialTxnResponseData.getAuthorizedDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate() : null);
        }
        receivingInfoResponseV1.setDepartmentNumber(financialTxnResponseData.getPoDeptNbr());
        receivingInfoResponseV1.setDivisionNumber(financialTxnResponseData.getDivisionNumber());
        receivingInfoResponseV1.setVendorNumber(financialTxnResponseData.getVendorNumber());
        receivingInfoResponseV1.setMemo(financialTxnResponseData.getMemo());
        receivingInfoResponseV1.setVendorName(financialTxnResponseData.getVendorName());
        receivingInfoResponseV1.setParentReceivingNbr(financialTxnResponseData.getParentReceivingNbr());
        receivingInfoResponseV1.setParentReceivingStoreNbr(financialTxnResponseData.getParentReceivingStoreNbr());
        receivingInfoResponseV1.setParentReceivingDate(financialTxnResponseData.getParentReceivingDate() != null ?
                financialTxnResponseData.getParentReceivingDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate() : null);
        receivingInfoResponseV1.setParentPurchaseOrderId(financialTxnResponseData.getParentPurchaseOrderId());
        receivingInfoResponseV1.setInvoiceId(financialTxnResponseData.getInvoiceId());
        receivingInfoResponseV1.setInvoiceNumber(financialTxnResponseData.getInvoiceNumber());
        // Version V1 additional fields
        receivingInfoResponseV1.setReceiptNumber(financialTxnResponseData.getReceiveId());
        receivingInfoResponseV1.setLocationNumber(financialTxnResponseData.getOrigStoreNbr());
        receivingInfoResponseV1.setPurchaseOrderId(financialTxnResponseData.getPurchaseOrderId());
        if (financialTxnResponseData.getReceivingDate() != null) {
            receivingInfoResponseV1.setReceiptDate(financialTxnResponseData.getReceivingDate()
                    .toInstant().atZone(ZoneId.of("GMT")).toLocalDate());
        }
        receivingInfoResponseV1.setTransactionId(financialTxnResponseData.getTransactionId());
        receivingInfoResponseV1.setTxnSeqNbr(financialTxnResponseData.getTxnSeqNbr());
        receivingInfoResponseV1.setF6ASeqNbr(financialTxnResponseData.getF6ASeqNbr());
        receivingInfoResponseV1.setTransactionNbr(financialTxnResponseData.getTransactionNbr());
        receivingInfoResponseV1.setCountryCode(financialTxnResponseData.getCountryCode());
        receivingInfoResponseV1.setApCompanyId(financialTxnResponseData.getApCompanyId());
        receivingInfoResponseV1.setTxnRetailAmt(financialTxnResponseData.getTxnRetailAmt());
        receivingInfoResponseV1.setTxnCostAmt(financialTxnResponseData.getTotalCostAmount());
        receivingInfoResponseV1.setTxnDiscountAmt(financialTxnResponseData.getTxnDiscountAmt());
        receivingInfoResponseV1.setTxnAllowanceAmt(financialTxnResponseData.getTxnAllowanceAmt());
        receivingInfoResponseV1.setVendorDeptNbr(financialTxnResponseData.getDepartmentNumber());
        receivingInfoResponseV1.setPostDate(financialTxnResponseData.getPostDate() == null ? null : financialTxnResponseData.getPostDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate());
        receivingInfoResponseV1.setDueDate(financialTxnResponseData.getDueDate() == null ? null : financialTxnResponseData.getDueDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate());
        receivingInfoResponseV1.setPoNbr(financialTxnResponseData.getPoNumber());
        receivingInfoResponseV1.setTransactionDate(financialTxnResponseData.getTransactionDate() == null ? null : financialTxnResponseData.getTransactionDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate());
        receivingInfoResponseV1.setClaimNbr(financialTxnResponseData.getClaimNbr());
        receivingInfoResponseV1.setAccountNbr(financialTxnResponseData.getAccountNbr());
        receivingInfoResponseV1.setDeductTypeCode(financialTxnResponseData.getDeductTypeCode());
        receivingInfoResponseV1.setTxnBatchNbr(financialTxnResponseData.getTxnBatchNbr());
        receivingInfoResponseV1.setTxnControlNbr(financialTxnResponseData.getTxnControlNbr());
        receivingInfoResponseV1.setDeliveryNoteId(financialTxnResponseData.getDeliveryNoteId());
        receivingInfoResponseV1.setOrigStoreNbr(financialTxnResponseData.getOrigStoreNbr());
        receivingInfoResponseV1.setOrigDivNbr(financialTxnResponseData.getOrigDivNbr());
        receivingInfoResponseV1.setPoDcNbr(financialTxnResponseData.getPoDcNbr());
        receivingInfoResponseV1.setPoTypeCode(financialTxnResponseData.getPoTypeCode());
        receivingInfoResponseV1.setPoDeptNbr(financialTxnResponseData.getPoDeptNbr());
        receivingInfoResponseV1.setOffsetAccountNbr(financialTxnResponseData.getOffsetAccountNbr());
        receivingInfoResponseV1.setGrocinvoiceInd(financialTxnResponseData.getGrocinvoiceInd());
        receivingInfoResponseV1.setMatchDate(financialTxnResponseData.getMatchDate() == null ? null : financialTxnResponseData.getMatchDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate());
        receivingInfoResponseV1.setProcessStatusCode(financialTxnResponseData.getProcessStatusCode());
        receivingInfoResponseV1.setInvoiceFinTransProcessLogs(
                CollectionUtils.isEmpty(financialTxnResponseData.getInvoiceFinTransProcessLogs()) ? null : financialTxnResponseData.getInvoiceFinTransProcessLogs().stream().map(t ->
                        new InvoiceFinTransProcessLogs(
                                t.getActionIndicator(),
                                t.getMemoComments(),
                                t.getProcessStatusCode(),
                                t.getProcessStatusTimestamp() == null ? null : t.getProcessStatusTimestamp().toInstant().atZone(ZoneId.of("GMT")).toLocalDate(),
                                t.getStatusUserId()))
                        .collect(Collectors.toList()));
        receivingInfoResponseV1.setInvoiceFinTransAdjustLogs(
                CollectionUtils.isEmpty(financialTxnResponseData.getInvoiceFinTransAdjustLogs()) ? null : financialTxnResponseData.getInvoiceFinTransAdjustLogs().stream().map(t ->
                        new InvoiceFinTransAdjustLogs(t.getAdjustmentNbr(), t.getCostAdjustAmt(),
                                t.getCreateTs() == null ? null : t.getCreateTs().toInstant().atZone(ZoneId.of("GMT")).toLocalDate(),
                                t.getCreateUserId(),
                                t.getDueDate() == null ? null : t.getDueDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate(),
                                t.getOrigTxnCostAmt(),
                                t.getPostDate() == null ? null : t.getPostDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate(),
                                t.getTransactionDate() == null ? null : t.getTransactionDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate()))
                        .collect(Collectors.toList()));
        receivingInfoResponseV1.setInvoiceFinDelNoteChangeLogs(
                CollectionUtils.isEmpty(financialTxnResponseData.getInvoiceFinDelNoteChangeLogs()) ? null : financialTxnResponseData.getInvoiceFinDelNoteChangeLogs().stream().map(t ->
                        new InvoiceFinDelNoteChangeLogs(
                                t.getChangeTimestamp() == null ? null : t.getChangeTimestamp().toInstant().atZone(ZoneId.of("GMT")).toLocalDate(),
                                t.getChangeUserId()
                                , t.getDeliveryNoteId()
                                , t.getOrgDelNoteId()
                        ))
                        .collect(Collectors.toList()));
        receivingInfoResponseV1.setTxnTypeCode(financialTxnResponseData.getTxnTypeCode());
        receivingInfoResponseV1.setTxnTypeDesc(financialTxnResponseData.getTxnTypeDesc());
        receivingInfoResponseV1.setReceivingType(financialTxnResponseData.getReceivingType());
    }
    /*************************** Version 1 Methods ***********************************/

}