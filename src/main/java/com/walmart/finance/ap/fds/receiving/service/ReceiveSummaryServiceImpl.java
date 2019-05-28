package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryReqConverter;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import com.walmart.finance.ap.fds.receiving.exception.SearchCriteriaException;
import com.walmart.finance.ap.fds.receiving.integrations.*;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummaryParameters;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLineParameters;
import com.walmart.finance.ap.fds.receiving.repository.ReceiveSummaryDataRepository;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    FreightLineIntegrationService freightLineIntegrationService;


    @Autowired
    ReceiveSummaryValidator receiveSummaryValidator;


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
    @Override
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

        //TODO change exception messages
        //Todo parallel stream
        if (receiveSummaries.isEmpty()) {
            throw new NotFoundException("Content not found for given search criteria.");
        } else if (receiveSummaries.size() > 1000) {
            throw new SearchCriteriaException("Modify the search criteria as records are more than 1000");
        } else {
            Map<String, FreightResponse> freightResponseMap = freightLineIntegrationService.getFreightLineAPIData(receiveSummaries);
            Map<String, ReceiveLineResponse> lineResponseMap = getLineResponseMap(receiveSummaries);
            if (freightResponseMap.isEmpty() && lineResponseMap.isEmpty()) {
                responseList = receiveSummaries.stream().map((t) -> receivingSummaryResponseConverter.convert(t)).collect(Collectors.toList());
            } else if (freightResponseMap.isEmpty() && !lineResponseMap.isEmpty()) {
                responseList = receiveSummaries.stream().map(
                        (t) -> {
                            ReceivingSummaryResponse response = receivingSummaryResponseConverter.convert(t);
                            response.setLineCount(lineResponseMap.get(t.get_id()) == null ? null : lineResponseMap.get(t.get_id()).getLineCount());
                            response.setTotalCostAmount(lineResponseMap.get(t.get_id()) == null ? null : lineResponseMap.get(t.get_id()).getTotalCostAmount());
                            response.setTotalRetailAmount(lineResponseMap.get(t.get_id()) == null ? null : lineResponseMap.get(t.get_id()).getTotalRetailAmount());
                            return response;
                        }
                ).collect(Collectors.toList());
            } else {
                responseList = receiveSummaries.stream().map(
                        (t) -> {
                            ReceivingSummaryResponse response = receivingSummaryResponseConverter.convert(t);
                            response.setCarrierCode(freightResponseMap.get(t.get_id()) == null ? null : freightResponseMap.get(t.get_id()).getCarrierCode());
                            response.setTrailerNumber(freightResponseMap.get(t.get_id()) == null ? null : Integer.parseInt(freightResponseMap.get(t.get_id()).getTrailerNumber()));
                            response.setLineCount(lineResponseMap.get(t.get_id()) == null ? null : lineResponseMap.get(t.get_id()).getLineCount());
                            response.setTotalCostAmount(lineResponseMap.get(t.get_id()) == null ? null : lineResponseMap.get(t.get_id()).getTotalCostAmount());
                            response.setTotalRetailAmount(lineResponseMap.get(t.get_id()) == null ? null : lineResponseMap.get(t.get_id()).getTotalRetailAmount());
                            return response;
                        }
                ).collect(Collectors.toList());
            }
            return responseList;
        }
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
        Query query = new Query();
        if (StringUtils.isNotEmpty(invoiceResponse.getInvoiceNumber())) {
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
        Query query = new Query();
        if (StringUtils.isNotEmpty(invoiceResponse.getPurchaseOrderNumber())) {
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
        if (StringUtils.isNotEmpty(invoiceResponse.getPurchaseOrderNumber())) {
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.PURCHASEORDERNUMBER.getParameterName()).is(invoiceResponse.getPurchaseOrderNumber().trim()));
        }
        if (StringUtils.isNotEmpty(invoiceResponse.getPurchaseOrderId())) {
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(invoiceResponse.getPurchaseOrderId().trim()));
        }
        if (StringUtils.isNotEmpty(invoiceResponse.getDestDivNbr())) {
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.STORENUMBER.getParameterName()).is(Integer.parseInt(invoiceResponse.getDestStoreNbr().trim())));
        }
        if (StringUtils.isNotEmpty(invoiceResponse.getDestStoreNbr())) {
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.BASEDIVISIONNUMBER.getParameterName()).is(Integer.parseInt(invoiceResponse.getDestDivNbr().trim())));
        }
