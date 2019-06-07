package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryReqConverter;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
import com.walmart.finance.ap.fds.receiving.exception.ContentNotFoundException;
import com.walmart.finance.ap.fds.receiving.exception.InvalidValueException;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import com.walmart.finance.ap.fds.receiving.integrations.*;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummaryParameters;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLineParameters;
import com.walmart.finance.ap.fds.receiving.repository.ReceiveSummaryDataRepository;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryLineValidator;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ReceiveSummaryServiceImpl implements ReceiveSummaryService {

    public static final Logger log = LoggerFactory.getLogger(ReceiveSummaryServiceImpl.class);

    @Autowired
    ReceiveSummaryDataRepository receiveDataRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ReceivingSummaryResponseConverter receivingSummaryResponseConverter;

    @Autowired
    ReceivingSummaryReqConverter receivingSummaryReqConverter;

    @Autowired
    InvoiceIntegrationService invoiceIntegrationService;

    @Autowired
    ReceiveSummaryValidator receiveSummaryValidator;

    @Autowired
    ReceiveSummaryLineValidator receiveSummaryLineValidator;

    FreightLineIntegrationService freightLineIntegrationService;


    @Autowired
    private ApplicationEventPublisher publisher;


    // TODO validation for incoming against MDM needs to be added later

    public ReceiveSummary saveReceiveSummary(ReceivingSummaryRequest receivingSummaryRequest) {
        ReceiveSummary receiveSummary = receivingSummaryReqConverter.convert(receivingSummaryRequest);
        return receiveDataRepository.save(receiveSummary);

    }

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

    public List<ReceivingSummaryResponse> getReceiveSummary(String countryCode, String purchaseOrderNumber, String purchaseOrderId, List<String> receiptNumbers, String transactionType, String controlNumber, String locationNumber,
                                                            String divisionNumber, String vendorNumber, String departmentNumber, String invoiceId, String invoiceNumber, String receiptDateStart, String receiptDateEnd, List<String> itemNumbers, List<String> upcNumbers) {// Map<String,String> allRequestParam) {


        // receiveSummaryValidator.validate(allRequestParam);
        HashMap<String, String> paramMap = checkingNotNullParameters(countryCode, purchaseOrderNumber, purchaseOrderId, receiptNumbers, transactionType, controlNumber, locationNumber,
                divisionNumber, vendorNumber, departmentNumber, invoiceId, invoiceNumber, receiptDateStart, receiptDateEnd);
        List<ReceiveSummary> receiveSummaries;
        List<ReceivingSummaryResponse> responseList;

        if (paramMap.containsKey(ReceivingConstants.INVOICENUMBER) || paramMap.containsKey(ReceivingConstants.INVOICEID) || paramMap.containsKey(ReceivingConstants.PURCHASEORDERNUMBER)) {
            receiveSummaries = getInvoiceFromInvoiceSummary(paramMap);
            if (paramMap.containsKey((ReceivingConstants.PURCHASEORDERNUMBER)) && receiveSummaries.isEmpty()) {
                receiveSummaries = getSearchCriteriaForGet(paramMap);
            }
        } else {
            receiveSummaries = getSearchCriteriaForGet(paramMap);
        }
        log.info("Before : size of recesummary list -" + receiveSummaries.size());
        if (CollectionUtils.isNotEmpty(receiveSummaries) && receiveSummaries.size() > 1000) {
            receiveSummaries.subList(1000, receiveSummaries.size()).clear();
        }
        log.info("After : size of recesummary list -" + receiveSummaries.size());
        Map<String, AdditionalResponse> responseMap = getLineResponseMap(receiveSummaries, itemNumbers, upcNumbers);

        //Todo parallel stream performance check
        if (CollectionUtils.isEmpty(receiveSummaries)) {
            throw new NotFoundException("Receiving summary not found for given search criteria.");
        }
        /*else if (receiveSummaries.size() > 1000) {
            throw new SearchCriteriaException("Modify the search criteria as records are more than 1000");
        } */
        else {
            responseList = receiveSummaries.stream().map(
                    (t) -> {
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
            return responseList;
        }
    }

    private String formulateLineId(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime, String sequenceNumber) {
        return receivingControlNumber + ReceivingConstants.PIPE_SEPARATOR + poReceiveId + ReceivingConstants.PIPE_SEPARATOR + storeNumber + ReceivingConstants.PIPE_SEPARATOR + baseDivisionNumber + ReceivingConstants.PIPE_SEPARATOR + transactionType + ReceivingConstants.PIPE_SEPARATOR + finalDate + ReceivingConstants.PIPE_SEPARATOR + finalTime + ReceivingConstants.PIPE_SEPARATOR + sequenceNumber;
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

    private String formulateId(String controlNumber, String receiptNumber, String locationNumber, String divisionNumber, String transactionType, String receiptDateStart, String receiptDateEnd) {
        return controlNumber + ReceivingConstants.PIPE_SEPARATOR + receiptNumber + ReceivingConstants.PIPE_SEPARATOR + locationNumber + ReceivingConstants.PIPE_SEPARATOR + divisionNumber + ReceivingConstants.PIPE_SEPARATOR + transactionType + ReceivingConstants.PIPE_SEPARATOR + receiptDateStart + ReceivingConstants.PIPE_SEPARATOR + receiptDateEnd;

    }
    /*******  Search Criteria methods  *********/

    private List<ReceiveSummary> getSearchCriteriaForGet(HashMap<String, String> paramMap) {
        log.info("Inside getSearchCriteriaForGet method");
        Query query = searchCriteriaForGet(paramMap);
        return executeQueryForReceiveSummary(query);
    }

    private Query searchCriteriaForGet(HashMap<String, String> paramMap) {
        Query dynamicQuery = new Query();

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.CONTROLNUMBER)) || StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.PURCHASEORDERID))) {

            if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.CONTROLNUMBER))) {
                Criteria controlNumberCriteria = Criteria.where(ReceiveSummaryParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(paramMap.get(ReceivingConstants.CONTROLNUMBER));
                dynamicQuery.addCriteria(controlNumberCriteria);
            } else {
                Criteria purchaseOrderIdCriteria = Criteria.where(ReceiveSummaryParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(paramMap.get(ReceivingConstants.PURCHASEORDERID));
                dynamicQuery.addCriteria(purchaseOrderIdCriteria);
            }
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.DIVISIONNUMBER))) {
            Criteria baseDivisionNumberCriteria = Criteria.where(ReceiveSummaryParameters.BASEDIVISIONNUMBER.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.DIVISIONNUMBER)));
            dynamicQuery.addCriteria(baseDivisionNumberCriteria);
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.RECEIPTDATESTART)) && StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.RECEIPTDATEEND))) {
            Criteria mdsReceiveDateCriteria = Criteria.where(ReceiveSummaryParameters.MDSRECEIVEDATE.getParameterName()).gte(getDate(paramMap.get(ReceivingConstants.RECEIPTDATESTART))).lte(getDate(paramMap.get(ReceivingConstants.RECEIPTDATEEND)));
            dynamicQuery.addCriteria(mdsReceiveDateCriteria);
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.TRANSACTIONTYPE))) {
            Criteria transactionTypeCriteria = Criteria.where(ReceiveSummaryParameters.TRANSACTIONTYPE.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.TRANSACTIONTYPE)));
            dynamicQuery.addCriteria(transactionTypeCriteria);
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.LOCATIONNUMBER))) {
            Criteria storeNumberCriteria = Criteria.where(ReceiveSummaryParameters.STORENUMBER.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.LOCATIONNUMBER)));
            dynamicQuery.addCriteria(storeNumberCriteria);
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.PURCHASEORDERNUMBER))) {
            Criteria purchaseOrderNumberCriteria = Criteria.where(ReceiveSummaryParameters.PURCHASEORDERNUMBER.getParameterName()).is(paramMap.get(ReceivingConstants.PURCHASEORDERNUMBER));
            dynamicQuery.addCriteria(purchaseOrderNumberCriteria);
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.RECEIPTNUMBER))) {
            Criteria poReceiveIdCriteria = Criteria.where(ReceiveSummaryParameters.PORECEIVEID.getParameterName()).is(paramMap.get(ReceivingConstants.RECEIPTNUMBER));
            dynamicQuery.addCriteria(poReceiveIdCriteria);
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.DEPARTMENTNUMBER))) {
            Criteria departmentNumberCriteria = Criteria.where(ReceiveSummaryParameters.DEPARTMENTNUMBER.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.DEPARTMENTNUMBER)));
            dynamicQuery.addCriteria(departmentNumberCriteria);
        }

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.VENDORNUMBER))) {
            Criteria vendorNumberCriteria = Criteria.where(ReceiveSummaryParameters.VENDORNUMBER.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.VENDORNUMBER)));
            dynamicQuery.addCriteria(vendorNumberCriteria);
        }

        log.info("query: " + dynamicQuery);
        return dynamicQuery;
    }

    public LocalDate getDate(String date) {
        if (null != date && !"null".equals(date)) {
            DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(date, formatterDate);
        }
        return null;

    }
    /*******  Search Criteria methods  *********/


    /******* Invoice Summary Integration *********/

    private List<ReceiveSummary> getInvoiceFromInvoiceSummary(HashMap<String, String> paramMap) {
        log.info("Inside getInvoiceFromInvoiceSummary method");
        InvoiceResponse[] invoiceResponseList = invoiceIntegrationService.getInvoice(paramMap);
        HashMap<String, ReceiveSummary> receiveSummaryHashMap = new HashMap<>();
        if (invoiceResponseList != null && invoiceResponseList.length > 0) {
            for (InvoiceResponse invoiceResponse : invoiceResponseList) {
                listToMapConversion(callRecvSmryAllAttributes(invoiceResponse), receiveSummaryHashMap);
                listToMapConversion(callRecvSmryByPOId(invoiceResponse), receiveSummaryHashMap);
                listToMapConversion(callRecvSmryByInvoiceNum(invoiceResponse), receiveSummaryHashMap);
            }
        }
        return receiveSummaryHashMap.values().stream().collect(Collectors.toList());

    }

    private List<ReceiveSummary> callRecvSmryByInvoiceNum(InvoiceResponse invoiceResponse) {
        Query query = queryRecvSmryByInvoiceNum(invoiceResponse);
        return executeQueryForReceiveSummary(query);
    }

    private Query queryRecvSmryByInvoiceNum(InvoiceResponse invoiceResponse) {
        Query query = null;
        if (StringUtils.isNotEmpty(invoiceResponse.getInvoiceNumber())) {
            query = new Query();
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(invoiceResponse.getInvoiceNumber().trim()));
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.TRANSACTIONTYPE.getParameterName()).is(1));
        }
        log.info("query: " + query);
        return query;
    }

    private List<ReceiveSummary> callRecvSmryByPOId(InvoiceResponse invoiceResponse) {
        Query query = queryRecvSmryByPOId(invoiceResponse);
        return executeQueryForReceiveSummary(query);
    }

    private Query queryRecvSmryByPOId(InvoiceResponse invoiceResponse) {
        Query query = null;
        if (StringUtils.isNotEmpty(invoiceResponse.getPurchaseOrderNumber())) {
            query = new Query();
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(invoiceResponse.getPurchaseOrderNumber().trim()));
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.TRANSACTIONTYPE.getParameterName()).is(0));
        }
        log.info("query: " + query);
        return query;
    }

    private List<ReceiveSummary> callRecvSmryAllAttributes(InvoiceResponse invoiceResponse) {
        Query query = queryRecvSmryAllAttributes(invoiceResponse);
        return executeQueryForReceiveSummary(query);
    }

    // TODO       addCriteria( "x", invoiceResponse.getReceivingNum(),query);
    private Query queryRecvSmryAllAttributes(InvoiceResponse invoiceResponse) {
        Query query = new Query();
        CriteriaDefinition criteriaDefinition = null;
        if (StringUtils.isNotEmpty(invoiceResponse.getPurchaseOrderNumber())) {
            criteriaDefinition = Criteria.where(ReceiveSummaryParameters.PURCHASEORDERNUMBER.getParameterName()).is(invoiceResponse.getPurchaseOrderNumber().trim());
            query.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(invoiceResponse.getPurchaseOrderId())) {
            criteriaDefinition = Criteria.where(ReceiveSummaryParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(invoiceResponse.getPurchaseOrderId().trim());
            query.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(invoiceResponse.getDestDivNbr())) {
            criteriaDefinition = Criteria.where(ReceiveSummaryParameters.STORENUMBER.getParameterName()).is(Integer.parseInt(invoiceResponse.getDestStoreNbr().trim()));
            query.addCriteria(criteriaDefinition);
        }
        //TODO : According to conversion with Anurag, this has been commented (29/May/2019)
        /*if (StringUtils.isNotEmpty(invoiceResponse.getDestStoreNbr())) {
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.BASEDIVISIONNUMBER.getParameterName()).is(Integer.parseInt(invoiceResponse.getDestDivNbr().trim())));
        }*/
        //TODO  : Commented due to dilemma of 6 digits and 9 digits
        /*   if (StringUtils.isNotEmpty(invoiceResponse.getVendorNumber())) {
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.VENDORNUMBER.getParameterName()).is(Integer.parseInt(invoiceResponse.getVendorNumber().trim())));
        }*/
        if (StringUtils.isNotEmpty(invoiceResponse.getInvoiceDeptNumber())) {
            criteriaDefinition = Criteria.where(ReceiveSummaryParameters.DEPARTMENTNUMBER.getParameterName()).is(Integer.parseInt(invoiceResponse.getInvoiceDeptNumber().trim()));
            query.addCriteria(criteriaDefinition);
        }
        log.info("query: " + query);
        return criteriaDefinition == null ? null : query;

    }

    /******* Invoice Summary Integration *********/


    /******* receive -line data fetching   *********/

    private Map<String, AdditionalResponse> getLineResponseMap(List<ReceiveSummary> receiveSummaries, List<String> itemNumbers, List<String> upcNumbers) {
        Map<String, AdditionalResponse> lineResponseMap = new HashMap<>();

        Iterator<ReceiveSummary> iterator = receiveSummaries.iterator();
        while (iterator.hasNext()) {
            ReceiveSummary receiveSummary = iterator.next();
            AdditionalResponse response = new AdditionalResponse();
            List<ReceivingLine> lineResponseList = queryForLineResponse(receiveSummary, itemNumbers, upcNumbers);
            if (CollectionUtils.isNotEmpty(lineResponseList)) {
                if (receiveSummary.getTypeIndicator().equals("W")) {
                    response.setTotalCostAmount(lineResponseList.stream().mapToDouble((t) -> t.getReceivedQuantity() * t.getCostAmount()).sum());
                    response.setTotalRetailAmount(lineResponseList.stream().mapToDouble((t) -> t.getReceivedQuantity() * t.getRetailAmount()).sum());
                } else {
                    response.setTotalCostAmount(receiveSummary.getTotalCostAmount());
                    response.setTotalRetailAmount(receiveSummary.getTotalRetailAmount());
                }
                response.setLineCount(new Long(lineResponseList.size()));
                getFreightResponse(receiveSummary, response);
                lineResponseMap.put(receiveSummary.get_id(), response);
            } else if (CollectionUtils.isNotEmpty(itemNumbers) || CollectionUtils.isNotEmpty(upcNumbers)) {
                iterator.remove();
            } else {
                getFreightResponse(receiveSummary, response);
                response.setTotalCostAmount(receiveSummary.getTotalCostAmount());
                response.setTotalRetailAmount(receiveSummary.getTotalCostAmount());
                lineResponseMap.put(receiveSummary.get_id(), response);
            }
        }
        return lineResponseMap;
    }


    //TODO When receive-summary id is included in receive-line, modify this query.
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
            criteriaDefinition = Criteria.where(ReceivingLineParameters.ITEMNUMBER.getParameterName()).in(itemNumbers.stream().map(Integer::parseInt).collect(Collectors.toList()));
            query.addCriteria(criteriaDefinition);
        }
        if (CollectionUtils.isNotEmpty(upcNumbers)) {
            criteriaDefinition = Criteria.where(ReceivingLineParameters.UPCNUMBER.getParameterName()).in(upcNumbers);
            query.addCriteria(criteriaDefinition);
        }
        //TODO final date and final time not present in receive-summary thus commented out
