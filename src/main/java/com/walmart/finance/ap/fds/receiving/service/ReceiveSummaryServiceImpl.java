package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryReqConverter;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
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
import org.springframework.transaction.annotation.Transactional;

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

    private String formulateLineId(String receivingControlNumber, String poReceiveId, String storeNumber, String receiptDate, String sequenceNumber) {
        return receivingControlNumber + ReceivingConstants.PIPE_SEPARATOR + poReceiveId + ReceivingConstants.PIPE_SEPARATOR + storeNumber + ReceivingConstants.PIPE_SEPARATOR + receiptDate + ReceivingConstants.PIPE_SEPARATOR + sequenceNumber;
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

    private String formulateId(String controlNumber, String receiptNumber, String locationNumber, String receiptDate) {
        return controlNumber + ReceivingConstants.PIPE_SEPARATOR + receiptNumber + ReceivingConstants.PIPE_SEPARATOR + locationNumber + ReceivingConstants.PIPE_SEPARATOR + receiptDate;

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
    @Transactional
    public ReceivingSummaryRequest updateReceiveSummary(ReceivingSummaryRequest receivingSummaryRequest, String countryCode) {
        Boolean isWareHouseData = isWareHouseData(receivingSummaryRequest.getMeta().getSorRoutingCtx().getInvProcAreaCode(), receivingSummaryRequest.getMeta().getSorRoutingCtx().getRepInTypCd(),
                receivingSummaryRequest.getMeta().getSorRoutingCtx().getLocationCountryCd());

        if (receivingSummaryRequest != null) {
            String id = formulateId(receivingSummaryRequest.getControlNumber(), receivingSummaryRequest.getReceiptNumber(), receivingSummaryRequest.getLocationNumber().toString(), receivingSummaryRequest.getReceiptDate().toString());

            ReceiveSummary receiveSummary = mongoTemplate.findById(id, ReceiveSummary.class, "receive-summary");
            if (receiveSummary != null) {
                if (receiveSummaryValidator.validateBusinessStatUpdateSummary(receivingSummaryRequest) == true) {
                    receiveSummary.setBusinessStatusCode(receivingSummaryRequest.getBusinessStatusCode().charAt(0));
                } else {
                    throw new InvalidValueException("Value of field  businessStatusCode passed is not valid");
                }
            } else {
                throw new NotFoundException("Receive summary not found for the given id");
            }
            ReceiveSummary commitedRcvSummary = mongoTemplate.save(receiveSummary, "receive-summary");
            if (Objects.nonNull(commitedRcvSummary) && isWareHouseData) {
                publisher.publishEvent(commitedRcvSummary);
            }

        }
        return receivingSummaryRequest;
    }

    @Override
    @Transactional
    public ReceivingSummaryLineRequest updateReceiveSummaryAndLine(ReceivingSummaryLineRequest receivingSummaryLineRequest, String countryCode) {
        Boolean isWareHouseData = isWareHouseData(receivingSummaryLineRequest.getMeta().getSorRoutingCtx().getInvProcAreaCode(), receivingSummaryLineRequest.getMeta().getSorRoutingCtx().getRepInTypCd(),
                receivingSummaryLineRequest.getMeta().getSorRoutingCtx().getLocationCountryCd());
        Query dynamicQuery = new Query();
        List<ReceivingLine> receiveLines = new ArrayList();
        ReceivingLine commitedRcvLine = null;
        if (receivingSummaryLineRequest.getSequenceNumber() == null) {
            String id = formulateId(receivingSummaryLineRequest.getControlNumber(), receivingSummaryLineRequest.getReceiptNumber(), receivingSummaryLineRequest.getLocationNumber().toString(), receivingSummaryLineRequest.getReceiptDate().toString());

            ReceiveSummary receiveSummary = mongoTemplate.findById(id, ReceiveSummary.class, "receive-summary");

            if (receiveSummary == null) {
                throw new NotFoundException("Receive summary not found for the given id");
            }
            //TODO need to check the datatype for BusinessStatusCode

            if (receiveSummaryLineValidator.validateBusinessStatUpdateSummary(receivingSummaryLineRequest) == true) {
                receiveSummary.setBusinessStatusCode(receivingSummaryLineRequest.getBusinessStatusCode().charAt(0));
            } else {
                throw new InvalidValueException("Value of field  businessStatusCode passed is not valid");
            }
            ReceiveSummary commitedRcvSummary = mongoTemplate.save(receiveSummary, "receive-summary");

            if (Objects.nonNull(commitedRcvSummary) && isWareHouseData) {
                publisher.publishEvent(commitedRcvSummary);
            }

            // TODO, ideally we should have receiveSummary key reference in Receive Line

            if (receivingSummaryLineRequest.getControlNumber() != null) {
                Criteria purchaseOrderIdCriteria = Criteria.where("receivingControlNumber").is(receivingSummaryLineRequest.getControlNumber());//TODO,purchasedOrderId, needed in COSMOS
                dynamicQuery.addCriteria(purchaseOrderIdCriteria);
            }
            if (receivingSummaryLineRequest.getReceiptNumber() != null) {
                Criteria receiptNumberCriteria = Criteria.where("purchaseOrderReceiveID").is(receivingSummaryLineRequest.getReceiptNumber());
                dynamicQuery.addCriteria(receiptNumberCriteria);
            }
            if (receivingSummaryLineRequest.getLocationNumber() != null) {
                Criteria locationNumberCriteria = Criteria.where("storeNumber").is(receivingSummaryLineRequest.getLocationNumber());
                dynamicQuery.addCriteria(locationNumberCriteria);
            }
            if (receivingSummaryLineRequest.getReceiptDate() != null) {
                Criteria receiptDateCriteria = Criteria.where("MDSReceiveDate").is(receivingSummaryLineRequest.getReceiptDate());
                dynamicQuery.addCriteria(receiptDateCriteria);
            }

            //TODO code needs to optimized remove the DB calls in loop
            List<ReceivingLine> receivingLineList = mongoTemplate.find(dynamicQuery, ReceivingLine.class, "receive-line");
            for (ReceivingLine receivingLine : receivingLineList) {
                if (receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest) == true) {
                    receivingLine.setInventoryMatchStatus(receivingSummaryLineRequest.getInventoryMatchStatus());
                } else {
                    throw new InvalidValueException("Value of InventoryMatchStatus should be between 0-9");
                }
                commitedRcvLine = mongoTemplate.save(receivingLine, "receive-line");
                if (Objects.nonNull(commitedRcvLine) && isWareHouseData) {
                    publisher.publishEvent(commitedRcvLine);
                }
            }


        } else {

            String lineId = formulateLineId(receivingSummaryLineRequest.getControlNumber(), receivingSummaryLineRequest.getReceiptNumber(), receivingSummaryLineRequest.getLocationNumber().toString(),
                    receivingSummaryLineRequest.getReceiptDate().toString(), receivingSummaryLineRequest.getSequenceNumber().toString());

            ReceivingLine receiveLine = mongoTemplate.findById(lineId, ReceivingLine.class, "receive-line");

            if (receiveLine == null) {
                throw new NotFoundException("Receive line not found for the given id ");
            }
            if (receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest) == true) {
                receiveLine.setInventoryMatchStatus(receivingSummaryLineRequest.getInventoryMatchStatus());
            } else {
                throw new InvalidValueException("Value of InventoryMatchStatus should be between 0-9");
            }
            commitedRcvLine = mongoTemplate.save(receiveLine, "receive-line");
            if (Objects.nonNull(commitedRcvLine) && isWareHouseData) {
                publisher.publishEvent(commitedRcvLine);
            }

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


