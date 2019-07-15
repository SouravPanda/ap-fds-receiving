package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.common.ReceivingInfoQueryParamName;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnIntegrationService;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnResponseData;
import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummaryParameters;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLineParameters;
import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoLineResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    FinancialTxnIntegrationService financialTxnIntegrationService;

    private ConcurrentMap<String, String> queryParamMap;

    /**
     * @param countryCode
     * @param invoiceId
     * @param invoiceNumber
     * @param purchaseOrderNumber
     * @param purchaseOrderId
     * @param receiptNumbers
     * @param transactionType
     * @param controlNumber
     * @param locationNumber
     * @param divisionNumber
     * @param vendorNumber
     * @param departmentNumber
     * @param itemNumbers
     * @param upcNumbers
     * @param receiptDateStart
     * @param receiptDateEnd
     * @param lineNumberFlag
     * @return
     */
    @Override
    public ReceivingResponse getSevice(String countryCode, String invoiceId, String invoiceNumber,
                                       String purchaseOrderNumber, String purchaseOrderId,
                                       List<String> receiptNumbers, String transactionType,
                                       String controlNumber, String locationNumber, String divisionNumber,
                                       String vendorNumber, String departmentNumber, List<String> itemNumbers,
                                       List<String> upcNumbers, String receiptDateStart, String receiptDateEnd, String lineNumberFlag) {
        List<ReceivingInfoResponse> receivingInfoResponses;
        queryParamMap = notNullParamMap(countryCode, invoiceId, invoiceNumber, purchaseOrderNumber, purchaseOrderId
                , transactionType, controlNumber, locationNumber, divisionNumber, vendorNumber, departmentNumber, receiptDateStart, receiptDateEnd, lineNumberFlag);
        if (queryParamMap.containsKey(ReceivingInfoQueryParamName.INVOICEID.getQueryParamName())
                || queryParamMap.containsKey(ReceivingInfoQueryParamName.INVOICENUMBER.getQueryParamName())
                || queryParamMap.containsKey(ReceivingInfoQueryParamName.PURCHASEORDERID.getQueryParamName())
                || queryParamMap.containsKey(ReceivingInfoQueryParamName.PURCHASEORDERNUMBER.getQueryParamName())) {
            List<FinancialTxnResponseData> financialTxnResponseDataList = financialTxnIntegrationService.getFinancialTxnDetails(queryParamMap);
            receivingInfoResponses = getDataForFinancialTxn(financialTxnResponseDataList, receiptNumbers, itemNumbers, upcNumbers);
        } else {
            receivingInfoResponses = getDataFromReceiveDB(receiptNumbers, itemNumbers, upcNumbers);
        }
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
    //TODO : receiptNumbers is not being used here. Add with proper use case.
    private List<ReceivingInfoResponse> getDataForFinancialTxn(List<FinancialTxnResponseData> financialTxnResponseDataArray, List<String> receiptNumbers, List<String> itemNumbers, List<String> upcNumbers) {
        List<ReceivingInfoResponse> receivingInfoResponses = new ArrayList<>();
        for (FinancialTxnResponseData financialTxnResponseData : financialTxnResponseDataArray) {
            Query query = getQueryForFinancialTxn(financialTxnResponseData);
            List<ReceiveSummary> summaryList = executeQueryInSummary(query);
            for (ReceiveSummary receiveSummary : summaryList) {
                List<ReceivingLine> lineResponseList = getLineResponse(receiveSummary, itemNumbers, upcNumbers);
                List<FreightResponse> freightResponseList = getFreightResponse(receiveSummary);
                ReceivingInfoResponse receivingInfoResponse = convertsionToReceivingInfo(receiveSummary, financialTxnResponseData, lineResponseList, freightResponseList);
                receivingInfoResponses.add(receivingInfoResponse);
            }
        }
        return receivingInfoResponses;
    }

    private Query getQueryForFinancialTxn(FinancialTxnResponseData financialTxnResponseData) {
        Query query = new Query();
        CriteriaDefinition criteriaDefinition = null;
        if (StringUtils.isNotEmpty(financialTxnResponseData.getPoReceiveId())) {
            criteriaDefinition = Criteria.where(ReceiveSummaryParameters.PORECEIVEID.getParameterName()).is(financialTxnResponseData.getPoReceiveId());
            query.addCriteria(criteriaDefinition);
        }
        if (financialTxnResponseData.getReceivingControlNumber() != 0) {
            criteriaDefinition = Criteria.where(ReceiveSummaryParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(financialTxnResponseData.getReceivingControlNumber().toString());
            query.addCriteria(criteriaDefinition);
        }
        if (financialTxnResponseData.getStoreNumber() != 0) {
            criteriaDefinition = Criteria.where(ReceiveSummaryParameters.STORENUMBER.getParameterName()).is(financialTxnResponseData.getStoreNumber());
            query.addCriteria(criteriaDefinition);
        }
        log.info("getQueryForFinancialTxn :: Query is " + query);
        return criteriaDefinition == null ? null : query;
    }
    /*************************** Financial-Txn Logic : END ***************************/

    /*************************** Normal-Flow : START ***************************/
    private List<ReceivingInfoResponse> getDataFromReceiveDB(List<String> receiptNumbers, List<String> itemNumbers, List<String> upcNumbers) {
        List<ReceivingInfoResponse> receivingInfoResponses = new ArrayList<>();
        Query query = getSummaryQuery(queryParamMap, receiptNumbers);
        List<ReceiveSummary> receiveSummaryList = executeQueryInSummary(query);
        for (ReceiveSummary receiveSummary : receiveSummaryList) {
            List<ReceivingLine> lineResponseList = getLineResponse(receiveSummary, itemNumbers, upcNumbers);
            List<FreightResponse> freightResponseList = getFreightResponse(receiveSummary);
            ReceivingInfoResponse receivingInfoResponse = convertsionToReceivingInfo(receiveSummary, null, lineResponseList, freightResponseList);
            receivingInfoResponses.add(receivingInfoResponse);
        }
        return receivingInfoResponses;
    }

    private Query getSummaryQuery(Map<String, String> queryParamMap, List<String> receiptNumbers) {
        Query query = new Query();
        CriteriaDefinition criteriaDefinition = null;
        if (CollectionUtils.isNotEmpty(receiptNumbers)) {
            criteriaDefinition = Criteria.where(ReceiveSummaryParameters.PORECEIVEID.getParameterName()).in(receiptNumbers);
            query.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(queryParamMap.get(ReceivingInfoQueryParamName.CONTROLNUMBER.getQueryParamName()))) {
            criteriaDefinition = Criteria.where(ReceiveSummaryParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(queryParamMap.get(ReceivingInfoQueryParamName.CONTROLNUMBER.getQueryParamName()));
            query.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(queryParamMap.get(ReceivingInfoQueryParamName.TRANSACTIONTYPE.getQueryParamName()))) {
            criteriaDefinition = Criteria.where(ReceiveSummaryParameters.TRANSACTIONTYPE.getParameterName()).is(queryParamMap.get(ReceivingInfoQueryParamName.TRANSACTIONTYPE.getQueryParamName()));
            query.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(queryParamMap.get(ReceivingInfoQueryParamName.LOCATIONNUMBER.getQueryParamName()))) {
            criteriaDefinition = Criteria.where(ReceiveSummaryParameters.STORENUMBER.getParameterName()).is(queryParamMap.get(ReceivingInfoQueryParamName.LOCATIONNUMBER.getQueryParamName()));
            query.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(queryParamMap.get(ReceivingInfoQueryParamName.DIVISIONNUMBER.getQueryParamName()))) {
            criteriaDefinition = Criteria.where(ReceiveSummaryParameters.BASEDIVISIONNUMBER.getParameterName()).is(queryParamMap.get(ReceivingInfoQueryParamName.DIVISIONNUMBER.getQueryParamName()));
            query.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(queryParamMap.get(ReceivingInfoQueryParamName.VENDORNUMBER.getQueryParamName()))) {
            criteriaDefinition = Criteria.where(ReceiveSummaryParameters.VENDORNUMBER.getParameterName()).is(queryParamMap.get(ReceivingInfoQueryParamName.VENDORNUMBER.getQueryParamName()));
            query.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(queryParamMap.get(ReceivingInfoQueryParamName.DEPARTMENTNUMBER.getQueryParamName()))) {
            criteriaDefinition = Criteria.where(ReceiveSummaryParameters.DEPARTMENTNUMBER.getParameterName()).is(queryParamMap.get(ReceivingInfoQueryParamName.DEPARTMENTNUMBER.getQueryParamName()));
            query.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(queryParamMap.get(ReceivingInfoQueryParamName.RECEIPTDATESTART.getQueryParamName()))
                && StringUtils.isNotEmpty(queryParamMap.get(ReceivingInfoQueryParamName.RECEIPTDATEEND.getQueryParamName()))) {
            try {
                DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeParser();
                DateTime startDate = dateTimeFormatter.parseDateTime(queryParamMap.get(ReceivingInfoQueryParamName.RECEIPTDATESTART.getQueryParamName()));
                DateTime endDate = dateTimeFormatter.parseDateTime(queryParamMap.get(ReceivingInfoQueryParamName.RECEIPTDATEEND.getQueryParamName()));
                criteriaDefinition = Criteria.where(ReceiveSummaryParameters.MDSRECEIVEDATE.getParameterName()).gte(startDate).lte(endDate);
                query.addCriteria(criteriaDefinition);
            } catch (IllegalArgumentException e) {
                log.error(ExceptionUtils.getStackTrace(e));
                throw new BadRequestException("Date format is not correct.", "please enter valid query parameters");
            }
        }
        log.info("getSummaryQuery :: Query is " + query);
        return criteriaDefinition == null ? null : query;
    }
    /*************************** Normal-Flow : END ***************************/

    /*************************** General Methods ***********************************/
    private ConcurrentMap<String, String> notNullParamMap(String countryCode, String invoiceId, String invoiceNumber, String purchaseOrderNumber, String purchaseOrderId, String transactionType, String controlNumber, String locationNumber, String divisionNumber, String vendorNumber, String departmentNumber, String receiptDateStart, String receiptDateEnd, String lineNumberFlag) {
        queryParamMap = new ConcurrentHashMap<>();
        if (StringUtils.isNotEmpty(countryCode)) {
            queryParamMap.put(ReceivingInfoQueryParamName.COUNTRYCODE.getQueryParamName(), countryCode);
        }
        if (StringUtils.isNotEmpty(invoiceId)) {
            queryParamMap.put(ReceivingInfoQueryParamName.INVOICEID.getQueryParamName(), invoiceId);
        }
        if (StringUtils.isNotEmpty(invoiceNumber)) {
            queryParamMap.put(ReceivingInfoQueryParamName.INVOICENUMBER.getQueryParamName(), invoiceNumber);
        }
        if (StringUtils.isNotEmpty(purchaseOrderNumber)) {
            queryParamMap.put(ReceivingInfoQueryParamName.PURCHASEORDERNUMBER.getQueryParamName(), purchaseOrderNumber);
        }
        if (StringUtils.isNotEmpty(purchaseOrderId)) {
            queryParamMap.put(ReceivingInfoQueryParamName.PURCHASEORDERID.getQueryParamName(), purchaseOrderId);
        }
        if (StringUtils.isNotEmpty(transactionType)) {
            queryParamMap.put(ReceivingInfoQueryParamName.TRANSACTIONTYPE.getQueryParamName(), transactionType);
        }
        if (StringUtils.isNotEmpty(controlNumber)) {
            queryParamMap.put(ReceivingInfoQueryParamName.CONTROLNUMBER.getQueryParamName(), controlNumber);
        }
        if (StringUtils.isNotEmpty(locationNumber)) {
            queryParamMap.put(ReceivingInfoQueryParamName.LOCATIONNUMBER.getQueryParamName(), locationNumber);
        }
        if (StringUtils.isNotEmpty(divisionNumber)) {
            queryParamMap.put(ReceivingInfoQueryParamName.DIVISIONNUMBER.getQueryParamName(), divisionNumber);
        }
        if (StringUtils.isNotEmpty(vendorNumber)) {
            queryParamMap.put(ReceivingInfoQueryParamName.VENDORNUMBER.getQueryParamName(), vendorNumber);
        }
        if (StringUtils.isNotEmpty(departmentNumber)) {
            queryParamMap.put(ReceivingInfoQueryParamName.DEPARTMENTNUMBER.getQueryParamName(), departmentNumber);
        }
        if (StringUtils.isNotEmpty(receiptDateStart)) {
            queryParamMap.put(ReceivingInfoQueryParamName.RECEIPTDATESTART.getQueryParamName(), receiptDateStart);
        }
        if (StringUtils.isNotEmpty(receiptDateEnd)) {
            queryParamMap.put(ReceivingInfoQueryParamName.RECEIPTDATEEND.getQueryParamName(), receiptDateEnd);
        }
        if (StringUtils.isNotEmpty(lineNumberFlag)) {
            queryParamMap.put(ReceivingInfoQueryParamName.LINENUMBERFLAG.getQueryParamName(), lineNumberFlag);
        }
        return queryParamMap;
    }

    private List<ReceiveSummary> executeQueryInSummary(Query query) {
        List<ReceiveSummary> receiveSummaries = new ArrayList<>();
        if (query != null) {
            receiveSummaries = mongoTemplate.find(query.limit(1000), ReceiveSummary.class, summaryCollection);
        }
        return receiveSummaries;
    }

    private List<ReceivingLine> executeQueryInLine(Query query) {
        List<ReceivingLine> receiveLines = new ArrayList<>();
        if (query != null) {
            receiveLines = mongoTemplate.find(query.limit(1000), ReceivingLine.class, lineCollection);
        }
        return receiveLines;
    }

    private List<FreightResponse> executeQueryInFreight(Query query) {
        List<FreightResponse> receiveFreights = null;
        if (query != null) {
            receiveFreights = mongoTemplate.find(query.limit(1000), FreightResponse.class, freightCollection);
        }
        return receiveFreights;
    }
    /*************************** General Methods ***********************************/

    /*************************** receive-line data ***************************/
    private List<ReceivingLine> getLineResponse(ReceiveSummary receiveSummary, List<String> itemNumbers, List<String> upcNumbers) {
        List<ReceivingLine> lineResponseList;
        lineResponseList = queryForLineResponse(receiveSummary, itemNumbers, upcNumbers);
        return lineResponseList;
    }

    private List<ReceivingLine> queryForLineResponse(ReceiveSummary receiveSummary, List<String> itemNumbers, List<String> upcNumbers) {
        Query query = new Query();
        CriteriaDefinition criteriaDefinition = null;
        if (StringUtils.isNotEmpty(receiveSummary.getReceivingControlNumber())) {
            criteriaDefinition = Criteria.where(ReceivingLineParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(receiveSummary.getReceivingControlNumber().trim());
            query.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(receiveSummary.getPoReceiveId())) {
            criteriaDefinition = Criteria.where(ReceivingLineParameters.PORECEIVEID.getParameterName()).is(receiveSummary.getPoReceiveId().trim());
            query.addCriteria(criteriaDefinition);
        }
        if (receiveSummary.getStoreNumber() != null) {
            criteriaDefinition = Criteria.where(ReceivingLineParameters.STORENUMBER.getParameterName()).is(receiveSummary.getStoreNumber());
            query.addCriteria(criteriaDefinition);
        }
        if (receiveSummary.getBaseDivisionNumber() != null) {
            criteriaDefinition = Criteria.where(ReceivingLineParameters.BASEDIVISIONNUMBER.getParameterName()).is(receiveSummary.getBaseDivisionNumber());
            query.addCriteria(criteriaDefinition);
        }
        if (receiveSummary.getTransactionType() != null) {
            criteriaDefinition = Criteria.where(ReceivingLineParameters.TRANSACTIONTYPE.getParameterName()).is(receiveSummary.getTransactionType());
            query.addCriteria(criteriaDefinition);
        }
        if (CollectionUtils.isNotEmpty(itemNumbers)) {
            criteriaDefinition = Criteria.where(ReceivingLineParameters.ITEMNUMBER.getParameterName()).in(itemNumbers);
            query.addCriteria(criteriaDefinition);
        }
        if (CollectionUtils.isNotEmpty(upcNumbers)) {
            criteriaDefinition = Criteria.where(ReceivingLineParameters.UPCNUMBER.getParameterName()).in(upcNumbers);
            query.addCriteria(criteriaDefinition);
        }
        log.info("queryForLineResponse :: Query is " + query);
        return executeQueryInLine(criteriaDefinition == null ? null : query);
    }
    /******* receive-line data   *********/

    /******* receive-freight data *********/
    private List<FreightResponse> getFreightResponse(ReceiveSummary receiveSummary) {
        return makeQueryForFreight(receiveSummary);
    }

    private List<FreightResponse> makeQueryForFreight(ReceiveSummary receiveSummary) {
        if (receiveSummary.getFreightBillExpandID() != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(receiveSummary.getFreightBillExpandID()));
            return executeQueryInFreight(query);
        }
        return null;
    }
    /******* receive-freight data   *********/

    /*************************** Conversion Methods ***********************************/
    private ReceivingInfoResponse convertsionToReceivingInfo(ReceiveSummary receiveSummary, FinancialTxnResponseData financialTxnResponseData, List<ReceivingLine> lineResponseList, List<FreightResponse> freightResponseList) {
        ReceivingInfoResponse receivingInfoResponse = new ReceivingInfoResponse();
        if (financialTxnResponseData != null) {
            receivingInfoResponse.setPurchaseOrderId(StringUtils.isNotEmpty(financialTxnResponseData.getPoNumber()) ? financialTxnResponseData.getPoNumber() : receiveSummary.getReceivingControlNumber());
            receivingInfoResponse.setReceiptNumber(StringUtils.isNotEmpty(financialTxnResponseData.getPoReceiveId()) ? Integer.parseInt(financialTxnResponseData.getPoReceiveId()) : StringUtils.isNotEmpty(receiveSummary.getPoReceiveId()) ? Integer.parseInt(receiveSummary.getPoReceiveId()) : 0);
            receivingInfoResponse.setControlNumber(financialTxnResponseData.getReceivingControlNumber() != null ? financialTxnResponseData.getReceivingControlNumber().toString() : receiveSummary.getReceivingControlNumber());
            receivingInfoResponse.setLocationNumber(financialTxnResponseData.getStoreNumber() != null ? financialTxnResponseData.getStoreNumber() : receiveSummary.getStoreNumber());
            receivingInfoResponse.setDivisionNumber(financialTxnResponseData.getBaseDivisionNumber() != null ? financialTxnResponseData.getBaseDivisionNumber() : receiveSummary.getBaseDivisionNumber());
            receivingInfoResponse.setVendorNumber(financialTxnResponseData.getVendorNumber() != null ? financialTxnResponseData.getVendorNumber() : receiveSummary.getVendorNumber());
            receivingInfoResponse.setTotalCostAmount(financialTxnResponseData.getTotalCostAmount() != null ? financialTxnResponseData.getTotalCostAmount() : receiveSummary.getTotalCostAmount());
            receivingInfoResponse.setDepartmentNumber(financialTxnResponseData.getDepartmentNumber() != null ? Integer.parseInt(Integer.toString(financialTxnResponseData.getDepartmentNumber()).substring(0, 2)) : receiveSummary.getDepartmentNumber());
        } else {
            receivingInfoResponse.setPurchaseOrderId(receiveSummary.getReceivingControlNumber());
            receivingInfoResponse.setReceiptNumber(StringUtils.isNotEmpty(receiveSummary.getPoReceiveId()) ? Integer.parseInt(receiveSummary.getPoReceiveId()) : 0);
            receivingInfoResponse.setControlNumber(receiveSummary.getReceivingControlNumber());
            receivingInfoResponse.setLocationNumber(receiveSummary.getStoreNumber());
            receivingInfoResponse.setDivisionNumber(receiveSummary.getBaseDivisionNumber());
            receivingInfoResponse.setVendorNumber(receiveSummary.getVendorNumber());
            receivingInfoResponse.setTotalCostAmount(receiveSummary.getTotalCostAmount());
            receivingInfoResponse.setDepartmentNumber(receiveSummary.getDepartmentNumber());
        }
        receivingInfoResponse.setTransactionType(receiveSummary.getTransactionType());
        receivingInfoResponse.setReceiptDate(receiveSummary.getDateReceived());
        receivingInfoResponse.setReceiptStatus(receiveSummary.getBusinessStatusCode());
        receivingInfoResponse.setCarrierCode(CollectionUtils.isNotEmpty(freightResponseList) ? freightResponseList.get(0).getCarrierCode() : null);
        receivingInfoResponse.setTrailerNumber(CollectionUtils.isNotEmpty(freightResponseList) ? freightResponseList.get(0).getTrailerNbr() : null);
        receivingInfoResponse.setTotalRetailAmount(receiveSummary.getTotalRetailAmount());
        receivingInfoResponse.setLineCount(new Long(lineResponseList.size()));
        if (queryParamMap.get(ReceivingInfoQueryParamName.LINENUMBERFLAG.getQueryParamName()).equalsIgnoreCase("Y")) {
            List<ReceivingInfoLineResponse> lineInfoList = lineResponseList.stream().map((t) -> convertToLineResponse(t)).collect(Collectors.toList());
            receivingInfoResponse.setReceivingInfoLineResponses(lineInfoList);
        }
        return receivingInfoResponse;
    }

    private ReceivingInfoLineResponse convertToLineResponse(ReceivingLine receivingLine) {
        ReceivingInfoLineResponse response = new ReceivingInfoLineResponse();
        response.setReceiptNumber(Integer.valueOf(receivingLine.getPurchaseOrderReceiveID()));
        response.setReceiptLineNumber(receivingLine.getLineNumber() == null ? 0 : receivingLine.getLineNumber());
        response.setItemNumber(receivingLine.getItemNumber());
        response.setVendorNumber(receivingLine.getVendorNumber());
        response.setQuantity(receivingLine.getReceivedQuantity());
        response.setEachCostAmount(receivingLine.getCostAmount());
        response.setEachRetailAmount(receivingLine.getRetailAmount());
        response.setPackQuantity(receivingLine.getQuantity());
        response.setNumberofCasesReceived(receivingLine.getReceivedQuantity());
        response.setPurchaseOrderId(receivingLine.getPurchasedOrderId() != null ? receivingLine.getPurchasedOrderId().toString() : null);
        response.setUnitOfMeasure(receivingLine.getReceivedQuantityUnitOfMeasureCode());
        response.setVariableWeightInd(receivingLine.getVariableWeightIndicator());
        response.setReceivedWeightQuantity(receivingLine.getReceivedWeightQuantity() == null ? null : receivingLine.getReceivedWeightQuantity().toString());
        response.setTransactionType(receivingLine.getTransactionType());
        response.setLocationNumber(receivingLine.getStoreNumber());
        response.setDivisionNumber(receivingLine.getBaseDivisionNumber() == null ? 0 : receivingLine.getBaseDivisionNumber());
        response.setPurchaseOrderNumber(receivingLine.getPurchaseOrderNumber());
        response.setControlNumber(receivingLine.getReceivingControlNumber());
        response.setBottleDepositAmount(10.00);
        return response;
    }
    /*************************** Conversion Methods ***********************************/
}
