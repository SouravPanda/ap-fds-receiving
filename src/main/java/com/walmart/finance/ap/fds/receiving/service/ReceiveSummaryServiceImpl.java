package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryReqConverter;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import com.walmart.finance.ap.fds.receiving.integrations.InvoiceIntegrationService;
import com.walmart.finance.ap.fds.receiving.integrations.InvoiceResponse;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummaryParameters;
import com.walmart.finance.ap.fds.receiving.repository.ReceiveSummaryDataRepository;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
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
     * @param pageNbr
     * @param pageSize
     * @param orderBy
     * @param order
     * @return
     */
    @Override
    public Page<ReceivingSummaryResponse> getReceiveSummary(String countryCode, String purchaseOrderNumber, String purchaseOrderId, String receiptNumbers, String transactionType, String controlNumber, String locationNumber,
                                                            String divisionNumber, String vendorNumber, String departmentNumber, String invoiceId, String invoiceNumber, String receiptDateStart, String receiptDateEnd, int pageNbr, int pageSize, String orderBy, Sort.Direction order) {// Map<String,String> allRequestParam) {


        // receiveSummaryValidator.validate(allRequestParam);
        HashMap<String, String> paramMap = checkingNotNullParameters(countryCode, purchaseOrderNumber, purchaseOrderId, receiptNumbers, transactionType, controlNumber, locationNumber,
                divisionNumber, vendorNumber, departmentNumber, invoiceId, invoiceNumber, receiptDateStart, receiptDateEnd);
        Page<ReceiveSummary> receiveSummaryPage;
        List<ReceiveSummary> receiveSummaries;
        Pageable pageable = PageRequest.of(pageNbr, pageSize);

        if (paramMap.containsKey(ReceivingConstants.INVOICENUMBER) || paramMap.containsKey(ReceivingConstants.INVOICEID) || paramMap.containsKey(ReceivingConstants.PURCHASEORDERNUMBER)) {
            receiveSummaries = getInvoiceFromInvoiceSummary(paramMap);
            //TODO Pagination for Invoice
            receiveSummaryPage = PageableExecutionUtils.getPage(receiveSummaries, pageable, () -> Long.parseLong(ReceivingConstants.COSMOSRECORDCOUNT));

        } else {
            Query query = searchCriteriaForGet(purchaseOrderNumber, purchaseOrderId, receiptNumbers, transactionType, controlNumber, locationNumber,
                    divisionNumber, vendorNumber, departmentNumber, invoiceId, invoiceNumber, receiptDateStart, receiptDateEnd);
            query.with(pageable);
            List<String> orderByproperties = new ArrayList<>();
            orderByproperties.add(orderBy);
            Sort sort = new Sort(order, orderByproperties);
            receiveSummaries = mongoTemplate.find(query, ReceiveSummary.class, "receive-summary");

            receiveSummaryPage = PageableExecutionUtils.getPage(
                    receiveSummaries,
                    pageable,
                    () -> mongoTemplate.count(query, ReceiveSummary.class));
        }
        if (receiveSummaries.isEmpty()) {
            throw new NotFoundException("Content not found.");
        }
        return mapReceivingSummaryToResponse(receiveSummaryPage);

    }

    private Page<ReceivingSummaryResponse> mapReceivingSummaryToResponse(Page<ReceiveSummary> receiveSummaryPage) {
        Page<ReceivingSummaryResponse> receivingSummaryResponsePage = receiveSummaryPage.map(new Function<ReceiveSummary, ReceivingSummaryResponse>() {
            @Override
            public ReceivingSummaryResponse apply(ReceiveSummary receiveSummary) {
                return receivingSummaryResponseConverter.convert(receiveSummary);
            }
        });
        return receivingSummaryResponsePage;
    }


    private String formulateId(String controlNumber, String receiptNumber, String locationNumber, String divisionNumber, String transactionType, String receiptDateStart, String receiptDateEnd) {
        return controlNumber + ReceivingConstants.PIPE_SEPARATOR + receiptNumber + ReceivingConstants.PIPE_SEPARATOR + locationNumber + ReceivingConstants.PIPE_SEPARATOR + divisionNumber + ReceivingConstants.PIPE_SEPARATOR + transactionType + ReceivingConstants.PIPE_SEPARATOR + receiptDateStart + ReceivingConstants.PIPE_SEPARATOR + receiptDateEnd;

    }

    private Query searchCriteriaFromInvoiceResponse(List<InvoiceResponse> invoiceResponses, Query dynamicQuery) {

        if (CollectionUtils.isNotEmpty(invoiceResponses) && invoiceResponses.size() > 1) {

        }

        return dynamicQuery;
    }


    private Query searchCriteriaForGet(String purchaseOrderNumber, String purchaseOrderId, String receiptNumbers, String transactionType, String controlNumber, String locationNumber,
                                       String divisionNumber, String vendorNumber, String departmentNumber, String invoiceId, String invoiceNumber, String receiptDateStart, String receiptDateEnd) {
        Query dynamicQuery = new Query();

        if (StringUtils.isNotEmpty(controlNumber) || StringUtils.isNotEmpty(purchaseOrderId)) {

            if (StringUtils.isNotEmpty(controlNumber)) {
                Criteria controlNumberCriteria = Criteria.where(ReceiveSummaryParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(controlNumber);
                dynamicQuery.addCriteria(controlNumberCriteria);
            } else {
                Criteria purchaseOrderIdCriteria = Criteria.where(ReceiveSummaryParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(purchaseOrderId);
                dynamicQuery.addCriteria(purchaseOrderIdCriteria);
            }
        }

        if (StringUtils.isNotEmpty(divisionNumber)) {
            Criteria baseDivisionNumberCriteria = Criteria.where(ReceiveSummaryParameters.BASEDIVISIONNUMBER.getParameterName()).is(Integer.valueOf(divisionNumber));
            dynamicQuery.addCriteria(baseDivisionNumberCriteria);
        }

        if (StringUtils.isNotEmpty(receiptDateStart) && StringUtils.isNotEmpty(receiptDateEnd)) {
            Criteria mdsReceiveDateCriteria = Criteria.where(ReceiveSummaryParameters.MDSRECEIVEDATE.getParameterName()).gte(getDate(receiptDateStart)).lte(getDate(receiptDateEnd));
            dynamicQuery.addCriteria(mdsReceiveDateCriteria);
        }

        if (StringUtils.isNotEmpty(transactionType)) {
            Criteria transactionTypeCriteria = Criteria.where(ReceiveSummaryParameters.TRANSACTIONTYPE.getParameterName()).is(Integer.valueOf(transactionType));
            dynamicQuery.addCriteria(transactionTypeCriteria);
        }

        if (StringUtils.isNotEmpty(locationNumber)) {
            Criteria storeNumberCriteria = Criteria.where(ReceiveSummaryParameters.STORENUMBER.getParameterName()).is(Integer.valueOf(locationNumber));
            dynamicQuery.addCriteria(storeNumberCriteria);
        }

        if (StringUtils.isNotEmpty(purchaseOrderNumber)) {
            Criteria purchaseOrderNumberCriteria = Criteria.where(ReceiveSummaryParameters.PURCHASEORDERNUMBER.getParameterName()).is(purchaseOrderNumber);
            dynamicQuery.addCriteria(purchaseOrderNumberCriteria);
        }

        if (StringUtils.isNotEmpty(receiptNumbers)) {
            Criteria poReceiveIdCriteria = Criteria.where(ReceiveSummaryParameters.PORECEIVEID.getParameterName()).is(receiptNumbers);
            dynamicQuery.addCriteria(poReceiveIdCriteria);
        }

        if (StringUtils.isNotEmpty(departmentNumber)) {
            Criteria departmentNumberCriteria = Criteria.where(ReceiveSummaryParameters.DEPARTMENTNUMBER.getParameterName()).is(Integer.valueOf(departmentNumber));
            dynamicQuery.addCriteria(departmentNumberCriteria);
        }

        if (StringUtils.isNotEmpty(vendorNumber)) {
            Criteria vendorNumberCriteria = Criteria.where(ReceiveSummaryParameters.VENDORNUMBER.getParameterName()).is(Integer.valueOf(vendorNumber));
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


    private HashMap<String, String> checkingNotNullParameters(String countryCode, String purchaseOrderNumber, String purchaseOrderId, String receiptNumber, String transactionType, String controlNumber, String locationNumber, String divisionNumber,
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
        if (StringUtils.isNotEmpty(receiptNumber)) {
            paramMap.put(ReceivingConstants.RECEIPTNUMBER, receiptNumber);
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

    private List<ReceiveSummary> getInvoiceFromInvoiceSummary(HashMap<String, String> paramMap) {
        InvoiceResponse[] invoiceResponseList = invoiceIntegrationService.getInvoice(paramMap);
        HashMap<String, ReceiveSummary> receiveSummaryHashMap = new HashMap<>();
        for (InvoiceResponse invoiceResponse : invoiceResponseList) {
            listToMapConversion(callRecvSmryAllAttributes(invoiceResponse), receiveSummaryHashMap);
            listToMapConversion(callRecvSmryByPOId(invoiceResponse), receiveSummaryHashMap);
            listToMapConversion(callRecvSmryByInvoiceNum(invoiceResponse), receiveSummaryHashMap);
        }
        return receiveSummaryHashMap.values().stream().collect(Collectors.toList());

    }

    private List<ReceiveSummary> callRecvSmryByInvoiceNum(InvoiceResponse invoiceResponse) {
        Query query = queryRecvSmryByInvoiceNum(invoiceResponse);
        return executeQuery(query);
    }

    private Query queryRecvSmryByInvoiceNum(InvoiceResponse invoiceResponse) {
        Query query = new Query();
        if (StringUtils.isNotEmpty(invoiceResponse.getInvoiceNumber())) {
            //TODO Uncomment below line after pagination test.
            // query.addCriteria(Criteria.where(ReceiveSummaryParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(invoiceResponse.getInvoiceNumber()));
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.TRANSACTIONTYPE.getParameterName()).is(1));
        }

        log.info("query: " + query);
        return query;
    }

    private List<ReceiveSummary> callRecvSmryByPOId(InvoiceResponse invoiceResponse) {

        Query query = queryRecvSmryByPOId(invoiceResponse);
        return executeQuery(query);
    }

    private Query queryRecvSmryByPOId(InvoiceResponse invoiceResponse) {
        Query query = new Query();
        if (StringUtils.isNotEmpty(invoiceResponse.getPurchaseOrderNumber())) {
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(invoiceResponse.getPurchaseOrderNumber()));
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.TRANSACTIONTYPE.getParameterName()).is(0));
        }

        log.info("query: " + query);
        return query;
    }

    private List<ReceiveSummary> callRecvSmryAllAttributes(InvoiceResponse invoiceResponse) {
        Query query = queryRecvSmryAllAttributes(invoiceResponse);
        return executeQuery(query);
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
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.STORENUMBER.getParameterName()).is(Integer.parseInt(invoiceResponse.getDestDivNbr().trim())));
        }
        if (StringUtils.isNotEmpty(invoiceResponse.getBaseDivisionNum())) {
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.BASEDIVISIONNUMBER.getParameterName()).is(Integer.parseInt(invoiceResponse.getBaseDivisionNum().trim())));
        }
        if (StringUtils.isNotEmpty(invoiceResponse.getVendorNumber())) {
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.VENDORNUMBER.getParameterName()).is(Integer.parseInt(invoiceResponse.getVendorNumber().trim())));
        }
        if (StringUtils.isNotEmpty(invoiceResponse.getInvoiceDeptNumber())) {
            query.addCriteria(Criteria.where(ReceiveSummaryParameters.DEPARTMENTNUMBER.getParameterName()).is(Integer.parseInt(invoiceResponse.getInvoiceDeptNumber().trim())));
        }

        log.info("query: " + query);
        return query;

    }

    private List<ReceiveSummary> executeQuery(Query query) {
        List<ReceiveSummary> receiveSummaries = mongoTemplate.find(query, ReceiveSummary.class, "receive-summary");
        return receiveSummaries;
    }

    private void listToMapConversion(List<ReceiveSummary> receiveSummaries, HashMap<String, ReceiveSummary> receiveSummaryHashMap) {
        receiveSummaries.stream().collect(Collectors.toMap((t) -> t.get_id(), (t) -> t));
    }

}


