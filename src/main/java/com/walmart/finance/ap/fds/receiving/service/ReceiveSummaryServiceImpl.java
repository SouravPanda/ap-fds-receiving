package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryReqConverter;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
import com.walmart.finance.ap.fds.receiving.integrations.InvoiceIntegrationService;
import com.walmart.finance.ap.fds.receiving.integrations.InvoiceResponse;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.repository.ReceiveSummaryDataRepository;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummarySearch;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


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

    @Override
    public Page<ReceivingSummaryResponse> getReceiveSummary(String purchaseOrderNumber, String purchaseOrderId, String receiptNumbers, String transactionType, String controlNumber, String locationNumber,
                                                            String divisionNumber, String vendorNumber, String departmentNumber, String invoiceId, String invoiceNumber, String receiptDateStart, String receiptDateEnd, int pageNbr, int pageSize, String orderBy, Sort.Direction order){// Map<String,String> allRequestParam) {


       // receiveSummaryValidator.validate(allRequestParam);
        Query query = searchCriteriaForGet(purchaseOrderNumber, purchaseOrderId, receiptNumbers, transactionType, controlNumber, locationNumber,
                divisionNumber, vendorNumber, departmentNumber, invoiceId, invoiceNumber, receiptDateStart, receiptDateEnd);
        Pageable pageable = PageRequest.of(pageNbr, pageSize);
        query.with(pageable);
        List<String> orderByproperties = new ArrayList<>();
        orderByproperties.add(orderBy);
        Sort sort = new Sort(order, orderByproperties);
        List<ReceiveSummary> receiveSummaries = mongoTemplate.find(query, ReceiveSummary.class, "receive-summary");
        Page<ReceiveSummary> receiveSummaryPage = PageableExecutionUtils.getPage(
                receiveSummaries,
                pageable,
                () -> mongoTemplate.count(query, ReceiveSummary.class));
        return mapReceivingSummaryToResponse(receiveSummaryPage);

    }

    @Override
    public Page<ReceivingSummaryResponse> getReceiveSummarySearch(ReceivingSummarySearch receivingSummarySearch, int pageNbr, int pageSize, String orderBy, Sort.Direction order) {

        Query dynamicQuery = new Query();

        // If invoiceId or invoiceNbr is present in search

        Query query;

        List<InvoiceResponse> invoiceResponse = new ArrayList<>();

        if (StringUtils.isNotEmpty(receivingSummarySearch.getInvoiceNumber()) || receivingSummarySearch.getInvoiceId() != null) {

            if (receivingSummarySearch.getInvoiceId() != null) {
                invoiceResponse.add(invoiceIntegrationService.getInvoiceByInvoiceId(receivingSummarySearch.getInvoiceId()));
            } else {
                invoiceResponse = invoiceIntegrationService.getInvoiceByinvoiceNbr(receivingSummarySearch.getInvoiceNumber());
            }
            query = searchCriteriaFromInvoiceResponse(invoiceResponse, dynamicQuery);
        } else {

            query = searchCriteria(receivingSummarySearch, dynamicQuery);

        }

        Pageable pageable = PageRequest.of(pageNbr, pageSize);
        dynamicQuery.with(pageable);
        List<String> orderByproperties = new ArrayList<>();
        orderByproperties.add(orderBy);
        Sort sort = new Sort(order, orderByproperties);
        // dynamicQuery.with(sort);

        List<ReceiveSummary> receiveSummaries = mongoTemplate.find(query, ReceiveSummary.class, "receive-summary");
        Page<ReceiveSummary> receiveSummaryPage = PageableExecutionUtils.getPage(
                receiveSummaries,
                pageable,
                () -> mongoTemplate.count(dynamicQuery, ReceiveSummary.class));

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


    private Query searchCriteria(ReceivingSummarySearch receivingSummarySearch, Query dynamicQuery) {

        if (StringUtils.isNotEmpty(receivingSummarySearch.getControlNumber()) || Optional.ofNullable(receivingSummarySearch.getPurchaseOrderId()).orElse(0L) != 0L) {

            if (StringUtils.isNotEmpty(receivingSummarySearch.getControlNumber())) {
                Criteria controlNumberCriteria = Criteria.where("receivingControlNumber").is(receivingSummarySearch.getControlNumber());
                dynamicQuery.addCriteria(controlNumberCriteria);
            } else {
                Criteria purchaseOrderIdCriteria = Criteria.where("receivingControlNumber").is(String.valueOf(receivingSummarySearch.getPurchaseOrderId()));
                dynamicQuery.addCriteria(purchaseOrderIdCriteria);

            }
        }

        if (Optional.ofNullable(receivingSummarySearch.getDivisionNumber()).orElse(0) != 0) {
            Criteria baseDivisionNumberCriteria = Criteria.where("baseDivisionNumber").is(receivingSummarySearch.getDivisionNumber());
            dynamicQuery.addCriteria(baseDivisionNumberCriteria);
        }

        if (receivingSummarySearch.getReceiptDateStart() != null || receivingSummarySearch.getReceiptDateEnd() != null) {
            if (receivingSummarySearch.getReceiptDateStart() != null) {
                Criteria mdsReceiveDateCriteria = Criteria.where("mdsReceiveDate").is(receivingSummarySearch.getReceiptDateStart().toLocalDate());
                dynamicQuery.addCriteria(mdsReceiveDateCriteria);
            } else {
                Criteria receiptDateEndCriteria = Criteria.where("mdsReceiveDate").is(receivingSummarySearch.getReceiptDateEnd().toLocalDate());
                dynamicQuery.addCriteria(receiptDateEndCriteria);
            }
        }

        if (Optional.ofNullable(receivingSummarySearch.getTransactionType()).orElse(0) != 0) {
            Criteria transactionTypeCriteria = Criteria.where("transactionType").is(receivingSummarySearch.getTransactionType());
            dynamicQuery.addCriteria(transactionTypeCriteria);
        }

        if (Optional.ofNullable(receivingSummarySearch.getLocationNumber()).orElse(0) != 0) {
            Criteria storeNumberCriteria = Criteria.where("storeNumber").is(receivingSummarySearch.getLocationNumber());
            dynamicQuery.addCriteria(storeNumberCriteria);
        }

        if (receivingSummarySearch.getPurchaseOrderNumber() != null) {
            Criteria purchaseOrderNumberCriteria = Criteria.where("purchaseOrderNumber").is(String.valueOf(receivingSummarySearch.getPurchaseOrderNumber()));
            dynamicQuery.addCriteria(purchaseOrderNumberCriteria);
        }

        if (StringUtils.isNotEmpty(receivingSummarySearch.getReceiptNumbers())) {
            Criteria poReceiveIdCriteria = Criteria.where("poReceiveId").is(Integer.parseInt(receivingSummarySearch.getReceiptNumbers()));
            dynamicQuery.addCriteria(poReceiveIdCriteria);
        }

        if (Optional.ofNullable(receivingSummarySearch.getDepartmentNumber()).orElse(0) != 0) {
            Criteria departmentNumberCriteria = Criteria.where("departmentNumber").is(receivingSummarySearch.getDepartmentNumber());
            dynamicQuery.addCriteria(departmentNumberCriteria);
        }

        if (Optional.ofNullable(receivingSummarySearch.getVendorNumber()).orElse(0) != 0) {
            Criteria vendorNumberCriteria = Criteria.where("vendorNumber").is(receivingSummarySearch.getVendorNumber());
            dynamicQuery.addCriteria(vendorNumberCriteria);
        }

        return dynamicQuery;
    }

    private Query searchCriteriaForGet(String purchaseOrderNumber, String purchaseOrderId, String receiptNumbers, String transactionType, String controlNumber, String locationNumber,
                                       String divisionNumber, String vendorNumber, String departmentNumber, String invoiceId, String invoiceNumber, String receiptDateStart, String receiptDateEnd) {
        Query dynamicQuery = new Query();

        if (StringUtils.isNotEmpty(controlNumber) || StringUtils.isNotEmpty(purchaseOrderId)) {

            if (StringUtils.isNotEmpty(controlNumber)) {
                Criteria controlNumberCriteria = Criteria.where("receivingControlNumber").is(controlNumber);
                dynamicQuery.addCriteria(controlNumberCriteria);
            } else {
                Criteria purchaseOrderIdCriteria = Criteria.where("receivingControlNumber").is(purchaseOrderId);
                dynamicQuery.addCriteria(purchaseOrderIdCriteria);

            }
        }

        if (StringUtils.isNotEmpty(divisionNumber)) {
            Criteria baseDivisionNumberCriteria = Criteria.where("baseDivisionNumber").is(Integer.valueOf(divisionNumber));
            dynamicQuery.addCriteria(baseDivisionNumberCriteria);
        }

        if (StringUtils.isNotEmpty(receiptDateStart) && StringUtils.isNotEmpty(receiptDateEnd)) {

            Criteria mdsReceiveDateCriteria = Criteria.where("MDSReceiveDate").gte(getDate(receiptDateStart)).lte(getDate(receiptDateEnd));
            dynamicQuery.addCriteria(mdsReceiveDateCriteria);
        }

        if (StringUtils.isNotEmpty(transactionType)) {
            Criteria transactionTypeCriteria = Criteria.where("transactionType").is(Integer.valueOf(transactionType));
            dynamicQuery.addCriteria(transactionTypeCriteria);
        }

        if (StringUtils.isNotEmpty(locationNumber)) {
            Criteria storeNumberCriteria = Criteria.where("storeNumber").is(Integer.valueOf(locationNumber));
            dynamicQuery.addCriteria(storeNumberCriteria);
        }

        if (StringUtils.isNotEmpty(purchaseOrderNumber)) {
            Criteria purchaseOrderNumberCriteria = Criteria.where("purchaseOrderNumber").is(purchaseOrderNumber);
            dynamicQuery.addCriteria(purchaseOrderNumberCriteria);
        }

        if (StringUtils.isNotEmpty(receiptNumbers)) {
            Criteria poReceiveIdCriteria = Criteria.where("poReceiveId").is(receiptNumbers);
            dynamicQuery.addCriteria(poReceiveIdCriteria);
        }

        if (StringUtils.isNotEmpty(departmentNumber)) {
            Criteria departmentNumberCriteria = Criteria.where("departmentNumber").is(Integer.valueOf(departmentNumber));
            dynamicQuery.addCriteria(departmentNumberCriteria);
        }

        if (StringUtils.isNotEmpty(vendorNumber)) {
            Criteria vendorNumberCriteria = Criteria.where("vendorNumber").is(Integer.valueOf(vendorNumber));
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

}


