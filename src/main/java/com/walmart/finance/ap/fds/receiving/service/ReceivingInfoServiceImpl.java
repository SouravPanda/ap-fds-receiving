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
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummaryCosmosDBParameters;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLineParameters;
import com.walmart.finance.ap.fds.receiving.response.*;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
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

    Gson gson = new Gson();

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
                List<FreightResponse> freightResponseList = getFreightData(receiveSummaries.get(0));
                ReceivingInfoResponse receivingInfoResponse = convertsionToReceivingInfo(receiveSummaries.get(0), financialTxnResponseData, lineResponseList, freightResponseList, allRequestParams);
                receivingInfoResponses.add(receivingInfoResponse);
            }
        }
        return receivingInfoResponses;
    }

    private List<ReceiveSummary> getSummaryData(FinancialTxnResponseData financialTxnResponseData, Map<String, String> allRequestParams) {
        String id = (financialTxnResponseData.getPurchaseOrderId() == null ? 0 : financialTxnResponseData.getPurchaseOrderId()) + ReceivingConstants.PIPE_SEPARATOR
                + (StringUtils.isEmpty(financialTxnResponseData.getReceiveId()) ? "0" : financialTxnResponseData.getReceiveId()) + ReceivingConstants.PIPE_SEPARATOR
                + (financialTxnResponseData.getStoreNumber() == null ? 0 : financialTxnResponseData.getStoreNumber()) + ReceivingConstants.PIPE_SEPARATOR
                + (financialTxnResponseData.getReceivingDate() == null ? "0" : financialTxnResponseData.getReceivingDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate());
        Query query = new Query();
        CriteriaDefinition criteriaDefinition = null;
        if (StringUtils.isNotEmpty(id)) {
            criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.ID.getParameterName()).is(id);
            query.addCriteria(criteriaDefinition);
        }
        if (financialTxnResponseData.getStoreNumber() != null) {
            criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.STORENUMBER.getParameterName()).is(financialTxnResponseData.getStoreNumber());
            query.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam()))
                && StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam()))) {
            criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.RECEIVINGDATE.getParameterName()).
                    gte(getDate(allRequestParams.get(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam()))).
                    lte(getDate(allRequestParams.get(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam())));
            query.addCriteria(criteriaDefinition);
        }
        log.info("queryForSummaryResponse :: Query is " + query);
        return executeQueryInSummary(query);
    }

    private LocalDate getDate(String date) {
        try {
            if (null != date && !"null".equals(date)) {
                DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDate.parse(date, formatterDate);
            }
        } catch (DateTimeParseException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new BadRequestException("Date format is not correct.", "Please enter valid query parameters");
        }
        return null;
    }
    /*************************** Financial-Txn Logic : END ***************************/

    /*************************** General Methods ***********************************/

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
        List<FreightResponse> receiveFreights = new ArrayList<>();
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
        if (receiveSummary.getStoreNumber() != null) {
            criteriaDefinition = Criteria.where(ReceivingLineParameters.STORENUMBER.getParameterName()).is(receiveSummary.getStoreNumber());
            query.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam()))) {
            List<String> itemNumbers = Arrays.asList(allRequestParams.get(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam()).split(","));
            criteriaDefinition = Criteria.where(ReceivingLineParameters.ITEMNUMBER.getParameterName()).in(itemNumbers.stream().map(Integer::parseInt).collect(Collectors.toList()));
            query.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam()))) {
            criteriaDefinition = Criteria.where(ReceivingLineParameters.UPCNUMBER.getParameterName()).in(Arrays.asList(allRequestParams.get(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam()).split(",")));
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
        receivingInfoResponse.setLineCount(CollectionUtils.isNotEmpty(lineResponseList) ? new Long(lineResponseList.size()) : 0);
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
        response.setReceiptNumber(StringUtils.isNotEmpty(receivingLine.getReceiveId()) ? Long.valueOf(receivingLine.getReceiveId()) : 0);
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

    /*************************** Version 1 Methods ***********************************/

    @Override
    public ReceivingResponse getInfoSeviceDataV1(Map<String, String> allRequestParams) {
        List<FinancialTxnResponseData> financialTxnResponseDataList = financialTxnIntegrationService.getFinancialTxnDetails(allRequestParams);
        List<ReceivingInfoResponseV1> receivingInfoResponses = getDataForFinancialTxnV1(financialTxnResponseDataList, allRequestParams);
        if (CollectionUtils.isEmpty(receivingInfoResponses)) {
            throw new NotFoundException("Receiving data not found for given search criteria.", "please enter valid query parameters");
        }
        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(receivingInfoResponses);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.now());
        return successMessage;
    }

    private List<ReceivingInfoResponseV1> getDataForFinancialTxnV1(List<FinancialTxnResponseData> financialTxnResponseDataList, Map<String, String> allRequestParams) {
        List<ReceivingInfoResponseV1> receivingInfoResponses = new ArrayList<>();
        for (FinancialTxnResponseData financialTxnResponseData : financialTxnResponseDataList) {
            List<ReceiveSummary> receiveSummaries = getSummaryData(financialTxnResponseData, allRequestParams);
            if (CollectionUtils.isNotEmpty(receiveSummaries)) {
                List<ReceivingLine> lineResponseList = getLineData(receiveSummaries.get(0), allRequestParams);
                List<FreightResponse> freightResponseList = getFreightData(receiveSummaries.get(0));
                ReceivingInfoResponseV1 receivingInfoResponseV1 = convertsionToReceivingInfoV1(receiveSummaries.get(0), financialTxnResponseData, lineResponseList, freightResponseList, allRequestParams);
                receivingInfoResponses.add(receivingInfoResponseV1);
            }
        }
        return receivingInfoResponses;
    }

    private ReceivingInfoResponseV1 convertsionToReceivingInfoV1(ReceiveSummary receiveSummary, FinancialTxnResponseData financialTxnResponseData, List<ReceivingLine> lineResponseList, List<FreightResponse> freightResponseList, Map<String, String> allRequestParams) {
        ReceivingInfoResponseV1 receivingInfoResponseV1 = new ReceivingInfoResponseV1();
        if (financialTxnResponseData != null) {
            receivingInfoResponseV1.setAuthorizedBy(financialTxnResponseData.getAuthorizedBy());
            receivingInfoResponseV1.setAuthorizedDate(financialTxnResponseData.getAuthorizedDate() != null ?
                    financialTxnResponseData.getAuthorizedDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate() : null);
            receivingInfoResponseV1.setDepartmentNumber(financialTxnResponseData.getDepartmentNumber());
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
            receivingInfoResponseV1.setTxnControlNbr(StringUtils.isNotEmpty(financialTxnResponseData.getTxnControlNbr()) ? Integer.valueOf(financialTxnResponseData.getTxnControlNbr()) : 0);
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
                    financialTxnResponseData.getInvoiceFinTransProcessLogs().stream().map(t ->
                            new InvoiceFinTransProcessLogs(
                                    t.getActionIndicator(),
                                    t.getMemoComments(),
                                    t.getProcessStatusCode(),
                                    t.getProcessStatusTimestamp() == null ? null : t.getProcessStatusTimestamp().toInstant().atZone(ZoneId.of("GMT")).toLocalDate(),
                                    t.getStatusUserId()))
                            .collect(Collectors.toList()));
            receivingInfoResponseV1.setInvoiceFinTransAdjustLogs(financialTxnResponseData.getInvoiceFinTransAdjustLogs().stream().map(t ->
                            new InvoiceFinTransAdjustLogs(t.getAdjustmentNbr(), t.getCostAdjustAmt(),
                            t.getCreateTs() == null ? null : t.getCreateTs().toInstant().atZone(ZoneId.of("GMT")).toLocalDate(),
                            t.getCreateUserId(),
                            t.getDueDate() == null ? null : t.getDueDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate(),
                            t.getOrigTxnCostAmt(),
                            t.getPostDate() == null ? null : t.getPostDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate(),
                            t.getTransactionDate() == null ? null : t.getTransactionDate().toInstant().atZone(ZoneId.of("GMT")).toLocalDate()))
                    .collect(Collectors.toList()));
            receivingInfoResponseV1.setInvoiceFinDelNoteChangeLogs(financialTxnResponseData.getInvoiceFinDelNoteChangeLogs().stream().map( t ->
                    new InvoiceFinDelNoteChangeLogs(
                            t.getChangeTimestamp() == null ? null : t.getChangeTimestamp().toInstant().atZone(ZoneId.of("GMT")).toLocalDate(),
                            t.getChangeUserId()
                            ,t.getDeliveryNoteId()
                            ,t.getOrgDelNoteId()
                    ))
                    .collect(Collectors.toList()));
        }
        receivingInfoResponseV1.setLineCount(CollectionUtils.isNotEmpty(lineResponseList) ? new Long(lineResponseList.size()) : 0);
        receivingInfoResponseV1.setCarrierCode(CollectionUtils.isNotEmpty(freightResponseList) ? freightResponseList.get(0).getCarrierCode() : null);
        receivingInfoResponseV1.setTrailerNumber(CollectionUtils.isNotEmpty(freightResponseList) ? freightResponseList.get(0).getTrailerNbr() : null);
        receivingInfoResponseV1.setControlNumber(receiveSummary.getReceivingControlNumber());
        receivingInfoResponseV1.setTransactionType(receiveSummary.getTransactionType());
        receivingInfoResponseV1.setLocationNumber(receiveSummary.getStoreNumber());
        receivingInfoResponseV1.setPurchaseOrderId(receiveSummary.getPurchaseOrderId());
        receivingInfoResponseV1.setReceiptDate(receiveSummary.getReceivingDate());
        receivingInfoResponseV1.setReceiptNumber(StringUtils.isNotEmpty(receiveSummary.getReceiveId()) ? Long.valueOf(receiveSummary.getReceiveId()) : 0);
        receivingInfoResponseV1.setTotalCostAmount(receiveSummary.getTotalCostAmount());
        receivingInfoResponseV1.setTotalRetailAmount(receiveSummary.getTotalRetailAmount());
        receivingInfoResponseV1.setBottleDepositAmount(receiveSummary.getBottleDepositAmount());
        receivingInfoResponseV1.setControlSequenceNumber(receiveSummary.getControlSequenceNumber());
        receivingInfoResponseV1.setReceiptStatus(receiveSummary.getBusinessStatusCode() != null ? receiveSummary.getBusinessStatusCode().toString() : null);
        if (StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.LINENUMBERFLAG.getQueryParam()))
                && allRequestParams.get(ReceivingInfoRequestQueryParameters.LINENUMBERFLAG.getQueryParam()).equalsIgnoreCase("Y")) {
            List<ReceivingInfoLineResponse> lineInfoList = lineResponseList.stream().map((t) -> convertToLineResponse(t)).collect(Collectors.toList());
            receivingInfoResponseV1.setReceivingInfoLineResponses(lineInfoList);
        }
        return receivingInfoResponseV1;
    }
    /*************************** Version 1 Methods ***********************************/

}
