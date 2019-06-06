package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryReqConverter;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
import com.walmart.finance.ap.fds.receiving.exception.ContentNotFoundException;
import com.walmart.finance.ap.fds.receiving.exception.InvalidValueException;
import com.walmart.finance.ap.fds.receiving.integrations.InvoiceResponse;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
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
import java.util.List;
import java.util.Objects;
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
    ReceiveSummaryValidator receiveSummaryValidator;

    @Autowired
    ReceiveSummaryLineValidator receiveSummaryLineValidator;

    @Autowired
    private ApplicationEventPublisher publisher;


    // TODO validation for incoming against MDM needs to be added later

    public ReceiveSummary saveReceiveSummary(ReceivingSummaryRequest receivingSummaryRequest) {
        ReceiveSummary receiveSummary = receivingSummaryReqConverter.convert(receivingSummaryRequest);
        return receiveDataRepository.save(receiveSummary);

    }

    @Override
    public Page<ReceivingSummaryResponse> getReceiveSummary(String purchaseOrderNumber, String purchaseOrderId, String receiptNumbers, String transactionType, String controlNumber, String locationNumber,
                                                            String divisionNumber, String vendorNumber, String departmentNumber, String invoiceId, String invoiceNumber, String receiptDateStart, String receiptDateEnd,
                                                            int pageNbr, int pageSize, String orderBy, Sort.Direction order) {// Map<String,String> allRequestParam) {

        Query query = searchCriteriaForGet(purchaseOrderNumber, purchaseOrderId, receiptNumbers, transactionType, controlNumber, locationNumber,
                divisionNumber, vendorNumber, departmentNumber, invoiceId, invoiceNumber, receiptDateStart, receiptDateEnd);
        Pageable pageable = PageRequest.of(pageNbr, pageSize);
        query.with(pageable);
        List<String> orderByproperties = new ArrayList<>();
        orderByproperties.add(orderBy);
        Sort sort = new Sort(order, orderByproperties);
        List<ReceiveSummary> receiveSummaries = mongoTemplate.find(query, ReceiveSummary.class, "receiving-summary");
        Page<ReceiveSummary> receiveSummaryPage = PageableExecutionUtils.getPage(
                receiveSummaries,
                pageable,
                () -> mongoTemplate.count(query, ReceiveSummary.class));
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

    private String formulateLineId(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime, String sequenceNumber) {
        return receivingControlNumber + ReceivingConstants.PIPE_SEPARATOR + poReceiveId + ReceivingConstants.PIPE_SEPARATOR + storeNumber + ReceivingConstants.PIPE_SEPARATOR + baseDivisionNumber + ReceivingConstants.PIPE_SEPARATOR + transactionType + ReceivingConstants.PIPE_SEPARATOR + finalDate + ReceivingConstants.PIPE_SEPARATOR + finalTime + ReceivingConstants.PIPE_SEPARATOR + sequenceNumber;
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
                receiveLine.setUpcNumber(receivingSummaryLineRequest.getUpcNumber());
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
            receivingLine.setUpcNumber(receivingSummaryLineRequest.getUpcNumber());
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


}