//        if (receiveSummary.getFinalDate() != null) {
//            query.addCriteria(Criteria.where(ReceivingLineParameters.FINALDATE.getParameterName()).is(receiveSummary.getFinalDate()));
//        }
//        if (receiveSummary.getFinalTime() != null) {
//            query.addCriteria(Criteria.where(ReceivingLineParameters.FINALTIME.getParameterName()).is(receiveSummary.getFinalTime()));
//        }
        log.info("Query is " + query);
        return executeQueryReceiveline(criteriaDefinition == null ? null : query);

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
            receiveSummaries = mongoTemplate.find(query.limit(1000), ReceiveSummary.class, "receive-summary");
        }
        return receiveSummaries;

    }

    private boolean isWareHouseData(Integer invProcAreaCode, String repInTypCd, String locationCountryCd) {

        if (StringUtils.isNotEmpty(locationCountryCd) && StringUtils.isNotEmpty(repInTypCd)) {
            if ((invProcAreaCode == 36 || invProcAreaCode == 30) && (repInTypCd.equals("R") || repInTypCd.equals("U") || repInTypCd.equals("F")) && (locationCountryCd.equals("US")))
                return true;
        }
        return false;
    }

    @Override
    public ReceivingSummaryRequest updateReceiveSummary(ReceivingSummaryRequest receivingSummaryRequest, Integer vendorNumber, String countryCode) {
        Boolean isWareHouseData = isWareHouseData(receivingSummaryRequest.getMeta().getSorRoutingCtx().getInvProcAreaCode(), receivingSummaryRequest.getMeta().getSorRoutingCtx().getRepInTypCd(),
                receivingSummaryRequest.getMeta().getSorRoutingCtx().getLocationCountryCd());

        if (receivingSummaryRequest != null) {
            String id = formulateId(receivingSummaryRequest.getControlNumber(), receivingSummaryRequest.getReceiptNumbers(), receivingSummaryRequest.getLocationNumber().toString(),
                    receivingSummaryRequest.getDivisionNumber().toString(), receivingSummaryRequest.getTransactionType().toString(), receivingSummaryRequest.getFinalDate().toString(), receivingSummaryRequest.getFinalTime().toString());

            ReceiveSummary receiveSummary = mongoTemplate.findById(id, ReceiveSummary.class, "receive-summary");
            if (receiveSummary != null) {
                receiveSummary.setReceivingControlNumber(receivingSummaryRequest.getControlNumber());
                receiveSummary.setPurchaseOrderNumber(receivingSummaryRequest.getPurchaseOrderNumber().toString());
                receiveSummary.setDepartmentNumber(receivingSummaryRequest.getDepartmentNumber());
                receiveSummary.setTotalCostAmount(receivingSummaryRequest.getCostAmount());
                receiveSummary.setTotalRetailAmount(receivingSummaryRequest.getRetailAmount());
                if (receivingSummaryRequest.getDepartmentNumber() >= 0 && receivingSummaryRequest.getDepartmentNumber() <= 99) {
                    receiveSummary.setDepartmentNumber(receivingSummaryRequest.getDepartmentNumber());
                }
                receiveSummary.setVendorNumber(receivingSummaryRequest.getVendorNumber());
                receiveSummary.setAccountNumber(receivingSummaryRequest.getAccountNumber());
                receiveSummary.setClaimPendingIndicator(receivingSummaryRequest.getClaimPendingIndicator());
                receiveSummary.setControlSequenceNumber(receivingSummaryRequest.getControlSequenceNumber());
                if (receiveSummaryValidator.validateControlType(receivingSummaryRequest) == true) {
                    receiveSummary.setControlType(receivingSummaryRequest.getControlType());
                }
                receiveSummary.setFreeAstrayIndicator(receivingSummaryRequest.getFreeAstrayIndicator());
                receiveSummary.setFinalDate(receivingSummaryRequest.getFinalDate());
                receiveSummary.setFinalTime(receivingSummaryRequest.getFinalTime());
                receiveSummary.setFreightConslIndicator(receivingSummaryRequest.getFreightConslIndicator());
                receiveSummary.setMatchIndicator(receivingSummaryRequest.getMatchIndicator());
                receiveSummary.setReceiveSequenceNumber(receivingSummaryRequest.getReceiveSequenceNumber());
                receiveSummary.setReceiveWeightQuantity(receivingSummaryRequest.getReceiveWeightQuantity());
                receiveSummary.setWriteIndicator(receivingSummaryRequest.getWriteIndicator());
                receiveSummary.setTypeIndicator(receivingSummaryRequest.getTypeIndicator());
                receiveSummary.setUserId(receivingSummaryRequest.getUserId());
                receiveSummary.setBaseDivisionNumber(receivingSummaryRequest.getDivisionNumber());
                receiveSummary.setStoreNumber(receivingSummaryRequest.getLocationNumber());
                receiveSummary.setMDSReceiveDate(receivingSummaryRequest.getReceiptDateStart().toLocalDate());//TODO, do we need to change?
                receiveSummary.setMDSReceiveDate(receivingSummaryRequest.getReceiptDateEnd().toLocalDate());
                receiveSummary.setFreightBillId(receivingSummaryRequest.getFreightBillId());
                receiveSummary.setSequenceNumber(receivingSummaryRequest.getSequenceNumber());
                receiveSummary.setPoReceiveId(receivingSummaryRequest.getReceiptNumbers());

               /* if (receiveSummaryValidator.validateVendorNumberUpdateSummary(receivingSummaryRequest, vendorNumber, countryCode) == true) {
                    receiveSummary.setVendorNumber(receivingSummaryRequest.getVendorNumber());
                } else {
                    throw new InvalidValueException("Value of field vendorNumber passed is not valid");
                }*/
                if (receiveSummaryValidator.validateBusinessStatUpdateSummary(receivingSummaryRequest) == true) {
                    receiveSummary.setBusinessStatusCode(receivingSummaryRequest.getBusinessStatusCode().charAt(0));
                } else {
                    throw new InvalidValueException("Value of field  businessStatusCode passed is not valid");
                }
            } else {
                throw new ContentNotFoundException("The content not found for the given id");
            }
            ReceiveSummary commitedRcvSummary = mongoTemplate.save(receiveSummary, "receive-summary");
            if (Objects.nonNull(commitedRcvSummary) && isWareHouseData) {
                publisher.publishEvent(commitedRcvSummary);
            }

        }
        return receivingSummaryRequest;
    }

    @Override
    public ReceivingSummaryLineRequest updateReceiveSummaryAndLine(ReceivingSummaryLineRequest receivingSummaryLineRequest, String countryCode, Integer vendorNumber) {
        Boolean isWareHouseData = isWareHouseData(receivingSummaryLineRequest.getMeta().getSorRoutingCtx().getInvProcAreaCode(), receivingSummaryLineRequest.getMeta().getSorRoutingCtx().getRepInTypCd(),
                receivingSummaryLineRequest.getMeta().getSorRoutingCtx().getLocationCountryCd());
        Query dynamicQuery = new Query();
        List<ReceivingLine> receiveLines = new ArrayList();

        String id = formulateId(receivingSummaryLineRequest.getControlNumber(), receivingSummaryLineRequest.getReceiptNumber().toString(), receivingSummaryLineRequest.getLocationNumber().toString(),
                receivingSummaryLineRequest.getDivisionNumber().toString(), receivingSummaryLineRequest.getTransactionType().toString(), receivingSummaryLineRequest.getFinalDate().toString(), receivingSummaryLineRequest.getFinalTime().toString());

        // String id = "708542521|30005|1018|0|99|0|0";

        ReceiveSummary receiveSummary = mongoTemplate.findById(id, ReceiveSummary.class, "receiving-summary");

        if (receiveSummary == null) {
            throw new ContentNotFoundException("No content found for the given id");
        }

        receiveSummary.setReceivingControlNumber(receivingSummaryLineRequest.getControlNumber());
        if (receivingSummaryLineRequest.getPurchasedOrderId() != null) {
            receiveSummary.setReceivingControlNumber(receivingSummaryLineRequest.getPurchasedOrderId().toString());
        }
        receiveSummary.setPurchaseOrderNumber(receivingSummaryLineRequest.getPurchaseOrderNumber());
        if (receivingSummaryLineRequest.getDepartmentNumber() >= 0 && receivingSummaryLineRequest.getDepartmentNumber() <= 99) {
            receiveSummary.setDepartmentNumber(receivingSummaryLineRequest.getDepartmentNumber());
        }
        if (Objects.nonNull(receivingSummaryLineRequest) && receivingSummaryLineRequest.getPurchasedOrderId() != null) {
            receiveSummary.setPoReceiveId(receivingSummaryLineRequest.getPurchaseOrderId().toString());
        }
        receiveSummary.setVendorNumber(receivingSummaryLineRequest.getVendorNumber());
        receiveSummary.setAccountNumber(receivingSummaryLineRequest.getAccountNumber());
        receiveSummary.setCasesReceived(receivingSummaryLineRequest.getCasesReceived());
        receiveSummary.setClaimPendingIndicator(receivingSummaryLineRequest.getClaimPendingIndicator());
        receiveSummary.setControlSequenceNumber(receivingSummaryLineRequest.getControlSequenceNumber());
        if (receiveSummaryLineValidator.validateControlType(receivingSummaryLineRequest) == true) {
            receiveSummary.setControlType(receivingSummaryLineRequest.getControlType());
        } else {
            throw new InvalidValueException("Value of field controlType passed is not valid");
        }
        receiveSummary.setFinalDate(receivingSummaryLineRequest.getFinalDate());
        receiveSummary.setFinalizedLoadTimestamp(receivingSummaryLineRequest.getFinalizedLoadTimestamp());
        receiveSummary.setFreeAstrayIndicator(receivingSummaryLineRequest.getFreeAstrayIndicator());
        receiveSummary.setFinalizedSequenceNumber(receivingSummaryLineRequest.getFinalizedSequenceNumber());
        receiveSummary.setFreightBillExpandID(receivingSummaryLineRequest.getFreightBillExpandID());
        receiveSummary.setFreightConslIndicator(receivingSummaryLineRequest.getFreightConslIndicator());
        receiveSummary.setReceiveSequenceNumber(receivingSummaryLineRequest.getReceiveSequenceNumber());
        receiveSummary.setReceiveWeightQuantity(receivingSummaryLineRequest.getReceiveWeightQuantity());
        receiveSummary.setUserId(receiveSummary.getUserId());
        receiveSummary.setBaseDivisionNumber(receivingSummaryLineRequest.getDivisionNumber());
        receiveSummary.setStoreNumber(receivingSummaryLineRequest.getLocationNumber());
        receiveSummary.setMDSReceiveDate(receivingSummaryLineRequest.getReceiptDateStart().toLocalDate());
        receiveSummary.setMDSReceiveDate(receivingSummaryLineRequest.getReceiptDateEnd().toLocalDate());
        receiveSummary.setFreightBillId(receivingSummaryLineRequest.getFreightBillId());
        receiveSummary.setSequenceNumber(receivingSummaryLineRequest.getSequenceNumber());
        receiveSummary.setFinalTime(receivingSummaryLineRequest.getFinalTime());
        if (receiveSummaryLineValidator.validateBusinessStatUpdateSummary(receivingSummaryLineRequest) == true) {
            receiveSummary.setBusinessStatusCode(receivingSummaryLineRequest.getBusinessStatusCode().charAt(0));
        } else {
            throw new InvalidValueException("Value of field  businessStatusCode passed is not valid");
        }
        if (receivingSummaryLineRequest.getReceiptNumber() != null) {
            receiveSummary.setPoReceiveId(receivingSummaryLineRequest.getReceiptNumber().toString());
        }
        mongoTemplate.save(receiveSummary, "receiving-summary");

        ReceiveSummary commitedRcvSummary = mongoTemplate.save(receiveSummary, "receiving-summary");
        if (Objects.nonNull(commitedRcvSummary) && isWareHouseData) {
            publisher.publishEvent(commitedRcvSummary);
        }


        if (StringUtils.isNotEmpty(receivingSummaryLineRequest.getSequenceNumber().toString())) {

            String lineId = formulateLineId(receivingSummaryLineRequest.getControlNumber(), receivingSummaryLineRequest.getReceiptNumber().toString(), receivingSummaryLineRequest.getLocationNumber().toString(),
                    receivingSummaryLineRequest.getDivisionNumber().toString(), receivingSummaryLineRequest.getTransactionType().toString(), receivingSummaryLineRequest.getFinalDate().toString(), receivingSummaryLineRequest.getFinalTime().toString(), receivingSummaryLineRequest.getSequenceNumber().toString());

            ReceivingLine receiveLine = mongoTemplate.findById(lineId, ReceivingLine.class, "receive-line");

            if (receiveLine != null) {
                receiveLine.setBaseDivisionNumber(receivingSummaryLineRequest.getBaseDivisionNumber());
                receiveLine.setCostAmount(receivingSummaryLineRequest.getCostAmount());
                receiveLine.setMDSReceiveDate(receivingSummaryLineRequest.getReceiptDateStart().toLocalDate());
                receiveLine.setMDSReceiveDate(receivingSummaryLineRequest.getReceiptDateEnd().toLocalDate());
                receiveLine.setFinalDate(receivingSummaryLineRequest.getFinalDate());
                receiveLine.setCostAmount(receivingSummaryLineRequest.getCostAmount());
                receiveLine.setFinalDate(receivingSummaryLineRequest.getFinalDate());
                receiveLine.setItemNumber(receivingSummaryLineRequest.getItemNumber());
                receiveLine.setLineNumber(receivingSummaryLineRequest.getLineNumber());
                receiveLine.setPurchasedOrderId(receivingSummaryLineRequest.getPurchasedOrderId());
                receiveLine.setPurchaseOrderNumber(receivingSummaryLineRequest.getPurchaseOrderNumber());
                receiveLine.setPurchaseReceiptNumber(receivingSummaryLineRequest.getPurchaseReceiptNumber());
                receiveLine.setPurchaseOrderReceiveID(receivingSummaryLineRequest.getReceiptNumber().toString());
                receiveLine.setReceivedQuantityUnitOfMeasureCode(receivingSummaryLineRequest.getReceivedQuantityUnitOfMeasureCode());
                receiveLine.setReceiveSequenceNumber(receivingSummaryLineRequest.getReceiveSequenceNumber());
                receiveLine.setReceivedQuantity(receivingSummaryLineRequest.getReceivedQuantity());
                receiveLine.setReceivedWeightQuantity(receivingSummaryLineRequest.getReceivedWeightQuantity());
                receiveLine.setReceivingControlNumber(receivingSummaryLineRequest.getControlNumber());
                receiveLine.setReceivingControlNumber(receivingSummaryLineRequest.getPurchasedOrderId().toString());
                receiveLine.setRetailAmount(receivingSummaryLineRequest.getRetailAmount());
                receiveLine.setSequenceNumber(receivingSummaryLineRequest.getSequenceNumber());
                receiveLine.setStoreNumber(receivingSummaryLineRequest.getLocationNumber());
                receiveLine.setTransactionType(receivingSummaryLineRequest.getTransactionType());
                if (receivingSummaryLineRequest.getUpcNumber() != null) {
                    receiveLine.setUpcNumber(receivingSummaryLineRequest.getUpcNumber().toString());
                }
                receiveLine.setInventoryMatchStatus(receivingSummaryLineRequest.getInventoryMatchStatus());
            /*    if (receiveSummaryLineValidator.validateVendorNumberUpdateSummary(receivingSummaryLineRequest, vendorNumber, countryCode) == true) {
                    receiveLine.setVendorNumber(receivingSummaryLineRequest.getVendorNumber());
                } else {
                    throw new InvalidValueException("Value of field vendorNumber passed is not valid");
                }*/
                mongoTemplate.save(receiveLine, "receive-line");

                ReceivingLine commitedRcvLine = mongoTemplate.save(receiveLine, "receive-line");
                if (Objects.nonNull(commitedRcvLine) && isWareHouseData) {
                    publisher.publishEvent(commitedRcvLine);
                }

            }

        } else {

            // TODO, ideally we should have receiveSummary key reference in Receive Line

            if ((receivingSummaryLineRequest.getPurchasedOrderId() != null) || (receivingSummaryLineRequest.getControlNumber() != null)) {
                if (receivingSummaryLineRequest.getPurchasedOrderId() != null) {
                    Criteria purchaseOrderIdCriteria = Criteria.where("receivingControlNumber").is(receivingSummaryLineRequest.getPurchasedOrderId().toString());//TODO,purchasedOrderId, needed in COSMOS
                    dynamicQuery.addCriteria(purchaseOrderIdCriteria);
                } else {
                    Criteria controlNumberCriteria = Criteria.where("receivingControlNumber").is(receivingSummaryLineRequest.getControlNumber());
                    dynamicQuery.addCriteria(controlNumberCriteria);
                }
            }
            if (receivingSummaryLineRequest.getReceiptNumber() != null) {
                Criteria receiptNumberCriteria = Criteria.where("purchaseOrderReceiveID").is(receivingSummaryLineRequest.getReceiptNumber());
                dynamicQuery.addCriteria(receiptNumberCriteria);
            }
            if (receivingSummaryLineRequest.getTransactionType() != null) {
                Criteria transactionTypeCriteria = Criteria.where("transactionType").is(receivingSummaryLineRequest.getTransactionType());
                dynamicQuery.addCriteria(transactionTypeCriteria);
            }

            if (receivingSummaryLineRequest.getBaseDivisionNumber() != null) {
                Criteria divisionNumberCriteria = Criteria.where("baseDivisionNumber").is(receivingSummaryLineRequest.getBaseDivisionNumber());
                dynamicQuery.addCriteria(divisionNumberCriteria);
            }
            if (receivingSummaryLineRequest.getStoreNumber() != null) {
                Criteria locationNumberCriteria = Criteria.where("storeNumber").is(Integer.valueOf(receivingSummaryLineRequest.getStoreNumber()));
                dynamicQuery.addCriteria(locationNumberCriteria);
            }

        }
        List<ReceivingLine> receivingLineList = mongoTemplate.find(dynamicQuery, ReceivingLine.class, "receive-line");
        for (ReceivingLine receivingLine : receivingLineList) {
            receivingLine.setBaseDivisionNumber(receivingSummaryLineRequest.getBaseDivisionNumber());
            receivingLine.setCostAmount(receivingSummaryLineRequest.getCostAmount());
            receivingLine.setMDSReceiveDate(receivingSummaryLineRequest.getReceiptDateStart().toLocalDate());
            receivingLine.setMDSReceiveDate(receivingSummaryLineRequest.getReceiptDateEnd().toLocalDate());
            receivingLine.setFinalDate(receivingSummaryLineRequest.getFinalDate());
            receivingLine.setCostAmount(receivingSummaryLineRequest.getCostAmount());
            receivingLine.setFinalDate(receivingSummaryLineRequest.getFinalDate());
            receivingLine.setItemNumber(receivingSummaryLineRequest.getItemNumber());
            receivingLine.setLineNumber(receivingSummaryLineRequest.getLineNumber());
            receivingLine.setPurchasedOrderId(receivingSummaryLineRequest.getPurchasedOrderId());
            receivingLine.setPurchaseOrderNumber(receivingSummaryLineRequest.getPurchaseOrderNumber());
            receivingLine.setPurchaseReceiptNumber(receivingSummaryLineRequest.getPurchaseReceiptNumber());
            receivingLine.setQuantity(receivingSummaryLineRequest.getReceivedQuantity());
            receivingLine.setPurchaseOrderReceiveID(receivingSummaryLineRequest.getPurchaseOrderReceiveID());
            receivingLine.setReceivedQuantityUnitOfMeasureCode(receivingSummaryLineRequest.getReceivedQuantityUnitOfMeasureCode());
            receivingLine.setReceiveSequenceNumber(receivingSummaryLineRequest.getReceiveSequenceNumber());
            receivingLine.setReceivedQuantity(receivingSummaryLineRequest.getReceivedQuantity());
            receivingLine.setReceivedWeightQuantity(receivingSummaryLineRequest.getReceivedWeightQuantity());
            receivingLine.setReceivingControlNumber(receivingSummaryLineRequest.getReceivingControlNumber());
            receivingLine.setRetailAmount(receivingSummaryLineRequest.getRetailAmount());
            receivingLine.setStoreNumber(receivingSummaryLineRequest.getLocationNumber());
            receivingLine.setTransactionType(receivingSummaryLineRequest.getTransactionType());
            if (receivingSummaryLineRequest.getUpcNumber() != null) {
                receivingLine.setUpcNumber(receivingSummaryLineRequest.getUpcNumber().toString());
            }
            receivingLine.setInventoryMatchStatus(receivingSummaryLineRequest.getInventoryMatchStatus());
       /*     if (receiveSummaryLineValidator.validateVendorNumberUpdateSummary(receivingSummaryLineRequest, vendorNumber, countryCode)) {
                receivingLine.setVendorNumber(receivingSummaryLineRequest.getVendorNumber());
            } else {
                throw new InvalidValueException("Value of field vendorNumber passed is not valid");
            }*/
            receiveLines.add(receivingLine);
        }
        List<ReceivingLine> commitedRcvLineList = mongoTemplate.save(receiveLines, "receive-line");
        if (Objects.nonNull(commitedRcvLineList) && isWareHouseData) {
            publisher.publishEvent(commitedRcvLineList);
        }
        return receivingSummaryLineRequest;
    }

    private List<ReceivingLine> executeQueryReceiveline(Query query) {
        List<ReceivingLine> receiveLines = new ArrayList<>();
        if (query != null) {
            receiveLines = mongoTemplate.find(query, ReceivingLine.class, "receive-line-new");
        }
        return receiveLines;
    }

    private List<FreightResponse> executeQueryReceiveFreight(Query query) {
        List<FreightResponse> receiveFreights = mongoTemplate.find(query, FreightResponse.class, "receive-freight");
        return receiveFreights;
    }

    private void listToMapConversion(List<ReceiveSummary> receiveSummaries, HashMap<String, ReceiveSummary> receiveSummaryHashMap) {
        receiveSummaries.stream().forEach((t) -> receiveSummaryHashMap.put(t.get_id(), t));
    }


}


