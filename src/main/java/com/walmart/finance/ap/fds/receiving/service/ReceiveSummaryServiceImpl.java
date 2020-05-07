package com.walmart.finance.ap.fds.receiving.service;

import com.mongodb.client.result.UpdateResult;
import com.walmart.finance.ap.fds.receiving.common.DB2SyncStatus;
import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.common.ReceivingUtils;
import com.walmart.finance.ap.fds.receiving.config.DefaultValuesConfigProperties;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
import com.walmart.finance.ap.fds.receiving.exception.*;
import com.walmart.finance.ap.fds.receiving.integrations.AdditionalResponse;
import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.model.*;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryLineValidator;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryValidator;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestQueryParameters;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.LOCATION_TYPE_WAREHOUSE;
import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.UOM_CODE_WH_EXCEPTION_RESOLUTION;

@Service
public class ReceiveSummaryServiceImpl implements ReceiveSummaryService {

    public static final Logger log = LoggerFactory.getLogger(ReceiveSummaryServiceImpl.class);

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ReceivingSummaryResponseConverter receivingSummaryResponseConverter;

    @Autowired
    ReceiveSummaryValidator receiveSummaryValidator;

    @Autowired
    ReceiveSummaryLineValidator receiveSummaryLineValidator;

    @Autowired
    private DefaultValuesConfigProperties defaultValuesConfigProperties;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Value("${azure.cosmosdb.collection.summary}")
    private String summaryCollection;

    @Value("${azure.cosmosdb.collection.line}")
    private String lineCollection;

    @Value("${azure.cosmosdb.collection.freight}")
    private String freightCollection;

    @Setter
    @Value("${months.per.shard}")
    private Integer monthsPerShard;

    @Setter
    @Value("${months.to.display}")
    private Integer monthsToDisplay;

