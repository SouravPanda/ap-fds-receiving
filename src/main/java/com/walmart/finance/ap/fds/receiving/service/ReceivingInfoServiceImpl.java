package com.walmart.finance.ap.fds.receiving.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnIntegrationService;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnResponseData;
import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLineParameters;
import com.walmart.finance.ap.fds.receiving.response.ReceiveMDSResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoLineResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestQueryParameters;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
//    private ConcurrentMap<String, String> queryParamMap;

    Gson gson = new Gson();

    /**
     * @param allRequestParams
     * @return
     */
    @Override
    public ReceivingResponse getInfoSeviceData(Map<String, String> allRequestParams) {
        List<ReceivingInfoResponse> receivingInfoResponses;
            // First Fin Txn + Cosmos
            List<FinancialTxnResponseData> financialTxnResponseDataList = financialTxnIntegrationService.getFinancialTxnDetails(allRequestParams);
            receivingInfoResponses = getDataForFinancialTxn(financialTxnResponseDataList, allRequestParams);
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
    private List<ReceivingInfoResponse> getDataForFinancialTxn(List<FinancialTxnResponseData> financialTxnResponseDataArray, Map<String, String> allRequestParams) {
        List<ReceivingInfoResponse> receivingInfoResponses = new ArrayList<>();
        for (FinancialTxnResponseData financialTxnResponseData : financialTxnResponseDataArray) {
            ReceiveSummary receiveSummary = getSummaryData(financialTxnResponseData);
            if(receiveSummary != null)
            {
                List<ReceivingLine> lineResponseList = getLineData(receiveSummary, allRequestParams);
                List<FreightResponse> freightResponseList = getFreightData(receiveSummary);
                ReceivingInfoResponse receivingInfoResponse = convertsionToReceivingInfo(receiveSummary, financialTxnResponseData, lineResponseList, freightResponseList, allRequestParams);
                receivingInfoResponses.add(receivingInfoResponse);
            }
        }
        return receivingInfoResponses;
    }

    private ReceiveSummary getSummaryData(FinancialTxnResponseData financialTxnResponseData) {
        String id = (financialTxnResponseData.getPurchaseOrderId() == null ? 0 : financialTxnResponseData.getPurchaseOrderId()) + ReceivingConstants.PIPE_SEPARATOR
                + (StringUtils.isEmpty(financialTxnResponseData.getReceiveId()) ? "0" : financialTxnResponseData.getReceiveId()) + ReceivingConstants.PIPE_SEPARATOR
                + (financialTxnResponseData.getStoreNumber() == null ? 0 : financialTxnResponseData.getStoreNumber()) + ReceivingConstants.PIPE_SEPARATOR
                + (financialTxnResponseData.getReceivingDate() == null ? "0" : financialTxnResponseData.getReceivingDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate());
        ReceiveSummary receiveSummary = mongoTemplate.findById(id, ReceiveSummary.class, summaryCollection);
        return receiveSummary; //executeQueryInSummary(query);
    }
    /*************************** Financial-Txn Logic : END ***************************/
    /*************************** Normal-Flow : START ***************************/
//    private List<ReceivingInfoResponse> getDataFromReceiveDB(List<String> receiptNumbers, List<String> itemNumbers, List<String> upcNumbers) {
//        List<ReceivingInfoResponse> receivingInfoResponses = new ArrayList<>();
//        Query query = getSummaryQuery(queryParamMap, receiptNumbers);
//        List<ReceiveSummary> receiveSummaryList = executeQueryInSummary(query);
//        for (ReceiveSummary receiveSummary : receiveSummaryList) {
//            List<ReceivingLine> lineResponseList = getLineData(receiveSummary, itemNumbers, upcNumbers);
//            List<FreightResponse> freightResponseList = getFreightData(receiveSummary);
//            ReceivingInfoResponse receivingInfoResponse = convertsionToReceivingInfo(receiveSummary, null, lineResponseList, freightResponseList, allRequestParams);
//            receivingInfoResponses.add(receivingInfoResponse);
//        }
//        return receivingInfoResponses;
//    }
//    private Query getSummaryQuery(Map<String, String> queryParamMap, List<String> receiptNumbers) {
//        Query query = new Query();
//        CriteriaDefinition criteriaDefinition = null;
//        if (CollectionUtils.isNotEmpty(receiptNumbers)) {
//            criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.RECEIVEID.getParameterName()).in(receiptNumbers);
//            query.addCriteria(criteriaDefinition);
//        }
//        if (StringUtils.isNotEmpty(queryParamMap.get(ReceivingInfoRequestQueryParameters.CONTROLNUMBER.getQueryParam()))) {
//            criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(queryParamMap.get(ReceivingInfoRequestQueryParameters.CONTROLNUMBER.getQueryParam()));
//            query.addCriteria(criteriaDefinition);
//        }
//        if (StringUtils.isNotEmpty(queryParamMap.get(ReceivingInfoRequestQueryParameters.TRANSACTIONTYPE.getQueryParam()))) {
//            criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.TRANSACTIONTYPE.getParameterName()).is(queryParamMap.get(ReceivingInfoRequestQueryParameters.TRANSACTIONTYPE.getQueryParam()));
//            query.addCriteria(criteriaDefinition);
//        }
//        if (StringUtils.isNotEmpty(queryParamMap.get(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam()))) {
//            criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.STORENUMBER.getParameterName()).is(queryParamMap.get(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam()));
//            query.addCriteria(criteriaDefinition);
//        }
//        if (StringUtils.isNotEmpty(queryParamMap.get(ReceivingInfoRequestQueryParameters.DIVISIONNUMBER.getQueryParam()))) {
//            criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.BASEDIVISIONNUMBER.getParameterName()).is(queryParamMap.get(ReceivingInfoRequestQueryParameters.DIVISIONNUMBER.getQueryParam()));
//            query.addCriteria(criteriaDefinition);
//        }
//        if (StringUtils.isNotEmpty(queryParamMap.get(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam()))) {
//            criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.VENDORNUMBER.getParameterName()).is(queryParamMap.get(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam()));
//            query.addCriteria(criteriaDefinition);
//        }
//        if (StringUtils.isNotEmpty(queryParamMap.get(ReceivingInfoRequestQueryParameters.DEPARTMENTNUMBER.getQueryParam()))) {
//            criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.DEPARTMENTNUMBER.getParameterName()).is(queryParamMap.get(ReceivingInfoRequestQueryParameters.DEPARTMENTNUMBER.getQueryParam()));
//            query.addCriteria(criteriaDefinition);
//        }
//        if (StringUtils.isNotEmpty(queryParamMap.get(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam()))
//                && StringUtils.isNotEmpty(queryParamMap.get(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam()))) {
//            try {
//                DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeParser();
//                DateTime startDate = dateTimeFormatter.parseDateTime(queryParamMap.get(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam()));
//                DateTime endDate = dateTimeFormatter.parseDateTime(queryParamMap.get(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam()));
//                criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.RECEIVINGDATE.getParameterName()).gte(startDate).lte(endDate);
//                query.addCriteria(criteriaDefinition);
//            } catch (IllegalArgumentException e) {
//                log.error(ExceptionUtils.getStackTrace(e));
//                throw new BadRequestException("Date format is not correct.", "please enter valid query parameters");
//            }
//        }
//        log.info("getSummaryQuery :: Query is " + query);
//        return criteriaDefinition == null ? null : query;
//    }
    /*************************** Normal-Flow : END ***************************/

    /*************************** General Methods ***********************************/

    private List<ReceiveSummary> executeQueryInSummary(Query query) {
        List<ReceiveSummary> receiveSummaries = new ArrayList<>();
        if (query != null) {
            ReceiveSummary receiveSummary = mongoTemplate.findById(query, ReceiveSummary.class, summaryCollection);
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
    private List<ReceivingLine> getLineData(ReceiveSummary receiveSummary, Map<String, String> allRequestParams) {
        Query query = new Query();
        CriteriaDefinition criteriaDefinition = null;
        if (StringUtils.isNotEmpty(receiveSummary.get_id())) {
            criteriaDefinition = Criteria.where(ReceivingLineParameters.SUMMARYREFERENCE.getParameterName()).is(receiveSummary.get_id());
            query.addCriteria(criteriaDefinition);
        }
        log.info("queryForLineResponse :: Query is " + query);
        return executeQueryInLine(criteriaDefinition == null ? null : query);
    }
    /******* receive-line data   *********/

    /******* receive-freight data *********/
    private List<FreightResponse> getFreightData(ReceiveSummary receiveSummary) {
        if (receiveSummary.getFreightBillExpandID() != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(receiveSummary.getFreightBillExpandID()));
            return executeQueryInFreight(query);
        }
        return null;
    }
    /******* receive-freight data   *********/

    /*************************** Conversion Methods ***********************************/
    private ReceivingInfoResponse convertsionToReceivingInfo(ReceiveSummary receiveSummary, FinancialTxnResponseData financialTxnResponseData, List<ReceivingLine> lineResponseList, List<FreightResponse> freightResponseList, Map<String, String> allRequestParams) {
        ReceivingInfoResponse receivingInfoResponse = new ReceivingInfoResponse();
        if (financialTxnResponseData != null) {
            receivingInfoResponse.setAuthorizedBy(financialTxnResponseData.getAuthorizedBy());
            receivingInfoResponse.setAuthorizedDate(financialTxnResponseData.getAuthorizedDate() != null ?
                    financialTxnResponseData.getAuthorizedDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate() : null );
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
        receivingInfoResponse.setLineCount(new Long(lineResponseList.size()));
        receivingInfoResponse.setCarrierCode(CollectionUtils.isNotEmpty(freightResponseList) ? freightResponseList.get(0).getCarrierCode() : null);
        receivingInfoResponse.setTrailerNumber(CollectionUtils.isNotEmpty(freightResponseList) ? freightResponseList.get(0).getTrailerNbr() : null);
        receivingInfoResponse.setControlNumber(receiveSummary.getReceivingControlNumber());
        receivingInfoResponse.setTransactionType(receiveSummary.getTransactionType());
        receivingInfoResponse.setLocationNumber(receiveSummary.getStoreNumber());
        receivingInfoResponse.setPurchaseOrderId(receiveSummary.getPurchaseOrderId());
        receivingInfoResponse.setReceiptDate(receiveSummary.getReceivingDate());
        receivingInfoResponse.setReceiptNumber(StringUtils.isNotEmpty(receiveSummary.getReceiveId()) ? Long.valueOf(receiveSummary.getReceiveId()) : 0);
        receivingInfoResponse.setTotalCostAmount(receiveSummary.getTotalCostAmount());
        receivingInfoResponse.setTotalRetailAmount(receiveSummary.getTotalRetailAmount());
        receivingInfoResponse.setBottleDepositAmount(receiveSummary.getBottleDepositAmount());
        receivingInfoResponse.setControlSequenceNumber(receiveSummary.getControlSequenceNumber());
        receivingInfoResponse.setReceiptStatus(receiveSummary.getBusinessStatusCode() != null ? receiveSummary.getBusinessStatusCode().toString() : null);
        if (StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.LINENUMBERFLAG.getQueryParam()))
                && allRequestParams.get(ReceivingInfoRequestQueryParameters.LINENUMBERFLAG.getQueryParam()).equalsIgnoreCase("Y")) {
            List<ReceivingInfoLineResponse> lineInfoList = lineResponseList.stream().map((t) -> convertToLineResponse(t)).collect(Collectors.toList());
            receivingInfoResponse.setReceivingInfoLineResponses(lineInfoList);
        }
        return receivingInfoResponse;
    }

    private ReceivingInfoLineResponse convertToLineResponse(ReceivingLine receivingLine) {
        ReceivingInfoLineResponse response = new ReceivingInfoLineResponse();
        response.setReceiptNumber(Long.valueOf(receivingLine.getReceiveId()));
        response.setReceiptLineNumber(receivingLine.getLineSequenceNumber());
        response.setItemNumber(receivingLine.getItemNumber());
        response.setQuantity(receivingLine.getReceivedQuantity());
        response.setEachCostAmount(receivingLine.getCostAmount());
        response.setEachRetailAmount(receivingLine.getRetailAmount());
        response.setNumberofCasesReceived(receivingLine.getReceivedQuantity());
        response.setPackQuantity(receivingLine.getQuantity());
        response.setBottleDepositFlag(receivingLine.getBottleDepositFlag());
        response.setUpc(receivingLine.getUpcNumber());
        response.setItemDescription(receivingLine.getItemDescription());
        response.setUnitOfMeasure(receivingLine.getReceivedQuantityUnitOfMeasureCode());
        response.setVariableWeightInd(receivingLine.getVariableWeightIndicator());
        response.setCostMultiple(receivingLine.getCostMultiple());
        response.setReceivedWeightQuantity(receivingLine.getReceivedWeightQuantity() == null ? null : receivingLine.getReceivedWeightQuantity().toString());
        if (StringUtils.isNotEmpty(receivingLine.getMerchandises())) {
            JsonObject jsonObject = gson.fromJson(receivingLine.getMerchandises(), JsonObject.class);
            response.setMerchandises(new ArrayList<>());
            for (Map.Entry<String, JsonElement> jsonElementEntry : jsonObject.entrySet()) {
                JsonObject innerJsonObject = (JsonObject) jsonElementEntry.getValue();
                ReceiveMDSResponse receiveMDSResponse = new ReceiveMDSResponse(
                        innerJsonObject.get("mdseConditionCode").getAsInt(),
                        innerJsonObject.get("mdseQuantity").getAsInt(),
                        innerJsonObject.get("mdseQuantityUnitOfMeasureCode").getAsString());
                response.getMerchandises().add(receiveMDSResponse);
            }
        }
        return response;
    }
    /*************************** Conversion Methods ***********************************/
}
