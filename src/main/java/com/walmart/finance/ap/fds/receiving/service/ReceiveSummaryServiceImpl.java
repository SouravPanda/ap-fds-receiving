package com.walmart.finance.ap.fds.receiving.service;


import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.common.DB2SyncStatus;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryReqConverter;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
import com.walmart.finance.ap.fds.receiving.exception.*;
import com.walmart.finance.ap.fds.receiving.integrations.*;
import com.walmart.finance.ap.fds.receiving.model.*;
import com.walmart.finance.ap.fds.receiving.repository.ReceiveSummaryDataRepository;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryLineValidator;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ReceiveSummaryServiceImpl implements ReceiveSummaryService {

    public static final Logger log = LoggerFactory.getLogger(ReceiveSummaryServiceImpl.class);

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ReceivingSummaryResponseConverter receivingSummaryResponseConverter;

    @Autowired
    InvoiceIntegrationService invoiceIntegrationService;

    @Autowired
    ReceiveSummaryValidator receiveSummaryValidator;

    @Autowired
    ReceiveSummaryLineValidator receiveSummaryLineValidator;


    @Autowired
    private ApplicationEventPublisher publisher;

    @Value("${azure.cosmosdb.collection.summary}")
    private String summaryCollection;

    @Value("${azure.cosmosdb.collection.line}")
    private String lineCollection;

    @Value("${azure.cosmosdb.collection.freight}")
    private String freightCollection;

    /**
     * Service layer to get the data based on the requested parameters and return pageable response.
     *
     * @param countryCode
     * @param purchaseOrderNumber
     * @param purchaseOrderId
     * @param receiptNumbers
     * @param transactionType
     * @param controlNumber
     * @param locationNumber
     * @param divisionNumber
     * @param vendorNumber
     * @param departmentNumber
     * @param invoiceId
     * @param invoiceNumber
     * @param receiptDateStart
     * @param receiptDateEnd
     * @param itemNumbers
     * @param upcNumbers
     * @return
     */

    public ReceivingResponse getReceiveSummary(String countryCode, String purchaseOrderNumber, String purchaseOrderId, List<String> receiptNumbers, String transactionType, String controlNumber, String locationNumber,
                                               String divisionNumber, String vendorNumber, String departmentNumber, String invoiceId, String invoiceNumber, String receiptDateStart, String receiptDateEnd, List<String> itemNumbers, List<String> upcNumbers) {// Map<String,String> allRequestParam) {
        HashMap<String, String> paramMap = checkingNotNullParameters(countryCode, purchaseOrderNumber, purchaseOrderId, receiptNumbers, transactionType, controlNumber, locationNumber,
                divisionNumber, vendorNumber, departmentNumber, invoiceId, invoiceNumber, receiptDateStart, receiptDateEnd);
        List<ReceiveSummary> receiveSummaries;
        List<ReceivingSummaryResponse> responseList;
        try {
            if (paramMap.containsKey(ReceivingConstants.INVOICENUMBER) || paramMap.containsKey(ReceivingConstants.INVOICEID) || paramMap.containsKey(ReceivingConstants.PURCHASEORDERNUMBER)) {
                receiveSummaries = getInvoiceFromInvoiceSummary(paramMap);
                if (paramMap.containsKey((ReceivingConstants.PURCHASEORDERNUMBER)) && receiveSummaries.isEmpty()) {
                    receiveSummaries = getSearchCriteriaForGet(paramMap);
                }
            } else {
                receiveSummaries = getSearchCriteriaForGet(paramMap);
            }
            log.info(ReceivingLogs.BEFORESIZESUMMARY.getParameterName() + receiveSummaries.size());
            if (CollectionUtils.isNotEmpty(receiveSummaries) && receiveSummaries.size() > 1000) {
                receiveSummaries.subList(1000, receiveSummaries.size()).clear();
            }
            log.info(ReceivingLogs.AFTERSIZESUMMARY.getParameterName() + receiveSummaries.size());
            Map<String, AdditionalResponse> responseMap = getLineResponseMap(receiveSummaries, itemNumbers, upcNumbers);
            //Todo parallel stream performance check
            if (CollectionUtils.isEmpty(receiveSummaries)) {
                throw new NotFoundException(ReceivingErrors.CONTENTNOTFOUNDSUMMARY.getParameterName(), ReceivingErrors.INVALIDQUERYPARAMS.getParameterName());
            } else {
                responseList = receiveSummaries.stream().map(
                        t -> {
                            ReceivingSummaryResponse response = receivingSummaryResponseConverter.convert(t);
                            if (responseMap.get(t.get_id()) != null) {
                                response.setCarrierCode(responseMap.get(t.get_id()).getCarrierCode());
                                response.setTrailerNumber(responseMap.get(t.get_id()).getTrailerNumber());
                                response.setLineCount(responseMap.get(t.get_id()).getLineCount());
                                response.setTotalCostAmount(responseMap.get(t.get_id()).getTotalCostAmount());
                                response.setTotalRetailAmount(responseMap.get(t.get_id()).getTotalRetailAmount());
                            }
                            return response;
                        }
                ).collect(Collectors.toList());
                ReceivingResponse successMessage = new ReceivingResponse();
                successMessage.setData(responseList);
                successMessage.setSuccess(true);
                successMessage.setTimestamp(LocalDateTime.now());
                return successMessage;
            }
        } catch (NumberFormatException e) {
            log.error(ExceptionUtils.getStackTrace(e));//TODO
            throw new BadRequestException(ReceivingErrors.INVALIDDATATYPE.getParameterName(),ReceivingErrors.INVALIDQUERYPARAMS.getParameterName());
        }
    }

    private String formulateLineId(String receivingControlNumber, String poReceiveId, String storeNumber, String receiptDate, String lineSequenceNumber) {
        return receivingControlNumber + ReceivingConstants.PIPE_SEPARATOR + poReceiveId + ReceivingConstants.PIPE_SEPARATOR + storeNumber + ReceivingConstants.PIPE_SEPARATOR + receiptDate + ReceivingConstants.PIPE_SEPARATOR + lineSequenceNumber;
    }

    private HashMap<String, String> checkingNotNullParameters(String countryCode, String purchaseOrderNumber, String purchaseOrderId, List<String> receiptNumber, String transactionType, String controlNumber, String locationNumber, String divisionNumber,
                                                              String vendorNumber, String departmentNumber, String invoiceId, String invoiceNumber, String receiptDateStart, String receiptDateEnd) {
        HashMap<String, String> paramMap = new HashMap<>();

        if (StringUtils.isNotEmpty(countryCode)) {
            paramMap.put(ReceivingConstants.COUNTRYCODE, countryCode);
        }
        if (StringUtils.isNotEmpty(purchaseOrderId)) {
            paramMap.put(ReceivingConstants.PURCHASEORDERID, purchaseOrderId);
        }
        if (StringUtils.isNotEmpty(purchaseOrderNumber)) {
            paramMap.put(ReceivingConstants.PURCHASEORDERNUMBER, purchaseOrderNumber);
        }
        if (receiptNumber != null && !receiptNumber.isEmpty()) {
            paramMap.put(ReceivingConstants.RECEIPTNUMBER, receiptNumber.get(0));
        }
        if (StringUtils.isNotEmpty(transactionType)) {
            paramMap.put(ReceivingConstants.TRANSACTIONTYPE, transactionType);
        }
        if (StringUtils.isNotEmpty(controlNumber)) {
            paramMap.put(ReceivingConstants.CONTROLNUMBER, controlNumber);
        }
        if (StringUtils.isNotEmpty(locationNumber)) {
            paramMap.put(ReceivingConstants.LOCATIONNUMBER, locationNumber);
        }
        if (StringUtils.isNotEmpty(divisionNumber)) {
            paramMap.put(ReceivingConstants.DIVISIONNUMBER, divisionNumber);
        }
        if (StringUtils.isNotEmpty(vendorNumber)) {
            paramMap.put(ReceivingConstants.VENDORNUMBER, vendorNumber);
        }
        if (StringUtils.isNotEmpty(departmentNumber)) {
            paramMap.put(ReceivingConstants.DEPARTMENTNUMBER, departmentNumber);
        }
        if (StringUtils.isNotEmpty(invoiceId)) {
            paramMap.put(ReceivingConstants.INVOICEID, invoiceId);
        }
        if (StringUtils.isNotEmpty(invoiceNumber)) {
            paramMap.put(ReceivingConstants.INVOICENUMBER, invoiceNumber);
        }
        if (StringUtils.isNotEmpty(receiptDateStart)) {
            paramMap.put(ReceivingConstants.RECEIPTDATESTART, receiptDateStart);
        }
        if (StringUtils.isNotEmpty(receiptDateEnd)) {
            paramMap.put(ReceivingConstants.RECEIPTDATEEND, receiptDateEnd);
        }
        return paramMap;
    }

    private String formulateId(String receivingControlNumber, String poReceiveId, String locationNumber, String MDSReceiveDate) {
        return receivingControlNumber + ReceivingConstants.PIPE_SEPARATOR + poReceiveId + ReceivingConstants.PIPE_SEPARATOR + locationNumber + ReceivingConstants.PIPE_SEPARATOR + MDSReceiveDate;

    }

    /*******  Search Criteria methods  *********/

    private List<ReceiveSummary> getSearchCriteriaForGet(HashMap<String, String> paramMap) {
        log.info(ReceivingLogs.SEARCHCRITERIAFORGET.getParameterName());
        Query query = searchCriteriaForGet(paramMap);
        return executeQueryForReceiveSummary(query);
    }

    private Query searchCriteriaForGet(HashMap<String, String> paramMap) {
        Query dynamicQuery = new Query();
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.CONTROLNUMBER))) {
            Criteria controlNumberCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(paramMap.get(ReceivingConstants.CONTROLNUMBER));
            dynamicQuery.addCriteria(controlNumberCriteria);
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.PURCHASEORDERID))) {
            Criteria purchaseOrderIdCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.PURCHASEORDERID.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.PURCHASEORDERID)));
            dynamicQuery.addCriteria(purchaseOrderIdCriteria);
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.DIVISIONNUMBER))) {
            Criteria baseDivisionNumberCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.BASEDIVISIONNUMBER.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.DIVISIONNUMBER)));
            dynamicQuery.addCriteria(baseDivisionNumberCriteria);
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.RECEIPTDATESTART)) && StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.RECEIPTDATEEND))) {
            Criteria mdsReceiveDateCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.RECEIVINGDATE.getParameterName()).gte(getDate(paramMap.get(ReceivingConstants.RECEIPTDATESTART))).lte(getDate(paramMap.get(ReceivingConstants.RECEIPTDATEEND)));

            dynamicQuery.addCriteria(mdsReceiveDateCriteria);
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.TRANSACTIONTYPE))) {
            Criteria transactionTypeCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.TRANSACTIONTYPE.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.TRANSACTIONTYPE)));
            dynamicQuery.addCriteria(transactionTypeCriteria);
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.LOCATIONNUMBER))) {
            Criteria storeNumberCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.STORENUMBER.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.LOCATIONNUMBER)));
            dynamicQuery.addCriteria(storeNumberCriteria);
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.PURCHASEORDERNUMBER))) {
            Criteria purchaseOrderNumberCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.PURCHASEORDERNUMBER.getParameterName()).is(paramMap.get(ReceivingConstants.PURCHASEORDERNUMBER));
            dynamicQuery.addCriteria(purchaseOrderNumberCriteria);
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.RECEIPTNUMBER))) {
            Criteria poReceiveIdCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.RECEIVEID.getParameterName()).is(paramMap.get(ReceivingConstants.RECEIPTNUMBER));
            dynamicQuery.addCriteria(poReceiveIdCriteria);
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.DEPARTMENTNUMBER))) {
            Criteria departmentNumberCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.DEPARTMENTNUMBER.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.DEPARTMENTNUMBER)));
            dynamicQuery.addCriteria(departmentNumberCriteria);
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.VENDORNUMBER))) {
            Criteria vendorNumberCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.VENDORNUMBER.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.VENDORNUMBER)));
            dynamicQuery.addCriteria(vendorNumberCriteria);
        }

        log.info("query: " + dynamicQuery);
        return dynamicQuery;
    }

    public LocalDate getDate(String date) {
        try {
            if (null != date && !"null".equals(date)) {
                DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDate.parse(date, formatterDate);
            }
        } catch (DateTimeParseException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new BadRequestException(ReceivingErrors.INVALIDDATATYPE.getParameterName(),ReceivingErrors.INVALIDQUERYPARAMS.getParameterName());
        }
        return null;
    }
    /*******  Search Criteria methods  *********/


    /******* Invoice Summary Integration *********/

    private List<ReceiveSummary> getInvoiceFromInvoiceSummary(HashMap<String, String> paramMap) {
        log.info(ReceivingLogs.INVOICEFROMINVSUMMARY.getParameterName());
        List<InvoiceResponseData> invoiceResponseDataList = invoiceIntegrationService.getInvoice(paramMap);
        HashMap<String, ReceiveSummary> receiveSummaryHashMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(invoiceResponseDataList)) {
            for (InvoiceResponseData invoiceResponseData : invoiceResponseDataList) {
                listToMapConversion(callRecvSmryAllAttributes(invoiceResponseData), receiveSummaryHashMap);
                listToMapConversion(callRecvSmryByPOId(invoiceResponseData), receiveSummaryHashMap);
                listToMapConversion(callRecvSmryByInvoiceNum(invoiceResponseData), receiveSummaryHashMap);
            }
        }
        return receiveSummaryHashMap.values().stream().collect(Collectors.toList());

    }

    private List<ReceiveSummary> callRecvSmryByInvoiceNum(InvoiceResponseData invoiceResponseData) {
        Query query = queryRecvSmryByInvoiceNum(invoiceResponseData);
        return executeQueryForReceiveSummary(query);
    }

    private Query queryRecvSmryByInvoiceNum(InvoiceResponseData invoiceResponseData) {
        Query query = null;
        if (StringUtils.isNotEmpty(invoiceResponseData.getInvoiceNumber())) {
            query = new Query();
            query.addCriteria(Criteria.where(ReceiveSummaryCosmosDBParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(invoiceResponseData.getInvoiceNumber().trim()));
            query.addCriteria(Criteria.where(ReceiveSummaryCosmosDBParameters.TRANSACTIONTYPE.getParameterName()).is(1));
        }
        log.info("query: " + query);
        return query;
    }

    private List<ReceiveSummary> callRecvSmryByPOId(InvoiceResponseData invoiceResponseData) {
        Query query = queryRecvSmryByPOId(invoiceResponseData);
        return executeQueryForReceiveSummary(query);
    }

    private Query queryRecvSmryByPOId(InvoiceResponseData invoiceResponseData) {
        Query query = null;
        if (StringUtils.isNotEmpty(invoiceResponseData.getPurchaseOrderNumber())) {
            query = new Query();
            query.addCriteria(Criteria.where(ReceiveSummaryCosmosDBParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(invoiceResponseData.getPurchaseOrderNumber().trim()));
            query.addCriteria(Criteria.where(ReceiveSummaryCosmosDBParameters.TRANSACTIONTYPE.getParameterName()).is(0));
        }
        log.info("query: " + query);
        return query;
    }

    private List<ReceiveSummary> callRecvSmryAllAttributes(InvoiceResponseData invoiceResponseData) {
        Query query = queryRecvSmryAllAttributes(invoiceResponseData);
        return executeQueryForReceiveSummary(query);
    }

    // TODO       addCriteria( "x", invoiceResponse.getReceivingNum(),query);
    private Query queryRecvSmryAllAttributes(InvoiceResponseData invoiceResponseData) {
        Query query = new Query();
        CriteriaDefinition criteriaDefinition = null;
        if (StringUtils.isNotEmpty(invoiceResponseData.getPurchaseOrderNumber())) {
            criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.PURCHASEORDERNUMBER.getParameterName()).is(invoiceResponseData.getPurchaseOrderNumber().trim());
            query.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(invoiceResponseData.getPurchaseOrderId())) {
            criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.PURCHASEORDERID.getParameterName()).is(Integer.parseInt(invoiceResponseData.getPurchaseOrderId().trim()));
            query.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(invoiceResponseData.getDestDivNbr())) {
            criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.STORENUMBER.getParameterName()).is(Integer.parseInt(invoiceResponseData.getDestStoreNbr().trim()));
            query.addCriteria(criteriaDefinition);
        }
        //TODO : According to conversion with Anurag, this has been commented (29/May/2019)

        //TODO  : Commented due to dilemma of 6 digits and 9 digits
        /*   if (StringUtils.isNotEmpty(invoiceResponse.getVendorNumber())) {
            query.addCriteria(Criteria.where(ReceiveSummaryCosmosDBParameters.VENDORNUMBER.getParameterName()).is(Integer.parseInt(invoiceResponse.getVendorNumber().trim())));
        }*/
        if (StringUtils.isNotEmpty(invoiceResponseData.getInvoiceDeptNumber())) {
            criteriaDefinition = Criteria.where(ReceiveSummaryCosmosDBParameters.DEPARTMENTNUMBER.getParameterName()).is(Integer.parseInt(invoiceResponseData.getInvoiceDeptNumber().trim()));
            query.addCriteria(criteriaDefinition);
        }
        log.info("query: " + query);
        return criteriaDefinition == null ? null : query;

    }

    /******* Invoice Summary Integration *********/


    /******* receive -line data fetching   *********/

    private Map<String, AdditionalResponse> getLineResponseMap(List<ReceiveSummary> receiveSummaries, List<String> itemNumbers, List<String> upcNumbers) {
        Map<String, AdditionalResponse> lineResponseMap = new HashMap<>();
        List<ReceivingLine> lineResponseList = new LinkedList<>();
        List<Criteria> criteriaList = new ArrayList<>();
        for (ReceiveSummary receiveSummary : receiveSummaries) {
            criteriaList.add(queryForLineResponse(receiveSummary, itemNumbers, upcNumbers));
        }
        if (CollectionUtils.isNotEmpty(criteriaList)) {
            Query query = new Query(new Criteria().orOperator(criteriaList.toArray(new Criteria[criteriaList.size()])));
//            query.with(new Sort(Sort.Direction.ASC,"_id"));
            log.info("query: " + query);
            lineResponseList = executeQueryReceiveline(query);
        }
        Map<String, List<ReceivingLine>> receivingLineMap = new HashMap<>();

        //Grouping lines according to PurchaseOrderReceiveID
        for (ReceivingLine receivingLine : lineResponseList) {
            if (receivingLineMap.containsKey(receivingLine.getReceiveId())) {
                List<ReceivingLine> lineList = receivingLineMap.get(receivingLine.getReceiveId());
                lineList.add(receivingLine);
                receivingLineMap.put(receivingLine.getReceiveId(), lineList);
            } else {
                List<ReceivingLine> lineList = new ArrayList<>();
                lineList.add(receivingLine);
                receivingLineMap.put(receivingLine.getReceiveId(), lineList);
            }
        }
        Iterator<ReceiveSummary> iterator = receiveSummaries.iterator();
        while (iterator.hasNext()) {
            ReceiveSummary receiveSummary = iterator.next();
            AdditionalResponse response = new AdditionalResponse();
            if (receivingLineMap.containsKey(receiveSummary.getReceiveId())) {
                List<ReceivingLine> lineList = receivingLineMap.get(receiveSummary.getReceiveId());
                if (receiveSummary.getTypeIndicator().equals("W")) {
                    response.setTotalCostAmount(lineResponseList.stream().mapToDouble(t -> t.getReceivedQuantity() * t.getCostAmount()).sum());
                    response.setTotalRetailAmount(lineResponseList.stream().mapToDouble(t -> t.getReceivedQuantity() * t.getRetailAmount()).sum());
                } else {
                    response.setTotalCostAmount(receiveSummary.getTotalCostAmount());
                    response.setTotalRetailAmount(receiveSummary.getTotalRetailAmount());
                }
                response.setLineCount((long) lineList.size());
                getFreightResponse(receiveSummary, response);
                lineResponseMap.put(receiveSummary.get_id(), response);
            } else if (CollectionUtils.isNotEmpty(itemNumbers) || CollectionUtils.isNotEmpty(upcNumbers)) {
                iterator.remove();
            } else {
                getFreightResponse(receiveSummary, response);
                response.setTotalCostAmount(receiveSummary.getTotalCostAmount());
                response.setTotalRetailAmount(receiveSummary.getTotalRetailAmount());
                lineResponseMap.put(receiveSummary.get_id(), response);
            }
        }
        return lineResponseMap;
    }

    private Criteria queryForLineResponse(ReceiveSummary receiveSummary, List<String> itemNumbers, List<String> upcNumbers) {
        Criteria criteriaDefinition = new Criteria();
        if (StringUtils.isNotEmpty(receiveSummary.getReceivingControlNumber())) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(receiveSummary.getReceivingControlNumber().trim());
        }
        if (StringUtils.isNotEmpty(receiveSummary.getReceiveId())) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.RECEIVEID.getParameterName()).is(receiveSummary.getReceiveId().trim());
        }
        if (receiveSummary.getStoreNumber() != null) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.STORENUMBER.getParameterName()).is(receiveSummary.getStoreNumber());
        }
        if (receiveSummary.getBaseDivisionNumber() != null) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.BASEDIVISIONNUMBER.getParameterName()).is(receiveSummary.getBaseDivisionNumber());
        }
        if (receiveSummary.getTransactionType() != null) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.TRANSACTIONTYPE.getParameterName()).is(receiveSummary.getTransactionType());
        }
        if (CollectionUtils.isNotEmpty(itemNumbers)) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.ITEMNUMBER.getParameterName()).in(itemNumbers.stream().map(Integer::parseInt).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(upcNumbers)) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.UPCNUMBER.getParameterName()).in(upcNumbers);
        }
        return criteriaDefinition != null ? criteriaDefinition : null;
    }
    /******* receive -line data fetching   *********/

    /******* receive -freight data fetching   *********/

    private void getFreightResponse(ReceiveSummary receiveSummary, AdditionalResponse additionalResponse) {

        List<FreightResponse> receiveFreights = makeQueryForFreight(receiveSummary);
        if (CollectionUtils.isNotEmpty(receiveFreights)) {
            additionalResponse.setCarrierCode(receiveFreights.get(0).getCarrierCode() == null ? null : receiveFreights.get(0).getCarrierCode().trim());
            additionalResponse.setTrailerNumber(receiveFreights.get(0).getTrailerNbr() == null ? null : receiveFreights.get(0).getTrailerNbr().trim());
        }
    }

    private List<FreightResponse> makeQueryForFreight(ReceiveSummary receiveSummary) {
        if (receiveSummary.getFreightBillExpandID() != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(receiveSummary.getFreightBillExpandID()));
            return executeQueryReceiveFreight(query);
        }
        return null;
    }

    /******* receive -freight data fetching   *********/

    /******* Common Methods  *********/

    private List<ReceiveSummary> executeQueryForReceiveSummary(Query query) {
        List<ReceiveSummary> receiveSummaries = new ArrayList<>();
        if (query != null) {
            receiveSummaries = mongoTemplate.find(query.limit(1000), ReceiveSummary.class, summaryCollection);
        }
        return receiveSummaries;
    }

    private List<ReceivingLine> executeQueryReceiveline(Query query) {
        List<ReceivingLine> receiveLines = new LinkedList<>();
        if (query != null) {
            receiveLines = mongoTemplate.find(query, ReceivingLine.class, lineCollection);
        }
        return receiveLines;
    }

    private List<FreightResponse> executeQueryReceiveFreight(Query query) {
        return mongoTemplate.find(query, FreightResponse.class, freightCollection);
    }

    private void listToMapConversion(List<ReceiveSummary> receiveSummaries, HashMap<String, ReceiveSummary> receiveSummaryHashMap) {
        receiveSummaries.stream().forEach(t -> receiveSummaryHashMap.put(t.get_id(), t));
    }

    /******* Common Methods  *********/

    private boolean isWareHouseData(Integer invProcAreaCode, String repInTypCd, String locationCountryCd) {

        if (StringUtils.isNotEmpty(locationCountryCd) && StringUtils.isNotEmpty(repInTypCd) && invProcAreaCode != null) {
            return (invProcAreaCode == 36 || invProcAreaCode == 30) && (repInTypCd.equals("R") || repInTypCd.equals("U") || repInTypCd.equals("F")) && (locationCountryCd.equals("US"));
        }
        return false;
    }

    @Override
    @Transactional
    public ReceivingResponse updateReceiveSummary(ReceivingSummaryRequest receivingSummaryRequest, String countryCode) {

        log.info("unitofWorkid:" + receivingSummaryRequest.getMeta().getUnitofWorkid());
        List<ReceivingSummaryRequest> responseList = new ArrayList<>();
        Boolean isWareHouseData = isWareHouseData(receivingSummaryRequest.getMeta().getSorRoutingCtx().getInvProcAreaCode(), receivingSummaryRequest.getMeta().getSorRoutingCtx().getReplnTypCd(),
                receivingSummaryRequest.getMeta().getSorRoutingCtx().getLocationCountryCd());

        String id;

        if (receivingSummaryRequest != null) {
            if (!receiveSummaryValidator.validateBusinessStatUpdateSummary(receivingSummaryRequest)) {
                throw new InvalidValueException(ReceivingErrors.INVALIDBUSINESSSTATUSCODE.getParameterName(), ReceivingErrors.BUSINESSSTATUSDETAILS.getParameterName());
            }
        }


        if (isWareHouseData == false) {
            id = formulateId(receivingSummaryRequest.getPurchaseOrderId(), receivingSummaryRequest.getReceiptNumber(), receivingSummaryRequest.getLocationNumber().toString(), receivingSummaryRequest.getReceiptDate().toString());
        } else {
            id = formulateId(receivingSummaryRequest.getPurchaseOrderId(), receivingSummaryRequest.getReceiptNumber(), receivingSummaryRequest.getLocationNumber().toString(), "0");
        }
        Query dynamicQuery = new Query();
        dynamicQuery.addCriteria(Criteria.where(ReceiveSummaryParameters.ID.getParameterName()).is(id));
        dynamicQuery.addCriteria(Criteria.where(ReceiveSummaryParameters.STORENUMBER.getParameterName()).is(receivingSummaryRequest.getLocationNumber()));
        Update update = new Update();
        update.set(ReceiveSummaryParameters.BUSINESSSTATUSCODE.getParameterName(), receivingSummaryRequest.getBusinessStatusCode().charAt(0));
        update.set(ReceiveSummaryParameters.DATASYNCSTATUS.getParameterName(), DB2SyncStatus.UPDATE_SYNC_INITIATED);
        update.set(ReceiveSummaryParameters.LASTUPDATEDDATE.getParameterName(), LocalDateTime.now());

        ReceiveSummary commitedRcvSummary = mongoTemplate.findAndModify(dynamicQuery, update, ReceiveSummary.class, summaryCollection);
        if (commitedRcvSummary == null) {
            throw new ContentNotFoundException(ReceivingErrors.CONTENTNOTFOUNDSUMMARY.getParameterName(), ReceivingErrors.VALIDID.getParameterName());
        }

        if (Objects.nonNull(commitedRcvSummary) && isWareHouseData) {
            publisher.publishEvent(commitedRcvSummary);
        }
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
        Boolean isWareHouseData = isWareHouseData(receivingSummaryLineRequest.getMeta().getSorRoutingCtx().getInvProcAreaCode(), receivingSummaryLineRequest.getMeta().getSorRoutingCtx().getReplnTypCd(),
                receivingSummaryLineRequest.getMeta().getSorRoutingCtx().getLocationCountryCd());
        Query dynamicQuery = new Query();
        ReceivingLine commitedRcvLine;
        ReceiveSummary commitedRcvSummary;
        String id;
        List<ReceivingSummaryLineRequest> responseList = new ArrayList<>();
        List summaryLineList = new ArrayList();
        log.info("unitofWorkid:" + receivingSummaryLineRequest.getMeta().getUnitofWorkid());

        if (!receiveSummaryLineValidator.validateBusinessStatUpdateSummary(receivingSummaryLineRequest)) {
            throw new InvalidValueException(ReceivingErrors.INVALIDBUSINESSSTATUSCODE.getParameterName(), ReceivingErrors.BUSINESSSTATUSDETAILS.getParameterName());
        }

        if (!receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest)) {
            throw new InvalidValueException(ReceivingErrors.INVALIDINVENTORYMATCHSTATUSCODE.getParameterName(), ReceivingErrors.INVALIDINVENTORYMATCHSTATUSDETAILS.getParameterName());
        }

        if (receivingSummaryLineRequest.getReceiptLineNumber() == null) {
            if (isWareHouseData == false) {
                id = formulateId(receivingSummaryLineRequest.getPurchaseOrderId(), receivingSummaryLineRequest.getReceiptNumber(), receivingSummaryLineRequest.getLocationNumber().toString(), receivingSummaryLineRequest.getReceiptDate().toString());
            } else {
                id = formulateId(receivingSummaryLineRequest.getPurchaseOrderId(), receivingSummaryLineRequest.getReceiptNumber(), receivingSummaryLineRequest.getLocationNumber().toString(), "0");
            }

            Query query = new Query();
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.ID.getParameterName()).is(id));
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.STORENUMBER.getParameterName()).is(receivingSummaryLineRequest.getLocationNumber()));

            Update update = new Update();
            update.set(ReceiveSummaryParameters.DATASYNCSTATUS.getParameterName(), DB2SyncStatus.UPDATE_SYNC_INITIATED);
            update.set(ReceiveSummaryParameters.LASTUPDATEDDATE.getParameterName(), LocalDateTime.now());
            update.set(ReceiveSummaryParameters.BUSINESSSTATUSCODE.getParameterName(), receivingSummaryLineRequest.getBusinessStatusCode().charAt(0));
            commitedRcvSummary = mongoTemplate.findAndModify(query, update, ReceiveSummary.class, summaryCollection);
            if (commitedRcvSummary == null) {
                throw new ContentNotFoundException(ReceivingErrors.CONTENTNOTFOUNDSUMMARY.getParameterName(), ReceivingErrors.VALIDID.getParameterName());
            }

            if (Objects.nonNull(commitedRcvSummary) && isWareHouseData) {
                summaryLineList.add(commitedRcvSummary);
            }

            if (StringUtils.isNotEmpty(id)) {
                Criteria summaryReferenceCriteria = Criteria.where(ReceivingLineParameters.SUMMARYREFERENCE.getParameterName()).is(id);
                dynamicQuery.addCriteria(summaryReferenceCriteria);

            }

            if (receivingSummaryLineRequest.getLocationNumber() != null) {
                Criteria locationNumberCriteria = Criteria.where(ReceivingLineParameters.STORENUMBER.getParameterName()).is(receivingSummaryLineRequest.getLocationNumber());
                dynamicQuery.addCriteria(locationNumberCriteria);
            }

            //TODO code needs to optimized remove the DB calls in loop
            List<ReceivingLine> receivingLineList = mongoTemplate.find(dynamicQuery, ReceivingLine.class, lineCollection);
            String lineId;
            for (ReceivingLine receivingLine : receivingLineList) {
                lineId = receivingLine.get_id();
                Query queryForLine = new Query();
                Update updateLine = new Update();
                updateLine.set(ReceivingLineParameters.DATASYNCSTATUS.getParameterName(), DB2SyncStatus.UPDATE_SYNC_INITIATED);
                updateLine.set(ReceivingLineParameters.LASTUPDATEDDATE.getParameterName(), LocalDateTime.now());
                queryForLine.addCriteria(Criteria.where(ReceivingLineParameters.STORENUMBER.getParameterName()).is(receivingSummaryLineRequest.getLocationNumber()));
                queryForLine.addCriteria(Criteria.where(ReceivingLineParameters.ID.getParameterName()).is(lineId));
                updateLine.set(ReceivingLineParameters.INVENTORYMATCHSTATUS.getParameterName(), Integer.parseInt(receivingSummaryLineRequest.getInventoryMatchStatus()));
                commitedRcvLine = mongoTemplate.findAndModify(queryForLine, updateLine, ReceivingLine.class, lineCollection);
                if (Objects.nonNull(commitedRcvLine) && isWareHouseData) {
                    summaryLineList.add(commitedRcvLine);
                }
            }
            publisher.publishEvent(summaryLineList);

        } else {
            String lineId;
            String summaryId;
            if (isWareHouseData == false) {
                summaryId = formulateId(receivingSummaryLineRequest.getPurchaseOrderId(), receivingSummaryLineRequest.getReceiptNumber(), receivingSummaryLineRequest.getLocationNumber().toString(), receivingSummaryLineRequest.getReceiptDate().toString());
                lineId = formulateLineId(receivingSummaryLineRequest.getPurchaseOrderId(), receivingSummaryLineRequest.getReceiptNumber(), receivingSummaryLineRequest.getLocationNumber().toString(),
                        receivingSummaryLineRequest.getReceiptDate().toString(), receivingSummaryLineRequest.getReceiptLineNumber().toString());
            } else {
                summaryId = formulateId(receivingSummaryLineRequest.getPurchaseOrderId(), receivingSummaryLineRequest.getReceiptNumber(), receivingSummaryLineRequest.getLocationNumber().toString(), "0");
                lineId = formulateLineId(receivingSummaryLineRequest.getPurchaseOrderId(), receivingSummaryLineRequest.getReceiptNumber(), receivingSummaryLineRequest.getLocationNumber().toString(),
                        "0", receivingSummaryLineRequest.getReceiptLineNumber().toString());
            }

            Query query = new Query();
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.ID.getParameterName()).is(summaryId));
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.STORENUMBER.getParameterName()).is(receivingSummaryLineRequest.getLocationNumber()));
            Update update = new Update();
            update.set(ReceiveSummaryParameters.DATASYNCSTATUS.getParameterName(), DB2SyncStatus.UPDATE_SYNC_INITIATED);
            update.set(ReceiveSummaryParameters.LASTUPDATEDDATE.getParameterName(), LocalDateTime.now());

            update.set(ReceiveSummaryParameters.BUSINESSSTATUSCODE.getParameterName(), receivingSummaryLineRequest.getBusinessStatusCode().charAt(0));
            commitedRcvSummary = mongoTemplate.findAndModify(query, update, ReceiveSummary.class, summaryCollection);
            if (commitedRcvSummary == null) {
                throw new ContentNotFoundException(ReceivingErrors.CONTENTNOTFOUNDSUMMARY.getParameterName(), ReceivingErrors.VALIDID.getParameterName());
            }
            if (Objects.nonNull(commitedRcvSummary) && isWareHouseData) {
                summaryLineList.add(commitedRcvSummary);
            }

            query = new Query();
            query.addCriteria(Criteria.where(ReceivingLineParameters.ID.getParameterName()).is(lineId));
            query.addCriteria(Criteria.where(ReceivingLineParameters.STORENUMBER.getParameterName()).is(receivingSummaryLineRequest.getLocationNumber()));
            Update updateLine = new Update();
            updateLine.set(ReceivingLineParameters.DATASYNCSTATUS.getParameterName(), DB2SyncStatus.UPDATE_SYNC_INITIATED);
            updateLine.set(ReceivingLineParameters.LASTUPDATEDDATE.getParameterName(), LocalDateTime.now());
            updateLine.set(ReceivingLineParameters.INVENTORYMATCHSTATUS.getParameterName(), Integer.parseInt(receivingSummaryLineRequest.getInventoryMatchStatus()));

            commitedRcvLine = mongoTemplate.findAndModify(query, updateLine, ReceivingLine.class, lineCollection);
            if (commitedRcvLine == null) {
                throw new ContentNotFoundException(ReceivingErrors.CONTENTNOTFOUNDLINE.getParameterName(), ReceivingErrors.VALIDID.getParameterName());
            }
            if (Objects.nonNull(commitedRcvLine) && isWareHouseData) {
                summaryLineList.add(commitedRcvLine);
            }

            publisher.publishEvent(summaryLineList);
        }
        responseList.add(receivingSummaryLineRequest);
        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setSuccess(true);
        successMessage.setData(responseList);
        successMessage.setTimestamp(LocalDateTime.now());
        return successMessage;
    }

}