/*TODO  : Commented due to dilemma of 6 digits and 9 digits
           if (StringUtils.isNotEmpty(invoiceResponse.getVendorNumber())) {
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.VENDORNUMBER.getParameterName()).is(Integer.parseInt(invoiceResponse.getVendorNumber().trim())));
        }*/
        if (StringUtils.isNotEmpty(invoiceResponse.getInvoiceDeptNumber())) {
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.DEPARTMENTNUMBER.getParameterName()).is(Integer.parseInt(invoiceResponse.getInvoiceDeptNumber().trim())));
        }
        log.info("query: " + query);
        return query;

    }

    /******* Invoice Summary Integration *********/


    /******* receive -line data fetching   *********/

    private Map<String, ReceiveLineResponse> getLineResponseMap(List<ReceiveSummary> receiveSummaries) {
        Map<String, ReceiveLineResponse> lineResponseMap = new HashMap<>();
        for (ReceiveSummary receiveSummary : receiveSummaries) {
            List<ReceivingLine> lineResponseList = queryForLineResponse(receiveSummary);
            if (CollectionUtils.isNotEmpty(lineResponseList)) {
                ReceiveLineResponse response = new ReceiveLineResponse();
                response.setTotalCostAmount(lineResponseList.stream().mapToDouble((t) -> t.getReceivedQuantity() * t.getCostAmount()).sum());
                response.setTotalRetailAmount(lineResponseList.stream().mapToDouble((t) -> t.getReceivedQuantity() * t.getRetailAmount()).sum());
                response.setLineCount(new Long(lineResponseList.size()));
                lineResponseMap.put(receiveSummary.get_id(), response);
            }
        }
        return lineResponseMap;
    }


    //TODO When receive-summary id is included in receive-line, modify this query.
    private List<ReceivingLine> queryForLineResponse(ReceiveSummary receiveSummary) {

        Query query = new Query();
        if (StringUtils.isNotEmpty(receiveSummary.getReceivingControlNumber())) {
            query.addCriteria(Criteria.where(ReceivingLineParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(receiveSummary.getReceivingControlNumber().trim()));
        }
        if (StringUtils.isNotEmpty(receiveSummary.getPoReceiveId())) {
            query.addCriteria(Criteria.where(ReceivingLineParameters.PORECEIVEID.getParameterName()).is(receiveSummary.getPoReceiveId().trim()));
        }
        if (receiveSummary.getStoreNumber() != null) {
            query.addCriteria(Criteria.where(ReceivingLineParameters.STORENUMBER.getParameterName()).is(receiveSummary.getStoreNumber()));
        }
        if (receiveSummary.getBaseDivisionNumber() != null) {
            query.addCriteria(Criteria.where(ReceivingLineParameters.BASEDIVISIONNUMBER.getParameterName()).is(receiveSummary.getBaseDivisionNumber()));
        }
        if (receiveSummary.getTransactionType() != null) {
            query.addCriteria(Criteria.where(ReceivingLineParameters.TRANSACTIONTYPE.getParameterName()).is(receiveSummary.getTransactionType()));
        }
        //TODO final date and final time not present in receive-summary so
//        if (receiveSummary.getFinalDate() != null) {
//            query.addCriteria(Criteria.where(ReceivingLineParameters.FINALDATE.getParameterName()).is(receiveSummary.getFinalDate()));
//        }
//        if (receiveSummary.getFinalTime() != null) {
//            query.addCriteria(Criteria.where(ReceivingLineParameters.FINALTIME.getParameterName()).is(receiveSummary.getFinalTime()));
//        }
        log.info("Query is " + query);
        return executeQueryReceiveline(query);

    }
    /******* receive -line data fetching   *********/


    /******* Common Methods  *********/

    private List<ReceiveSummary> executeQueryForReceiveSummary(Query query) {
        List<ReceiveSummary> receiveSummaries = mongoTemplate.find(query, ReceiveSummary.class, "receive-summary");
        return receiveSummaries;
    }

    //TODO Count query in line
    private List<ReceivingLine> executeQueryReceiveline(Query query) {
        List<ReceivingLine> receiveSummaries = mongoTemplate.find(query, ReceivingLine.class, "receive-line-new");
        return receiveSummaries;
    }

    private void listToMapConversion(List<ReceiveSummary> receiveSummaries, HashMap<String, ReceiveSummary> receiveSummaryHashMap) {
        receiveSummaries.stream().forEach((t) -> receiveSummaryHashMap.put(t.get_id(), t));
    }
    /******* Common Methods  *********/


}