    /**
     * Service layer to get the data based on the requested parameters and return pageable response.
     *
     * @param allRequestParams
     * @return
     */
    public ReceivingResponse getReceiveSummary(Map<String, String> allRequestParams) {
        List<ReceiveSummary> receiveSummaries;
        List<ReceivingSummaryResponse> responseList;
        try {
            receiveSummaries = getSearchCriteriaForGet(allRequestParams);
            log.info(ReceivingLogs.BEFORESIZESUMMARY.getParameterName() + receiveSummaries.size());
            if (CollectionUtils.isNotEmpty(receiveSummaries) && receiveSummaries.size() > 1000) {
                receiveSummaries.subList(1000, receiveSummaries.size()).clear();
            }
            log.info(ReceivingLogs.AFTERSIZESUMMARY.getParameterName() + receiveSummaries.size());
            if (CollectionUtils.isNotEmpty(receiveSummaries) && receiveSummaries.size() > 1000) {
                receiveSummaries.subList(1000, receiveSummaries.size()).clear();
            }
            //Todo parallel stream performance check
            if (CollectionUtils.isEmpty(receiveSummaries)) {
                throw new NotFoundException(ReceivingErrors.CONTENTNOTFOUNDSUMMARY.getParameterName(), ReceivingErrors.INVALIDQUERYPARAMS.getParameterName());
            } else {
               responseList = receiveSummaries.stream().map(
                        t -> {
                            ReceivingSummaryResponse response = receivingSummaryResponseConverter.convert(t);
                           return response;
                        }
                ).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(responseList)) {
                    throw new NotFoundException(ReceivingErrors.CONTENTNOTFOUNDSUMMARY.getParameterName(), ReceivingErrors.INVALIDQUERYPARAMS.getParameterName());
                }
                ReceivingResponse successMessage = new ReceivingResponse();
                successMessage.setData(responseList);
                successMessage.setSuccess(true);
                successMessage.setTimestamp(LocalDateTime.now());
                return successMessage;
            }
        } catch (NumberFormatException e) {
            log.error(ExceptionUtils.getStackTrace(e));//TODO
            throw new BadRequestException(ReceivingErrors.INVALIDDATATYPE.getParameterName(), ReceivingErrors.INVALIDQUERYPARAMS.getParameterName());
        }
    }

    /*    private String formulateId(String controlNumber, String receiptNumber, String locationNumber, String receiptDate) {
            return controlNumber + ReceivingConstants.PIPE_SEPARATOR + receiptNumber + ReceivingConstants.PIPE_SEPARATOR + locationNumber + ReceivingConstants.PIPE_SEPARATOR + receiptDate;

        }*/
    private String formulateId(String receivingControlNumber, String poReceiveId, String locationNumber, String MDSReceiveDate) {
        return receivingControlNumber + ReceivingConstants.PIPE_SEPARATOR + poReceiveId + ReceivingConstants.PIPE_SEPARATOR + locationNumber + ReceivingConstants.PIPE_SEPARATOR + MDSReceiveDate;
    }

    /*******  Search Criteria methods  *********/

    private List<ReceiveSummary> getSearchCriteriaForGet(Map<String, String> paramMap) {
        log.info(ReceivingLogs.SEARCHCRITERIAFORGET.getParameterName());
        Query query = searchCriteriaForGet(paramMap);
        return executeQueryForReceiveSummary(query);
    }


    private Query searchCriteriaForGet(Map<String, String> paramMap) {
        Query dynamicQuery = new Query();

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.CONTROLNUMBER))) {
            Criteria controlNumberCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(paramMap.get(ReceivingConstants.CONTROLNUMBER));
            dynamicQuery.addCriteria(controlNumberCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.PURCHASEORDERID))) {
            Criteria purchaseOrderIdCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.PURCHASEORDERID.getParameterName()).is(Long.valueOf(paramMap.get(ReceivingConstants.PURCHASEORDERID)));
            dynamicQuery.addCriteria(purchaseOrderIdCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.DIVISIONNUMBER))) {
            Criteria baseDivisionNumberCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.BASEDIVISIONNUMBER.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.DIVISIONNUMBER)));
            dynamicQuery.addCriteria(baseDivisionNumberCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.RECEIPTDATESTART)) && StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.RECEIPTDATEEND))) {
            LocalDateTime startDate = getDate(paramMap.get(ReceivingConstants.RECEIPTDATESTART) + " 00:00:00");
            LocalDateTime endDate = getDate(paramMap.get(ReceivingConstants.RECEIPTDATEEND) + " 23:59:59");
            String applicableDateField = ReceiveSummaryCosmosDBParameters.DATERECEIVED.getParameterName();
            if (paramMap.containsKey(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam()) &&
                    paramMap.get(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam()).equals(LOCATION_TYPE_WAREHOUSE)) {
                applicableDateField = ReceiveSummaryCosmosDBParameters.RECEIVEPROCESSDATE.getParameterName();
            }
            Criteria mdsReceiveDateCriteria = Criteria.where(applicableDateField).gte(startDate).lte(endDate);
            dynamicQuery.addCriteria(mdsReceiveDateCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.TRANSACTIONTYPE))) {
            Criteria transactionTypeCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.TRANSACTIONTYPE.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.TRANSACTIONTYPE)));
            dynamicQuery.addCriteria(transactionTypeCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.LOCATIONNUMBER))) {
            ReceivingUtils.updateQueryForPartitionKey(null, paramMap,
                    Integer.valueOf(paramMap.get(ReceivingConstants.LOCATIONNUMBER)), dynamicQuery, monthsPerShard,monthsToDisplay);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.PURCHASEORDERNUMBER))) {
            Criteria purchaseOrderNumberCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.PURCHASEORDERNUMBER.getParameterName()).is(paramMap.get(ReceivingConstants.PURCHASEORDERNUMBER));
            dynamicQuery.addCriteria(purchaseOrderNumberCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.RECEIPTNUMBERS))) {
            Criteria poReceiveIdCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.RECEIVEID.getParameterName()).in(paramMap.get(ReceivingConstants.RECEIPTNUMBERS).split(","));
            dynamicQuery.addCriteria(poReceiveIdCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.DEPARTMENTNUMBER))) {
            Criteria departmentNumberCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.DEPARTMENTNUMBER.getParameterName()).is(paramMap.get(ReceivingConstants.DEPARTMENTNUMBER));
            dynamicQuery.addCriteria(departmentNumberCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.VENDORNUMBER))) {
            Criteria vendorNumberCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.VENDORNUMBER.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.VENDORNUMBER)));
            dynamicQuery.addCriteria(vendorNumberCriteria);
        }
        log.info("query: " + dynamicQuery);
        return dynamicQuery;
    }

    private LocalDateTime getDate(String date) {
        try {
            DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern(ReceivingConstants.DATEFORMATTER);
            return LocalDateTime.parse(date, formatterDate);
        } catch (DateTimeParseException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new BadRequestException(ReceivingErrors.INVALIDDATATYPE.getParameterName(), ReceivingErrors.INVALIDQUERYPARAMS.getParameterName());
        }
    }
    /*******  Search Criteria methods  *********/

    /******* receive -line data fetching   *********/

    private Map<String, AdditionalResponse> getLineResponseMap(List<ReceiveSummary> receiveSummaries, Map<String, String> allRequestParams) {
        Map<String, AdditionalResponse> lineResponseMap = new HashMap<>();
        List<ReceivingLine> lineResponseList = new LinkedList<>();
        List<Criteria> criteriaList = new ArrayList<>();
        List<String> itemNumbers = allRequestParams.containsKey(ReceiveSummaryRequestParams.ITEMNUMBERS.getParameterName()) ? Arrays.asList(allRequestParams.get(ReceiveSummaryRequestParams.ITEMNUMBERS.getParameterName()).split(",")) : null;
        List<String> upcNumbers = allRequestParams.containsKey(ReceiveSummaryRequestParams.UPCNUMBERS.getParameterName()) ? Arrays.asList(allRequestParams.get(ReceiveSummaryRequestParams.UPCNUMBERS.getParameterName()).split(",")) : null;
        Set<String> partitionNumbers = new HashSet<>();
        List<String> summaryReferences = new ArrayList<>();
        for (ReceiveSummary receiveSummary : receiveSummaries) {
            partitionNumbers.addAll(ReceivingUtils
                    .getPartitionKeyList(null, allRequestParams, receiveSummary.getStoreNumber(), monthsPerShard, monthsToDisplay));
            summaryReferences.add(receiveSummary.get_id());
        }



        Criteria criteriaDefinition = queryForLineResponse(partitionNumbers, itemNumbers, upcNumbers,
                summaryReferences);

        if (criteriaDefinition != null) {
            Query query = new Query(criteriaDefinition);
            log.info("query: " + query);
            lineResponseList = executeQueryReceiveline(query);
        }
        Map<String, List<ReceivingLine>> receivingLineMap = new HashMap<>();
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

        Iterator<ReceiveSummary> iterator = receiveSummaries.iterator();
        while (iterator.hasNext()) {
            ReceiveSummary receiveSummary = iterator.next();
            AdditionalResponse response = new AdditionalResponse();
            List<ReceivingLine> lineList = receivingLineMap.get(receiveSummary.get_id());
            if (CollectionUtils.isNotEmpty(lineList)) {
                if (receiveSummary.getTypeIndicator().equals('W')) {
                    if (lineList.get(0).getPoLineValue() != null && !lineList.get(0).getPoLineValue().isEmpty()) {
                        response.setTotalCostAmount(BigDecimal.valueOf(lineList.stream()
                                .filter(t -> t.getPoLineValue().containsKey(UOM_CODE_WH_EXCEPTION_RESOLUTION) &&
                                        t.getReceivedQuantity() != null &&
                                        t.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION).getCostAmount() != null)
                                .mapToDouble(t -> t.getReceivedQuantity() *
                                        t.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION).getCostAmount())
                                .sum()).setScale(2, RoundingMode.HALF_UP).doubleValue());
                        response.setTotalRetailAmount(BigDecimal.valueOf(
                                lineList.stream()
                                        .filter(t -> t.getPoLineValue().containsKey(UOM_CODE_WH_EXCEPTION_RESOLUTION) &&
                                                t.getReceivedQuantity() != null &&
                                                t.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION).getRetailAmount() != null)
                                        .mapToDouble(t -> t.getReceivedQuantity() *
                                                t.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION).getRetailAmount())
                                        .sum()).setScale(2, RoundingMode.HALF_UP).doubleValue());
                    } else {
                        response.setTotalCostAmount(lineList.stream().mapToDouble(t -> t.getReceivedQuantity() * t.getCostAmount()).sum());
                        response.setTotalRetailAmount(lineList.stream().mapToDouble(t -> t.getReceivedQuantity() * t.getRetailAmount()).sum());
                    }
                } else {
                    response.setTotalCostAmount(receiveSummary.getTotalCostAmount() != null ?
                            receiveSummary.getTotalCostAmount() : defaultValuesConfigProperties.getTotalCostAmount());
                    response.setTotalRetailAmount(receiveSummary.getTotalRetailAmount() != null ?
                            receiveSummary.getTotalRetailAmount() : defaultValuesConfigProperties.getTotalRetailAmount());
                }
                response.setLineCount((long) lineList.size());
                lineResponseMap.put(receiveSummary.get_id(), response);
            } else if (CollectionUtils.isNotEmpty(itemNumbers) || CollectionUtils.isNotEmpty(upcNumbers)) {
                iterator.remove();
            } else {
                response.setTotalCostAmount(receiveSummary.getTotalCostAmount()!= null ?
                        receiveSummary.getTotalCostAmount() : defaultValuesConfigProperties.getTotalCostAmount());
                response.setTotalRetailAmount(receiveSummary.getTotalRetailAmount() != null ?
                        receiveSummary.getTotalRetailAmount() : defaultValuesConfigProperties.getTotalRetailAmount());
                lineResponseMap.put(receiveSummary.get_id(), response);
            }
        }
        return lineResponseMap;
    }

    private Criteria queryForLineResponse(Set<String> partitionNumbers, List<String> itemNumbers, List<String> upcNumbers, List<String> summaryReferences) {
        Criteria criteriaDefinition = new Criteria();
        if (CollectionUtils.isNotEmpty(partitionNumbers)) {
            criteriaDefinition.and(ReceivingConstants.RECEIVING_SHARD_KEY_FIELD).in(partitionNumbers);
        }
        if (CollectionUtils.isNotEmpty(summaryReferences)) {
            criteriaDefinition.and(ReceivingLineParameters.SUMMARYREFERENCE.getParameterName()).in(summaryReferences);
        }
        if (CollectionUtils.isNotEmpty(itemNumbers)) {
            criteriaDefinition.and(ReceivingLineParameters.ITEMNUMBER.getParameterName()).in(itemNumbers.stream().map(Long::parseLong).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(upcNumbers)) {
            criteriaDefinition.and(ReceivingLineParameters.UPCNUMBER.getParameterName()).in(upcNumbers);
        }
        return criteriaDefinition;
    }

    /******* Common Methods  *********/

    private List<ReceiveSummary> executeQueryForReceiveSummary(Query query) {
        List<ReceiveSummary> receiveSummaries = new ArrayList<>();
        if (query != null) {
            long startTime = System.currentTimeMillis();
            receiveSummaries = mongoTemplate.find(query.limit(1000), ReceiveSummary.class, summaryCollection);
            log.info("executeQueryForReceiveSummary :: queryTime :: " + (System.currentTimeMillis() - startTime));
        }
        return receiveSummaries;
    }

    private List<ReceivingLine> executeQueryReceiveline(Query query) {
        List<ReceivingLine> receiveLines = new LinkedList<>();
        if (query != null) {
            long startTime = System.currentTimeMillis();
            receiveLines = mongoTemplate.find(query, ReceivingLine.class, lineCollection);
            log.info("executeQueryReceiveline :: queryTime :: " + (System.currentTimeMillis() - startTime));
        }
        return receiveLines;
    }

    private FreightResponse executeQueryReceiveFreight(Long id) {
         Long startTime = System.currentTimeMillis();
        FreightResponse freightResponses = mongoTemplate.findById(id, FreightResponse.class, freightCollection);
        log.info("executeQueryReceiveFreight :: queryTime :: " + (System.currentTimeMillis() - startTime));
        return freightResponses;
    }

    private void listToMapConversion(List<ReceiveSummary> receiveSummaries, HashMap<String, ReceiveSummary> receiveSummaryHashMap) {
        receiveSummaries.stream().forEach(t -> receiveSummaryHashMap.put(t.get_id(), t));
    }

    /******* Common Methods  *********/

    @Override
    @Transactional
    public ReceivingResponse updateReceiveSummary(ReceivingSummaryRequest receivingSummaryRequest, String countryCode) {
        log.info("unitOfWorkId:" + receivingSummaryRequest.getMeta().getUnitOfWorkId());
        Boolean isWareHouseData = receiveSummaryValidator.isWareHouseData(receivingSummaryRequest.getMeta().getSorRoutingCtx());
        receiveSummaryValidator.validateBusinessStatUpdateSummary(receivingSummaryRequest.getBusinessStatusCode());
        String id = formulateId(receivingSummaryRequest.getPurchaseOrderId(), receivingSummaryRequest.getReceiptNumber(), receivingSummaryRequest.getLocationNumber().toString(), isWareHouseData ? "0" : receivingSummaryRequest.getReceiptDate().toString());
        Query dynamicQuery = new Query();
        dynamicQuery.addCriteria(Criteria.where(ReceiveSummaryParameters.ID.getParameterName()).is(id));
        addShardKeyQuery(dynamicQuery, String.valueOf(receivingSummaryRequest.getLocationNumber()),
                receivingSummaryRequest.getReceiptDate());
        Update update = new Update();
        update.set(ReceiveSummaryParameters.BUSINESSSTATUSCODE.getParameterName(), receivingSummaryRequest.getBusinessStatusCode().charAt(0));
        update.set(ReceiveSummaryParameters.DATASYNCSTATUS.getParameterName(), DB2SyncStatus.UPDATE_SYNC_INITIATED);
        update.set(ReceiveSummaryParameters.LASTUPDATEDDATE.getParameterName(), LocalDateTime.now());
        long startTime = System.currentTimeMillis();
        ReceiveSummary commitedRcvSummary = mongoTemplate.findAndModify(dynamicQuery, update, FindAndModifyOptions.options().returnNew(true), ReceiveSummary.class, summaryCollection);
        log.info("updateReceiveSummary :: updateSummaryQueryTime :: " + (System.currentTimeMillis() - startTime));
        if (commitedRcvSummary == null) {
            throw new ContentNotFoundException(ReceivingErrors.CONTENTNOTFOUNDSUMMARY.getParameterName(), ReceivingErrors.VALIDID.getParameterName());
        }
        if (Objects.nonNull(commitedRcvSummary) && isWareHouseData) {
            publisher.publishEvent(receivingSummaryRequest);
        }
        List<ReceivingSummaryRequest> responseList = new ArrayList<>();
        responseList.add(receivingSummaryRequest);
        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setTimestamp(LocalDateTime.now());
        successMessage.setData(responseList);
        successMessage.setSuccess(true);
        return successMessage;
    }

    @Override
    @Transactional
    public ReceivingResponse updateReceiveSummaryAndLine(ReceivingSummaryLineRequest receivingSummaryLineRequest, String countryCode) {
        log.info("unitOfWorkId:" + receivingSummaryLineRequest.getMeta().getUnitOfWorkId());
        Boolean isWareHouseData = receiveSummaryValidator.isWareHouseData(receivingSummaryLineRequest.getMeta().getSorRoutingCtx());

        receiveSummaryValidator.validateBusinessStatUpdateSummary(receivingSummaryLineRequest.getBusinessStatusCode());
        receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest);
        receiveSummaryLineValidator.validateReceiptLineNumber(receivingSummaryLineRequest.getReceiptLineNumber());
        String id = formulateId(receivingSummaryLineRequest.getPurchaseOrderId(), receivingSummaryLineRequest.getReceiptNumber(), receivingSummaryLineRequest.getLocationNumber().toString(), isWareHouseData ? "0" : receivingSummaryLineRequest.getReceiptDate().toString());
        Query query = new Query();
        query.addCriteria(Criteria.where(ReceiveSummaryParameters.ID.getParameterName()).is(id));
        addShardKeyQuery(query, String.valueOf(receivingSummaryLineRequest.getLocationNumber()),
                receivingSummaryLineRequest.getReceiptDate());
        Update update = new Update();
        update.set(ReceiveSummaryParameters.DATASYNCSTATUS.getParameterName(), DB2SyncStatus.UPDATE_SYNC_INITIATED);
        update.set(ReceiveSummaryParameters.LASTUPDATEDDATE.getParameterName(), LocalDateTime.now(ZoneId.of("UTC")));
        update.set(ReceiveSummaryParameters.BUSINESSSTATUSCODE.getParameterName(), receivingSummaryLineRequest.getBusinessStatusCode().charAt(0));
        long startTime = System.currentTimeMillis();
        ReceiveSummary commitedRcvSummary = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), ReceiveSummary.class, summaryCollection);
        log.info("updateReceiveSummaryAndLine :: updateSummaryQueryTime :: " + (System.currentTimeMillis() - startTime));
        if (commitedRcvSummary == null) {
            throw new ContentNotFoundException(ReceivingErrors.CONTENTNOTFOUNDSUMMARY.getParameterName(), ReceivingErrors.VALIDID.getParameterName());
        }
        Update updateLine = new Update();
        updateLine.set(ReceivingLineParameters.DATASYNCSTATUS.getParameterName(), DB2SyncStatus.UPDATE_SYNC_INITIATED);
        updateLine.set(ReceivingLineParameters.LASTUPDATEDDATE.getParameterName(), LocalDateTime.now(ZoneId.of("UTC")));
        updateLine.set(ReceivingLineParameters.INVENTORYMATCHSTATUS.getParameterName(), Integer.parseInt(receivingSummaryLineRequest.getInventoryMatchStatus()));
        if (StringUtils.isNotEmpty(receivingSummaryLineRequest.getReceiptLineNumber())) {
            String lineId = id + ReceivingConstants.PIPE_SEPARATOR + receivingSummaryLineRequest.getReceiptLineNumber().toString();
            Query queryForLine = new Query();
            queryForLine.addCriteria(Criteria.where(ReceivingLineParameters.ID.getParameterName()).is(lineId));
            addShardKeyQuery(queryForLine, String.valueOf(receivingSummaryLineRequest.getLocationNumber()),
                    receivingSummaryLineRequest.getReceiptDate());
            startTime = System.currentTimeMillis();
            ReceivingLine commitedRcvLine = mongoTemplate.findAndModify(queryForLine, updateLine, FindAndModifyOptions.options().returnNew(true), ReceivingLine.class, lineCollection);
            log.info("updateReceiveSummaryAndLine :: updateLineQueryTime :: findAndModify " + (System.currentTimeMillis() - startTime));

        } else {
            Query queryForLine = new Query();
            queryForLine.addCriteria(Criteria.where(ReceivingLineParameters.SUMMARYREFERENCE.getParameterName()).is(id));
            addShardKeyQuery(queryForLine, String.valueOf(receivingSummaryLineRequest.getLocationNumber()),
                    receivingSummaryLineRequest.getReceiptDate());
            startTime = System.currentTimeMillis();
            UpdateResult updateResult = mongoTemplate.updateMulti(queryForLine, updateLine, ReceivingLine.class, lineCollection);
            long endTime = System.currentTimeMillis();
            List<ReceivingLine> receivingLines = mongoTemplate.find(queryForLine, ReceivingLine.class, lineCollection);
            log.info("updateReceiveSummaryAndLine :: updateLineQueryTime :: multipleUpdate " + (endTime - startTime));
            log.info("updateReceiveSummaryAndLine :: updateLineQueryTime :: find " + (System.currentTimeMillis() - endTime));
        }

        List<ReceivingSummaryLineRequest> responseList = new ArrayList<>();
        responseList.add(receivingSummaryLineRequest);
        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setSuccess(true);
        successMessage.setData(responseList);
        successMessage.setTimestamp(LocalDateTime.now());
        if(isWareHouseData)
            publisher.publishEvent(receivingSummaryLineRequest);
        return successMessage;
    }


    private void addShardKeyQuery(Query query, String keyAttributeValue, LocalDate dateForPartitionKey) {
        Criteria partitionKeyCriteria =
                Criteria.where(ReceivingConstants.RECEIVING_SHARD_KEY_FIELD)
                    .in(ReceivingUtils.getPartitionKeyList(keyAttributeValue, dateForPartitionKey, monthsToDisplay,
                            monthsPerShard));
        query.addCriteria(partitionKeyCriteria);
    }

}