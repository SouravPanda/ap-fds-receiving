package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.common.ReceivingUtils;
import com.walmart.finance.ap.fds.receiving.config.DefaultValuesConfigProperties;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import com.walmart.finance.ap.fds.receiving.exception.ReceivingErrors;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnIntegrationService;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnResponseData;
import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummaryCosmosDBParameters;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLineParameters;
import com.walmart.finance.ap.fds.receiving.response.*;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestQueryParameters;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer to get the data from financial transaction API and respond with model response.
 */
@Service
public class ReceivingInfoServiceImpl implements ReceivingInfoService {
    public static final Logger log = LoggerFactory.getLogger(ReceivingInfoServiceImpl.class);

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

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private DefaultValuesConfigProperties defaultValuesConfigProperties;

    @Autowired
    FinancialTxnIntegrationService financialTxnIntegrationService;

    /**
     * @param allRequestParams
     * @return
     */
    @Override
    public ReceivingResponse getInfoSeviceData(Map<String, String> allRequestParams) {
        // First Fin Txn + Cosmos
        List<FinancialTxnResponseData> financialTxnResponseDataList = financialTxnIntegrationService.getFinancialTxnDetails(allRequestParams);
        List<ReceivingInfoResponse> receivingInfoResponses = getDataForFinancialTxn(financialTxnResponseDataList, allRequestParams);
        if (CollectionUtils.isEmpty(receivingInfoResponses)) {
            throw new NotFoundException("Receiving data not found for given search criteria.", "please enter valid query parameters");
        }
        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(receivingInfoResponses);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.now());
        return successMessage;
    }

    /*************************** Financial-Txn Logic : START ***************************/
    private List<ReceivingInfoResponse> getDataForFinancialTxn(List<FinancialTxnResponseData> financialTxnResponseDataList, Map<String, String> allRequestParams) {
        List<ReceivingInfoResponse> receivingInfoResponses = new ArrayList<>();
        for (FinancialTxnResponseData financialTxnResponseData : financialTxnResponseDataList) {
            List<ReceiveSummary> receiveSummaries = getSummaryData(financialTxnResponseData, allRequestParams);
            if (CollectionUtils.isNotEmpty(receiveSummaries)) {
                List<ReceivingLine> lineResponseList = getLineData(receiveSummaries.get(0), allRequestParams);
                FreightResponse freightResponse = getFreightData(receiveSummaries.get(0));
                ReceivingInfoResponse receivingInfoResponse = conversionToReceivingInfo(receiveSummaries.get(0), financialTxnResponseData, lineResponseList, freightResponse, allRequestParams);
                receivingInfoResponses.add(receivingInfoResponse);
            }
        }
        return receivingInfoResponses;
    }

    private List<ReceiveSummary> getSummaryData(FinancialTxnResponseData financialTxnResponseData, Map<String, String> allRequestParams) {
        Integer storeNumber = (financialTxnResponseData.getOrigStoreNbr() == null || financialTxnResponseData.getOrigStoreNbr() == 0)
                ? financialTxnResponseData.getStoreNumber() : financialTxnResponseData.getOrigStoreNbr();
        String id = (financialTxnResponseData.getPurchaseOrderId() == null ? 0 : financialTxnResponseData.getPurchaseOrderId()) + ReceivingConstants.PIPE_SEPARATOR
                + (StringUtils.isEmpty(financialTxnResponseData.getReceiveId()) ? "0" : financialTxnResponseData.getReceiveId()) + ReceivingConstants.PIPE_SEPARATOR
                + (storeNumber == null ? 0 : storeNumber) + ReceivingConstants.PIPE_SEPARATOR
                + (financialTxnResponseData.getReceivingDate() == null ? "0" : financialTxnResponseData.getReceivingDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate());
        Query query = new Query();
        CriteriaDefinition criteriaDefinition = null;
        if (StringUtils.isNotEmpty(id) && !id.equalsIgnoreCase("0|0|0|0")) {
            criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.ID.getParameterName()).is(id);
            query.addCriteria(criteriaDefinition);
        }
        if (storeNumber != null) {
            LocalDate receivingDate = null;
            if (financialTxnResponseData.getReceivingDate() != null) {
                receivingDate =
                        financialTxnResponseData.getReceivingDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate();
            }
            ReceivingUtils.updateQueryForPartitionKey(receivingDate, allRequestParams, storeNumber, query,
                    monthsPerShard, monthsToDisplay);
        }
        if (StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam()))
                && StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam()))) {
            LocalDateTime startDate = getDate(allRequestParams.get(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam()) + " 00:00:00");
            LocalDateTime endDate = getDate(allRequestParams.get(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam()) + " 23:59:59");
            if (endDate.isAfter(startDate)) {
                criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.DATERECEIVED.getParameterName()).
                        gte(startDate).
                        lte(endDate);
                query.addCriteria(criteriaDefinition);
            } else {
                throw new BadRequestException("Receipt end date should be greater than receipt start date.", "Please enter valid query parameters");
            }
        }
        log.info("queryForSummaryResponse :: Query is " + query);
        return executeQueryInSummary(query);
    }

    private List<ReceiveSummary> getSummaryData(Map<String, String> allRequestParams) {
        Query query = searchCriteriaForGet(allRequestParams);
        log.info("queryForSummaryResponse :: Query is " + query);
        return executeQueryInSummary(query);
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
            Criteria mdsReceiveDateCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.DATERECEIVED.getParameterName()).gte(startDate).lte(endDate);
            dynamicQuery.addCriteria(mdsReceiveDateCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.TRANSACTIONTYPE))) {
            Criteria transactionTypeCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.TRANSACTIONTYPE.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.TRANSACTIONTYPE)));
            dynamicQuery.addCriteria(transactionTypeCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.LOCATIONNUMBER))) {
            ReceivingUtils.updateQueryForPartitionKey(null, paramMap,
                    Integer.valueOf(paramMap.get(ReceivingConstants.LOCATIONNUMBER)), dynamicQuery, monthsPerShard, monthsToDisplay);
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
        // log.info("query: " + dynamicQuery);
        return dynamicQuery;
    }

    /**
     * Put the null check for date parameter befor calling this method
     *
     * @param date
     * @return
     */
    private LocalDateTime getDate(String date) {
        try {
            DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern(ReceivingConstants.DATEFORMATTER);
            return LocalDateTime.parse(date, formatterDate);
        } catch (DateTimeParseException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new BadRequestException(ReceivingErrors.INVALIDDATATYPE.getParameterName(), ReceivingErrors.INVALIDQUERYPARAMS.getParameterName());
        }
    }
    /*************************** Financial-Txn Logic : END ***************************/

    /*************************** General Methods ***********************************/

    private List<ReceiveSummary> executeQueryInSummary(Query query) {
        List<ReceiveSummary> receiveSummaries = new ArrayList<>();
        if (query != null) {
            long startTime = System.currentTimeMillis();
            receiveSummaries = mongoTemplate.find(query.limit(1000), ReceiveSummary.class, summaryCollection);
            log.info(" executeQueryInSummary :: queryTime :: " + (System.currentTimeMillis() - startTime));
        }
        return receiveSummaries;
    }

    private List<ReceivingLine> executeQueryInLine(Query query) {
        List<ReceivingLine> receiveLines = new ArrayList<>();
        if (query != null) {
            long startTime = System.currentTimeMillis();
            receiveLines = mongoTemplate.find(query.limit(1000), ReceivingLine.class, lineCollection);
            log.info(" executeQueryInLine :: queryTime :: " + (System.currentTimeMillis() - startTime));
        }
        return receiveLines;
    }

    private FreightResponse executeQueryInFreight(Long id) {
        FreightResponse receiveFreight = null;
        if (id != null) {
            long startTime = System.currentTimeMillis();
            receiveFreight = mongoTemplate.findById(id, FreightResponse.class, freightCollection);
            log.info(" executeQueryInFreight :: queryTime :: " + (System.currentTimeMillis() - startTime));
        }
        return receiveFreight;
    }
    /*************************** General Methods ***********************************/

    /*************************** receive-line data ***************************/
    private List<ReceivingLine> getLineData(ReceiveSummary receiveSummary, Map<String, String> allRequestParams) {
        Query query = new Query();
        CriteriaDefinition criteriaDefinition = null;
        if (StringUtils.isNotEmpty(receiveSummary.get_id())) {
            criteriaDefinition = Criteria.where(ReceivingLineParameters.SUMMARYREFERENCE.getParameterName()).is(receiveSummary.get_id());
            query.addCriteria(criteriaDefinition);
        }
        if (receiveSummary.getStoreNumber() != null) {
            ReceivingUtils.updateQueryForPartitionKey(null, allRequestParams, receiveSummary.getStoreNumber(), query,
                    monthsPerShard, monthsToDisplay);
        }
        if (StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam()))) {
            List<String> itemNumbers = Arrays.asList(allRequestParams.get(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam()).split(","));
            criteriaDefinition =
                    Criteria.where(ReceivingLineParameters.ITEMNUMBER.getParameterName()).in(itemNumbers.stream().map(Long::parseLong).collect(Collectors.toList()));
            query.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam()))) {
            List<String> upcNumberList =
                    Arrays.asList(allRequestParams.get(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam()).split(","));
            List<String> updatedUpcNumberList = new ArrayList<>();
            /*
             * Change 13 Digit UPC Number to 16 Digit GTIN Number while hitting line
             * Combination 1 : Add "00" to beginning and "0" to the end
             * Combination 2 : Add "000" to the beginning
             */
            for (String upcNumber : upcNumberList) {
                updatedUpcNumberList.add("00" + upcNumber + "0");
                updatedUpcNumberList.add("000" + upcNumber);
            }
            criteriaDefinition = Criteria.where(ReceivingLineParameters.UPCNUMBER.getParameterName()).in(updatedUpcNumberList);
            query.addCriteria(criteriaDefinition);
        }
        log.info("queryForLineResponse :: Query is " + query);
        return executeQueryInLine(criteriaDefinition == null ? null : query);
    }
    /******* receive-line data   *********/

    /******* receive-freight data *********/
    private FreightResponse getFreightData(ReceiveSummary receiveSummary) {
        return executeQueryInFreight(receiveSummary.getFreightBillExpandId());
    }
    /******* receive-freight data   *********/

    /*************************** Conversion Methods ***********************************/
    private ReceivingInfoResponse conversionToReceivingInfo(ReceiveSummary receiveSummary, FinancialTxnResponseData financialTxnResponseData, List<ReceivingLine> lineResponseList, FreightResponse freightResponse, Map<String, String> allRequestParams) {
        ReceivingInfoResponse receivingInfoResponse = new ReceivingInfoResponse();
        if (financialTxnResponseData != null) {
            receivingInfoResponse.setAuthorizedBy(financialTxnResponseData.getAuthorizedBy());
            receivingInfoResponse.setAuthorizedDate(financialTxnResponseData.getAuthorizedDate() != null ?
                    financialTxnResponseData.getAuthorizedDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate() : null);
            receivingInfoResponse.setDepartmentNumber(financialTxnResponseData.getDepartmentNumber());
            receivingInfoResponse.setDivisionNumber(financialTxnResponseData.getDivisionNumber());
            receivingInfoResponse.setVendorNumber(financialTxnResponseData.getVendorNumber());
            receivingInfoResponse.setMemo(financialTxnResponseData.getMemo());
            receivingInfoResponse.setVendorName(financialTxnResponseData.getVendorName());
            receivingInfoResponse.setParentReceivingNbr(financialTxnResponseData.getParentReceivingNbr());
            receivingInfoResponse.setParentReceivingStoreNbr(financialTxnResponseData.getParentReceivingStoreNbr());
            receivingInfoResponse.setParentReceivingDate(financialTxnResponseData.getParentReceivingDate() != null ?
                    financialTxnResponseData.getParentReceivingDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate() : null);
            receivingInfoResponse.setParentPurchaseOrderId(financialTxnResponseData.getParentPurchaseOrderId());
            receivingInfoResponse.setInvoiceId(financialTxnResponseData.getInvoiceId());
            receivingInfoResponse.setInvoiceNumber(financialTxnResponseData.getInvoiceNumber());
        }
        receivingInfoResponse.setLineCount(CollectionUtils.isNotEmpty(lineResponseList) ?
                new Long(lineResponseList.size()) : defaultValuesConfigProperties.getLineCount());
        receivingInfoResponse.setCarrierCode(freightResponse != null
                && StringUtils.isNotEmpty(freightResponse.getCarrierCode()) ? freightResponse.getCarrierCode() :
                defaultValuesConfigProperties.getCarrierCode());
        receivingInfoResponse.setTrailerNumber(freightResponse != null
                && StringUtils.isNotEmpty(freightResponse.getTrailerNbr()) ? freightResponse.getTrailerNbr() :
                defaultValuesConfigProperties.getTrailerNbr());
        receivingInfoResponse.setControlNumber(StringUtils.isNotEmpty(receiveSummary.getReceivingControlNumber()) ?
                receiveSummary.getReceivingControlNumber() : defaultValuesConfigProperties.getReceivingControlNumber());
        receivingInfoResponse.setTransactionType(receiveSummary.getTransactionType());
        receivingInfoResponse.setLocationNumber(receiveSummary.getStoreNumber());
        receivingInfoResponse.setPurchaseOrderId(receiveSummary.getPurchaseOrderId());
        receivingInfoResponse.setReceiptDate(receiveSummary.getDateReceived().atZone(ZoneId.of("GMT")).toLocalDate());
        receivingInfoResponse.setReceiptNumber(StringUtils.isNotEmpty(receiveSummary.getReceiveId()) ?
                receiveSummary.getReceiveId() : "0");
        if (receiveSummary.getTypeIndicator().equals('W') && CollectionUtils.isNotEmpty(lineResponseList)) {
            receivingInfoResponse.setTotalCostAmount(BigDecimal.valueOf(lineResponseList.stream()
                    .filter(t -> t.getReceivedQuantity() != null && t.getCostAmount() != null)
                    .mapToDouble(t -> t.getReceivedQuantity() * t.getCostAmount())
                    .sum()).setScale(2, RoundingMode.HALF_UP).doubleValue());
            receivingInfoResponse.setTotalRetailAmount(BigDecimal.valueOf(lineResponseList.stream()
                    .filter(t -> t.getReceivedQuantity() != null && t.getRetailAmount() != null)
                    .mapToDouble(t -> t.getReceivedQuantity() * t.getRetailAmount())
                    .sum()).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
        receivingInfoResponse.setTotalCostAmount(receiveSummary.getTotalCostAmount() != null ?
                receiveSummary.getTotalCostAmount() : defaultValuesConfigProperties.getTotalCostAmount());
        receivingInfoResponse.setTotalRetailAmount(receiveSummary.getTotalRetailAmount() != null ?
                receiveSummary.getTotalRetailAmount() : defaultValuesConfigProperties.getTotalRetailAmount());
        receivingInfoResponse.setBottleDepositAmount(receiveSummary.getBottleDepositAmount() != null ?
                receiveSummary.getBottleDepositAmount() : defaultValuesConfigProperties.getBottleDepositAmount());
        receivingInfoResponse.setControlSequenceNumber(receiveSummary.getControlSequenceNumber() != null ?
                receiveSummary.getControlSequenceNumber() : defaultValuesConfigProperties.getControlSequenceNumber());
        receivingInfoResponse.setReceiptStatus(receiveSummary.getBusinessStatusCode() != null ? receiveSummary.getBusinessStatusCode().toString() : null);
        if (StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.LINENUMBERFLAG.getQueryParam()))
                && allRequestParams.get(ReceivingInfoRequestQueryParameters.LINENUMBERFLAG.getQueryParam()).equalsIgnoreCase("Y")) {
            List<ReceivingInfoLineResponse> lineInfoList = lineResponseList.stream().map(t -> convertToLineResponse(t)).collect(Collectors.toList());
            receivingInfoResponse.setReceivingInfoLineResponses(lineInfoList);
        }
        return receivingInfoResponse;
    }

    private ReceivingInfoLineResponse convertToLineResponse(ReceivingLine receivingLine) {
        ReceivingInfoLineResponse response = new ReceivingInfoLineResponse();
        response.setReceiptNumber(StringUtils.isNotEmpty(receivingLine.getReceiveId()) ?
                receivingLine.getReceiveId() : "0");
        response.setReceiptLineNumber(receivingLine.getLineSequenceNumber());
        response.setItemNumber(receivingLine.getItemNumber() != null ? receivingLine.getItemNumber() :
                defaultValuesConfigProperties.getItemNumber());
        response.setQuantity(receivingLine.getReceivedQuantity() != null ?
                receivingLine.getReceivedQuantity().intValue() : defaultValuesConfigProperties.getReceivedQuantity());
        response.setEachCostAmount(receivingLine.getCostAmount() != null ?
                receivingLine.getCostAmount() : defaultValuesConfigProperties.getTotalCostAmount());
        response.setEachRetailAmount(receivingLine.getRetailAmount() != null ?
                receivingLine.getRetailAmount() : defaultValuesConfigProperties.getTotalRetailAmount());
        response.setNumberOfCasesReceived(receivingLine.getReceivedQuantity() != null ?
                receivingLine.getReceivedQuantity() : defaultValuesConfigProperties.getReceivedQuantity());
        response.setPackQuantity(receivingLine.getQuantity() != null ?
                receivingLine.getQuantity() : defaultValuesConfigProperties.getQuantity());
        response.setBottleDepositFlag(StringUtils.isNotEmpty(receivingLine.getBottleDepositFlag()) ?
                receivingLine.getBottleDepositFlag() : defaultValuesConfigProperties.getBottleDepositFlag());
        response.setUpc(StringUtils.isNotEmpty(receivingLine.getUpcNumber()) ? receivingLine.getUpcNumber() :
                defaultValuesConfigProperties.getUpcNumber());
        response.setItemDescription(receivingLine.getItemDescription());
        response.setUnitOfMeasure(receivingLine.getReceivedQuantityUOMCode());
        response.setVariableWeightInd(StringUtils.isNotEmpty(receivingLine.getVariableWeightIndicator()) ?
                receivingLine.getVariableWeightIndicator() : defaultValuesConfigProperties.getVariableWeightIndicator());
        response.setCostMultiple(receivingLine.getCostMultiple() != null ?
                receivingLine.getCostMultiple() : defaultValuesConfigProperties.getCostMultiple());
        response.setReceivedWeightQuantity(receivingLine.getReceivedWeightQuantity() == null ?
                defaultValuesConfigProperties.getReceivedWeightQuantity().toString() :
                receivingLine.getReceivedWeightQuantity().toString());
        if (receivingLine.getMerchandises() != null) {
            response.setMerchandises(new ArrayList<>(receivingLine.getMerchandises().values()));
        }
        return response;
    }
    /*************************** Conversion Methods ***********************************/
    /*************************** Version 1 Methods ***********************************/

    /**
     * Return the response with merge data of Fin Txn and Receiving.
     *
     * @param allRequestParams
     * @return
     */
    @Override
    public ReceivingResponse getInfoSeviceDataV1(Map<String, String> allRequestParams) {
        List<ReceivingInfoResponseV1> receivingInfoResponses;
        List<FinancialTxnResponseData> financialTxnResponseDataList = financialTxnIntegrationService.getFinancialTxnDetails(allRequestParams);
        enrichQueryParams(allRequestParams, financialTxnResponseDataList);
        if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.INVOICEID.getQueryParam()) ||
                allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam())) {
            /*
            As the request has InvoiceId/InvoiceNumber,
            we need response from Fin Trans response to get Receiving Info
             */
            if (financialTxnResponseDataList.isEmpty()) {
                throw new NotFoundException("Financial Transaction data not found for given search criteria.");
            } else {
                receivingInfoResponses = getDataForFinancialTxnV1(financialTxnResponseDataList, allRequestParams);
                if (CollectionUtils.isEmpty(receivingInfoResponses)) {
                    throw new NotFoundException("Receiving data not found for given search criteria.");
                }
            }
        } else {

            /*
            As the request has parameters which are available in Receiving,
            we can get response from 'Fin Trans' and 'Receiving Info' independently
             */
            receivingInfoResponses = getDataWoFinancialTxnV1(allRequestParams);
            if (CollectionUtils.isEmpty(receivingInfoResponses)) {
                throw new NotFoundException("Receiving data not found for given search criteria.");
            } else {
                Map<String, ReceivingInfoResponseV1> receivingInfoResponseV1Map =
                        getReceivingInfoMap(receivingInfoResponses);
                List<ReceivingInfoResponseV1> receivingInfoResponsesList = new ArrayList<>();
                List<String> receivingInfoResponsesKeyList = new ArrayList<>();
                for (FinancialTxnResponseData financialTxnResponseData : financialTxnResponseDataList) {
                    Integer storeNumber = (financialTxnResponseData.getOrigStoreNbr() == null || financialTxnResponseData.getOrigStoreNbr() == 0)
                            ? financialTxnResponseData.getStoreNumber() : financialTxnResponseData.getOrigStoreNbr();
                    String id = (financialTxnResponseData.getPurchaseOrderId() == null ? 0 :
                            financialTxnResponseData.getPurchaseOrderId())
                            + ReceivingConstants.PIPE_SEPARATOR
                            + (StringUtils.isEmpty(financialTxnResponseData.getReceiveId()) ? "0" :
                            financialTxnResponseData.getReceiveId()) + ReceivingConstants.PIPE_SEPARATOR
                            + (storeNumber == null ? 0 : storeNumber) + ReceivingConstants.PIPE_SEPARATOR
                            + (financialTxnResponseData.getReceivingDate() == null ? "0" :
                            financialTxnResponseData.getReceivingDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate());
                    ReceivingInfoResponseV1 receivingInfoResponseV1 = receivingInfoResponseV1Map.get(id);
                    if (receivingInfoResponseV1 != null) {
                        updateReceivingInfoResponseV1(financialTxnResponseData, receivingInfoResponseV1);
                        receivingInfoResponsesList.add(receivingInfoResponseV1);
                        receivingInfoResponsesKeyList.add(id);
                    }
                }
                receivingInfoResponsesKeyList.forEach(key -> receivingInfoResponseV1Map.remove(key));
                receivingInfoResponsesList.addAll(new ArrayList<>(receivingInfoResponseV1Map.values()));
                receivingInfoResponses = receivingInfoResponsesList;
            }
        }
        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(receivingInfoResponses);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.now());
        return successMessage;
    }

    private void enrichQueryParams(Map<String, String> allRequestParams, List<FinancialTxnResponseData> financialTxnResponseDataList) {
        if (CollectionUtils.isNotEmpty(financialTxnResponseDataList)) {
            Integer storeNumber =
                    financialTxnResponseDataList.get(0).getOrigStoreNbr() == null
                            ? financialTxnResponseDataList.get(0).getStoreNumber()
                            : financialTxnResponseDataList.get(0).getOrigStoreNbr();
            allRequestParams.put("locationNumber", String.valueOf(storeNumber));
        }
    }

    /**
     * Retrieve receiving response for all Fin Txn objects
     *
     * @param financialTxnResponseDataList
     * @param allRequestParams
     * @return
     */
    private List<ReceivingInfoResponseV1> getDataForFinancialTxnV1(List<FinancialTxnResponseData> financialTxnResponseDataList, Map<String, String> allRequestParams) {
        List<ReceivingInfoResponseV1> receivingInfoResponses = new ArrayList<>();
        List<ReceiveSummary> allReceiveSummaries = new ArrayList<>();
        List<Criteria> lineCriteriaList = new ArrayList<>();
        List<Criteria> freightCriteriaList = new ArrayList<>();
        Map<String, FinancialTxnResponseData> financialTxnResponseMap = new HashMap<>();
        for (FinancialTxnResponseData financialTxnResponseData : financialTxnResponseDataList) {
            List<ReceiveSummary> receiveSummaries = getSummaryData(financialTxnResponseData, allRequestParams);
            if (CollectionUtils.isNotEmpty(receiveSummaries)) {
                allReceiveSummaries.add(receiveSummaries.get(0));
                financialTxnResponseMap.put(receiveSummaries.get(0).get_id(), financialTxnResponseData);
                lineCriteriaList.add(getLineDataCriteria(receiveSummaries.get(0), allRequestParams));
                if (receiveSummaries.get(0).getFreightBillExpandId() != null) {
                    freightCriteriaList.add(Criteria.where("_id").is(receiveSummaries.get(0).getFreightBillExpandId()));
                }
            }
        }
        List<ReceivingLine> lineResponseList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(lineCriteriaList)) {
            Query query = new Query(new Criteria().orOperator(lineCriteriaList.toArray(new Criteria[lineCriteriaList.size()])));
            log.info("query: " + query);
            lineResponseList = executeQueryInLine(query);
        }
        List<FreightResponse> freightResponseList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(freightCriteriaList)) {
            Query query = new Query(new Criteria().orOperator(freightCriteriaList.toArray(new Criteria[freightCriteriaList.size()])));
            log.info("query: " + query);
            freightResponseList = executeQueryInFreight(query);
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
        Map<Long, FreightResponse> freightResponseMap = freightResponseList.stream().collect(Collectors.toMap(FreightResponse::getId, freightResponse -> freightResponse));
        Iterator<ReceiveSummary> iteratorSummary = allReceiveSummaries.iterator();
        while (iteratorSummary.hasNext()) {
            ReceiveSummary receiveSummary = iteratorSummary.next();
            ReceivingInfoResponseV1 receivingInfoResponseV1 = conversionToReceivingInfoV1(receiveSummary
                    , financialTxnResponseMap.get(receiveSummary.get_id())
                    , receivingLineMap.containsKey(receiveSummary.get_id()) ? receivingLineMap.get(receiveSummary.get_id()) : new ArrayList<>()
                    , freightResponseMap.containsKey(receiveSummary.getFreightBillExpandId()) ? freightResponseMap.get(receiveSummary.getFreightBillExpandId()) : new FreightResponse()
                    , allRequestParams);
            receivingInfoResponses.add(receivingInfoResponseV1);
        }
        return receivingInfoResponses;
    }

    private Criteria getLineDataCriteria(ReceiveSummary receiveSummary, Map<String, String> allRequestParams) {
        Criteria criteria = null;
        if (receiveSummary.getStoreNumber() != null) {
            criteria = ReceivingUtils.getCriteriaForPartitionKey(null, allRequestParams, receiveSummary.getStoreNumber(), monthsPerShard, monthsToDisplay);
        }
        if (criteria != null) {
            criteria.and(ReceivingLineParameters.SUMMARYREFERENCE.getParameterName()).is(receiveSummary.get_id());
        } else {
            criteria = new Criteria(ReceivingLineParameters.SUMMARYREFERENCE.getParameterName()).is(receiveSummary.get_id());
        }
        if (StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam()))) {
            List<String> itemNumbers = Arrays.asList(allRequestParams.get(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam()).split(","));
            criteria.and(ReceivingLineParameters.ITEMNUMBER.getParameterName()).in(itemNumbers.stream().map(Long::parseLong).collect(Collectors.toList()));
        }
        if (StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam()))) {
            List<String> upcNumberList =
                    Arrays.asList(allRequestParams.get(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam()).split(","));
            List<String> updatedUpcNumberList = new ArrayList<>();
            /*
             * Change 13 Digit UPC Number to 16 Digit GTIN Number while hitting line
             * Combination 1 : Add "00" to beginning and "0" to the end
             * Combination 2 : Add "000" to the beginning
             */
            for (String upcNumber : upcNumberList) {
                updatedUpcNumberList.add("00" + upcNumber + "0");
                updatedUpcNumberList.add("000" + upcNumber);
            }
            criteria.and(ReceivingLineParameters.UPCNUMBER.getParameterName()).in(updatedUpcNumberList);
        }
        log.info("queryForLineResponse :: Query is " + criteria);
        return criteria;
    }

    private List<FreightResponse> executeQueryInFreight(Query query) {
        List<FreightResponse> receiveFreights = new ArrayList<>();
        if (query != null) {
            long startTime = System.currentTimeMillis();
            receiveFreights = mongoTemplate.find(query.limit(1000), FreightResponse.class, freightCollection);
            log.info(" executeQueryInLine :: queryTime :: " + (System.currentTimeMillis() - startTime));
        }
        return receiveFreights;
    }

    private List<ReceivingInfoResponseV1> getDataWoFinancialTxnV1(Map<String, String> allRequestParams) {
        List<ReceivingInfoResponseV1> receivingInfoResponses = new ArrayList<>();
        List<Criteria> lineCriteriaList = new ArrayList<>();
        List<Criteria> freightCriteriaList = new ArrayList<>();
        List<ReceiveSummary> receiveSummaries = getSummaryData(allRequestParams);
        for (ReceiveSummary receiveSummary : receiveSummaries) {
            lineCriteriaList.add(getLineDataCriteria(receiveSummary, allRequestParams));
            if (receiveSummary.getFreightBillExpandId() != null) {
                freightCriteriaList.add(Criteria.where("_id").is(receiveSummary.getFreightBillExpandId()));
            }
        }
        List<ReceivingLine> lineResponseList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(lineCriteriaList)) {
            Query query = new Query(new Criteria().orOperator(lineCriteriaList.toArray(new Criteria[lineCriteriaList.size()])));
            log.info("query: " + query);
            lineResponseList = executeQueryInLine(query);
        }
        List<FreightResponse> freightResponseList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(freightCriteriaList)) {
            Query query = new Query(new Criteria().orOperator(freightCriteriaList.toArray(new Criteria[freightCriteriaList.size()])));
            log.info("query: " + query);
            freightResponseList = executeQueryInFreight(query);
        }
        Map<String, List<ReceivingLine>> receivingLineMap = new HashMap<>();
        Iterator<ReceivingLine> iteratorLine = lineResponseList.iterator();
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
        Map<Long, FreightResponse> freightResponseMap = freightResponseList.stream().collect(Collectors.toMap(FreightResponse::getId, freightResponse -> freightResponse));
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

    private Map<String, ReceivingInfoResponseV1> getReceivingInfoMap(List<ReceivingInfoResponseV1> receivingInfoResponseV1List) {
        Map<String, ReceivingInfoResponseV1> receivingInfoResponseV1Map = new HashMap<>();
        for (ReceivingInfoResponseV1 receivingInfoResponseV1 : receivingInfoResponseV1List) {
            String id = (receivingInfoResponseV1.getPurchaseOrderId() == null ? 0 :
                    receivingInfoResponseV1.getPurchaseOrderId())
                    + ReceivingConstants.PIPE_SEPARATOR
                    + (receivingInfoResponseV1.getReceiveId() == null ? "0" : receivingInfoResponseV1.getReceiveId())
                    + ReceivingConstants.PIPE_SEPARATOR
                    + (receivingInfoResponseV1.getOrigStoreNbr() == null ? 0 :
                    receivingInfoResponseV1.getOrigStoreNbr())
                    + ReceivingConstants.PIPE_SEPARATOR
                    + (receivingInfoResponseV1.getParentReceivingDate() == null ? "0" :
                    receivingInfoResponseV1.getParentReceivingDate());
            receivingInfoResponseV1Map.put(id, receivingInfoResponseV1);
        }
        return receivingInfoResponseV1Map;
    }

    private ReceivingInfoResponseV1 conversionToReceivingInfoV1(ReceiveSummary receiveSummary, FinancialTxnResponseData financialTxnResponseData, List<ReceivingLine> lineResponseList, FreightResponse freightResponse, Map<String, String> allRequestParams) {
        ReceivingInfoResponseV1 receivingInfoResponseV1 = new ReceivingInfoResponseV1();
        if (financialTxnResponseData != null) {
            updateReceivingInfoResponseV1(financialTxnResponseData, receivingInfoResponseV1);
        } else {
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
        }
        receivingInfoResponseV1.setLineCount(CollectionUtils.isNotEmpty(lineResponseList) ?
                new Long(lineResponseList.size()) : defaultValuesConfigProperties.getLineCount());
        receivingInfoResponseV1.setCarrierCode(freightResponse != null
                && StringUtils.isNotEmpty(freightResponse.getCarrierCode()) ?
                freightResponse.getCarrierCode() : defaultValuesConfigProperties.getCarrierCode());
        receivingInfoResponseV1.setTrailerNumber(freightResponse != null
                && StringUtils.isNotEmpty(freightResponse.getTrailerNbr()) ?
                freightResponse.getTrailerNbr() : defaultValuesConfigProperties.getTrailerNbr());
        receivingInfoResponseV1.setControlNumber(StringUtils.isNotEmpty(receiveSummary.getReceivingControlNumber()) ?
                receiveSummary.getReceivingControlNumber() : defaultValuesConfigProperties.getReceivingControlNumber());
        receivingInfoResponseV1.setTransactionType(receiveSummary.getTransactionType());
        receivingInfoResponseV1.setLocationNumber(receiveSummary.getStoreNumber());
        receivingInfoResponseV1.setPurchaseOrderId(receiveSummary.getPurchaseOrderId());
        receivingInfoResponseV1.setReceiptDate(receiveSummary.getDateReceived().atZone(ZoneId.of("GMT")).toLocalDate());
        receivingInfoResponseV1.setReceiptNumber(StringUtils.isNotEmpty(receiveSummary.getReceiveId()) ?
                receiveSummary.getReceiveId() : "0");
        if (receiveSummary.getTypeIndicator().equals('W')) {
            if (CollectionUtils.isNotEmpty(lineResponseList)) {
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
        receivingInfoResponseV1.setReceiveId(StringUtils.isNotEmpty(receiveSummary.getReceiveId()) ?
                receiveSummary.getReceiveId() : "0");
        receivingInfoResponseV1.setReceiptStatus(receiveSummary.getBusinessStatusCode() != null ? receiveSummary.getBusinessStatusCode().toString() : null);
        if (StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.LINENUMBERFLAG.getQueryParam()))
                && allRequestParams.get(ReceivingInfoRequestQueryParameters.LINENUMBERFLAG.getQueryParam()).equalsIgnoreCase("Y")) {
            ReceivingUtils.updateLineResponse(lineResponseList);
            List<ReceivingInfoLineResponse> lineInfoList = lineResponseList.stream().map(t -> convertToLineResponse(t)).collect(Collectors.toList());
            receivingInfoResponseV1.setReceivingInfoLineResponses(lineInfoList);
        }
        return receivingInfoResponseV1;
    }

    private void updateReceivingInfoResponseV1(FinancialTxnResponseData financialTxnResponseData, ReceivingInfoResponseV1 receivingInfoResponseV1) {
        receivingInfoResponseV1.setAuthorizedBy(financialTxnResponseData.getAuthorizedBy());
        receivingInfoResponseV1.setAuthorizedDate(financialTxnResponseData.getAuthorizedDate() != null ?
                financialTxnResponseData.getAuthorizedDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate() : null);
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
    }
    /*************************** Version 1 Methods ***********************************/

}
