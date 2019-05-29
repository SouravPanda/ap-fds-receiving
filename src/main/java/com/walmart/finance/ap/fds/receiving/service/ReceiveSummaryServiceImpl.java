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
import com.walmart.finance.ap.fds.receiving.request.ReceiveSummaryLineSearch;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummarySearch;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryLineValidator;
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

    @Override
    public ReceivingSummarySearch updateReceiveSummary(ReceivingSummarySearch receivingSummarySearch, Integer vendorNumber, String countryCode) {


        if (receivingSummarySearch != null) {


            String id = formulateId(receivingSummarySearch.getControlNumber(), receivingSummarySearch.getReceiptNumbers(), receivingSummarySearch.getLocationNumber().toString(),
                    receivingSummarySearch.getDivisionNumber().toString(), receivingSummarySearch.getTransactionType().toString(), "0", "0");

            ReceiveSummary receiveSummary = mongoTemplate.findById(id, ReceiveSummary.class, "receive-summary");
            if (receiveSummary != null) {
                receiveSummary.setReceivingControlNumber(receivingSummarySearch.getInvoiceNumber());
                receiveSummary.setPurchaseOrderNumber(receivingSummarySearch.getPurchaseOrderNumber().toString());
                receiveSummary.setDepartmentNumber(receivingSummarySearch.getDepartmentNumber());
                receiveSummary.setTotalCostAmount(receivingSummarySearch.getCostAmount());
                receiveSummary.setTotalRetailAmount(receivingSummarySearch.getRetailAmount());
                if (receivingSummarySearch.getDepartmentNumber() >= 0 && receivingSummarySearch.getDepartmentNumber() <= 99) {
                    receiveSummary.setDepartmentNumber(receivingSummarySearch.getDepartmentNumber());
                }
                receiveSummary.setPoReceiveId(receivingSummarySearch.getPurchaseOrderId().toString());
                receiveSummary.setVendorNumber(receivingSummarySearch.getVendorNumber());
                receiveSummary.setAccountNumber(receivingSummarySearch.getAccountNumber());
                receiveSummary.setClaimPendingIndicator(receivingSummarySearch.getClaimPendingIndicator());
                receiveSummary.setControlSequenceNumber(receivingSummarySearch.getControlSequenceNumber());
                if (receiveSummaryValidator.validateControlType(receivingSummarySearch) == true) {
                    receiveSummary.setControlType(receivingSummarySearch.getControlType());
                }
                receiveSummary.setFreeAstrayIndicator(receivingSummarySearch.getFreeAstrayIndicator());
                receiveSummary.setFreightConslIndicator(receivingSummarySearch.getFreightConslIndicator());
                receiveSummary.setMatchIndicator(receivingSummarySearch.getMatchIndicator());
                receiveSummary.setReceiveSequenceNumber(receivingSummarySearch.getReceiveSequenceNumber());
                receiveSummary.setReceiveWeightQuantity(receivingSummarySearch.getReceiveWeightQuantity());
                receiveSummary.setWriteIndicator(receivingSummarySearch.getWriteIndicator());
                receiveSummary.setTypeIndicator(receivingSummarySearch.getTypeIndicator());
                receiveSummary.setUserId(receiveSummary.getUserId());
                receiveSummary.setBaseDivisionNumber(receivingSummarySearch.getDivisionNumber());
                receiveSummary.setStoreNumber(receivingSummarySearch.getLocationNumber());
                receiveSummary.setMDSReceiveDate(receivingSummarySearch.getReceiptDateStart().toLocalDate());//TODO, do we need to change?
                receiveSummary.setMDSReceiveDate(receivingSummarySearch.getReceiptDateEnd().toLocalDate());
                receiveSummary.setFreightBillId(receivingSummarySearch.getFreightBillId());
                receiveSummary.setSequenceNumber(receivingSummarySearch.getSequenceNumber());
                receiveSummary.setPoReceiveId(receivingSummarySearch.getReceiptNumbers());

                if (receiveSummaryValidator.validateVendorNumberUpdateSummary(receivingSummarySearch, vendorNumber, countryCode) == true) {
                    receiveSummary.setVendorNumber(receivingSummarySearch.getVendorNumber());
                } else {
                    throw new InvalidValueException("Value of field vendorNumber passed is not valid");
                }
                if (receiveSummaryValidator.validateBusinessStatUpdateSummary(receivingSummarySearch) == true) {
                    receiveSummary.setBusinessStatusCode(receivingSummarySearch.getBusinessStatusCode().charAt(0));
                } else {
                    throw new InvalidValueException("Value of field  businessStatusCode passed is not valid");
                }
            } else {
                throw new ContentNotFoundException("The content not found for the given id");
            }
            mongoTemplate.save(receiveSummary, "receive-summary");

        }
        return receivingSummarySearch;
    }

    @Override
    public ReceiveSummaryLineSearch updateReceiveSummaryAndLine(ReceiveSummaryLineSearch receivingSummaryLineSearch, String countryCode, Integer vendorNumber) {
        Query dynamicQuery = new Query();
        List<ReceivingLine> receiveLines = new ArrayList();

      String id = formulateId(receivingSummaryLineSearch.getControlNumber(), receivingSummaryLineSearch.getReceiptNumber().toString(), receivingSummaryLineSearch.getLocationNumber().toString(),
              receivingSummaryLineSearch.getDivisionNumber().toString(), receivingSummaryLineSearch.getTransactionType().toString(), "0", "0");

      // String id = "708542521|30005|1018|0|99|0|0";

        ReceiveSummary receiveSummary = mongoTemplate.findById(id, ReceiveSummary.class, "receiving-summary");

        if (receiveSummary == null) {
            throw new ContentNotFoundException("No content found for the given id");
        }

        receiveSummary.setReceivingControlNumber(receivingSummaryLineSearch.getControlNumber());
        if (receivingSummaryLineSearch.getPurchasedOrderId() != null) {
            receiveSummary.setReceivingControlNumber(receivingSummaryLineSearch.getPurchasedOrderId().toString());
        }
        receiveSummary.setPurchaseOrderNumber(receivingSummaryLineSearch.getPurchaseOrderNumber());
        if (receivingSummaryLineSearch.getDepartmentNumber() >= 0 && receivingSummaryLineSearch.getDepartmentNumber() <= 99) {
            receiveSummary.setDepartmentNumber(receivingSummaryLineSearch.getDepartmentNumber());
        }
        if (Objects.nonNull(receivingSummaryLineSearch) && receivingSummaryLineSearch.getPurchasedOrderId() != null) {
            receiveSummary.setPoReceiveId(receivingSummaryLineSearch.getPurchaseOrderId().toString());
        }
        receiveSummary.setVendorNumber(receivingSummaryLineSearch.getVendorNumber());
        receiveSummary.setAccountNumber(receivingSummaryLineSearch.getAccountNumber());
        receiveSummary.setCasesReceived(receivingSummaryLineSearch.getCasesReceived());
        receiveSummary.setClaimPendingIndicator(receivingSummaryLineSearch.getClaimPendingIndicator());
        receiveSummary.setControlSequenceNumber(receivingSummaryLineSearch.getControlSequenceNumber());
        if (receiveSummaryLineValidator.validateControlType(receivingSummaryLineSearch) == true) {
            receiveSummary.setControlType(receivingSummaryLineSearch.getControlType());
        } else {
            throw new InvalidValueException("Value of field controlType passed is not valid");
        }
        receiveSummary.setFinalDate(receivingSummaryLineSearch.getFinalDate());
        receiveSummary.setFinalizedLoadTimestamp(receivingSummaryLineSearch.getFinalizedLoadTimestamp());
        receiveSummary.setFreeAstrayIndicator(receivingSummaryLineSearch.getFreeAstrayIndicator());
        receiveSummary.setFinalizedSequenceNumber(receivingSummaryLineSearch.getFinalizedSequenceNumber());
        receiveSummary.setFreightBillExpandID(receivingSummaryLineSearch.getFreightBillExpandID());
        receiveSummary.setFreightConslIndicator(receivingSummaryLineSearch.getFreightConslIndicator());
        receiveSummary.setReceiveSequenceNumber(receivingSummaryLineSearch.getReceiveSequenceNumber());
        receiveSummary.setReceiveWeightQuantity(receivingSummaryLineSearch.getReceiveWeightQuantity());
        receiveSummary.setUserId(receiveSummary.getUserId());
        receiveSummary.setBaseDivisionNumber(receivingSummaryLineSearch.getDivisionNumber());
        receiveSummary.setStoreNumber(receivingSummaryLineSearch.getLocationNumber());
        receiveSummary.setMDSReceiveDate(receivingSummaryLineSearch.getReceiptDateStart().toLocalDate());
        receiveSummary.setMDSReceiveDate(receivingSummaryLineSearch.getReceiptDateEnd().toLocalDate());
        receiveSummary.setFreightBillId(receivingSummaryLineSearch.getFreightBillId());
        receiveSummary.setSequenceNumber(receivingSummaryLineSearch.getSequenceNumber());
        receiveSummary.setFinalTime(receivingSummaryLineSearch.getFinalTime());
        if (receiveSummaryLineValidator.validateBusinessStatUpdateSummary(receivingSummaryLineSearch) == true) {
            receiveSummary.setBusinessStatusCode(receivingSummaryLineSearch.getBusinessStatusCode().charAt(0));
        } else {
            throw new InvalidValueException("Value of field  businessStatusCode passed is not valid");
        }
        if (receivingSummaryLineSearch.getReceiptNumber() != null) {
            receiveSummary.setPoReceiveId(receivingSummaryLineSearch.getReceiptNumber().toString());
        }
        mongoTemplate.save(receiveSummary, "receiving-summary");


        if (StringUtils.isNotEmpty(receivingSummaryLineSearch.getSequenceNumber().toString())) {

            String lineId = formulateLineId(receivingSummaryLineSearch.getControlNumber(), receivingSummaryLineSearch.getReceiptNumber().toString(), receivingSummaryLineSearch.getLocationNumber().toString(),
                    receivingSummaryLineSearch.getDivisionNumber().toString(), receivingSummaryLineSearch.getTransactionType().toString(), "0", "0", receivingSummaryLineSearch.getSequenceNumber().toString());

            ReceivingLine receiveLine = mongoTemplate.findById(lineId, ReceivingLine.class, "receive-line");

            // set all the filed and save
            if (receiveLine != null) {
                receiveLine.setBaseDivisionNumber(receivingSummaryLineSearch.getBaseDivisionNumber());
                receiveLine.setCostAmount(receivingSummaryLineSearch.getCostAmount());
                receiveLine.setMDSReceiveDate(receivingSummaryLineSearch.getReceiptDateStart().toLocalDate());
                receiveLine.setMDSReceiveDate(receivingSummaryLineSearch.getReceiptDateEnd().toLocalDate());
                receiveLine.setFinalDate(receivingSummaryLineSearch.getFinalDate());
                receiveLine.setCostAmount(receivingSummaryLineSearch.getCostAmount());
                //  receiveLine.setFinalTime(receivingSummaryLineSearch.getFinalTime());//needs clarifications, check with Rupesh
                receiveLine.setFinalDate(receivingSummaryLineSearch.getFinalDate());
                receiveLine.setItemNumber(receivingSummaryLineSearch.getItemNumber());
                receiveLine.setLineNumber(receivingSummaryLineSearch.getLineNumber());
                receiveLine.setPurchasedOrderId(receivingSummaryLineSearch.getPurchasedOrderId());
                receiveLine.setPurchaseOrderNumber(receivingSummaryLineSearch.getPurchaseOrderNumber());
                receiveLine.setPurchaseReceiptNumber(receivingSummaryLineSearch.getPurchaseReceiptNumber());
                //receiveLine.setQuantity(receivingSummaryLineSearch.getReceivedQuantity());//check with Rupesh
                receiveLine.setPurchaseOrderReceiveID(receivingSummaryLineSearch.getReceiptNumber().toString());
                receiveLine.setReceivedQuantityUnitOfMeasureCode(receivingSummaryLineSearch.getReceivedQuantityUnitOfMeasureCode());
                receiveLine.setReceiveSequenceNumber(receivingSummaryLineSearch.getReceiveSequenceNumber());
                receiveLine.setReceivedQuantity(receivingSummaryLineSearch.getReceivedQuantity());
                receiveLine.setReceivedWeightQuantity(receivingSummaryLineSearch.getReceivedWeightQuantity());
                receiveLine.setReceivingControlNumber(receivingSummaryLineSearch.getControlNumber());
                receiveLine.setReceivingControlNumber(receivingSummaryLineSearch.getPurchasedOrderId().toString());
                receiveLine.setRetailAmount(receivingSummaryLineSearch.getRetailAmount());
                receiveLine.setSequenceNumber(receivingSummaryLineSearch.getSequenceNumber());
                receiveLine.setStoreNumber(receivingSummaryLineSearch.getLocationNumber());
                receiveLine.setTransactionType(receivingSummaryLineSearch.getTransactionType());
                receiveLine.setUpcNumber(receivingSummaryLineSearch.getUpcNumber());
                if (receiveSummaryLineValidator.validateVendorNumberUpdateSummary(receivingSummaryLineSearch, vendorNumber, countryCode) == true) {
                    receiveLine.setVendorNumber(receivingSummaryLineSearch.getVendorNumber());
                } else {
                    throw new InvalidValueException("Value of field vendorNumber passed is not valid");
                }
                mongoTemplate.save(receiveLine, "receive-line");

            }

        } else {

            // TODO, ideally we should have receiveSummary key reference in Receive Line

            if ((receivingSummaryLineSearch.getPurchasedOrderId() != null) || (receivingSummaryLineSearch.getControlNumber() != null)) {
                if (receivingSummaryLineSearch.getPurchasedOrderId() != null) {
                    Criteria purchaseOrderIdCriteria = Criteria.where("receivingControlNumber").is(receivingSummaryLineSearch.getPurchasedOrderId().toString());//TODO,purchasedOrderId, needed in COSMOS
                    dynamicQuery.addCriteria(purchaseOrderIdCriteria);
                } else {
                    Criteria controlNumberCriteria = Criteria.where("receivingControlNumber").is(receivingSummaryLineSearch.getControlNumber());
                    dynamicQuery.addCriteria(controlNumberCriteria);
                }
            }
            if (receivingSummaryLineSearch.getReceiptNumber() != null) {
                Criteria receiptNumberCriteria = Criteria.where("purchaseOrderReceiveID").is(receivingSummaryLineSearch.getReceiptNumber());
                dynamicQuery.addCriteria(receiptNumberCriteria);
            }
            if (receivingSummaryLineSearch.getTransactionType() != null) {
                Criteria transactionTypeCriteria = Criteria.where("transactionType").is(receivingSummaryLineSearch.getTransactionType());
                dynamicQuery.addCriteria(transactionTypeCriteria);
            }

            if (receivingSummaryLineSearch.getBaseDivisionNumber() != null) {
                Criteria divisionNumberCriteria = Criteria.where("baseDivisionNumber").is(receivingSummaryLineSearch.getBaseDivisionNumber());
                dynamicQuery.addCriteria(divisionNumberCriteria);
            }
            if (receivingSummaryLineSearch.getStoreNumber() != null) {
                Criteria locationNumberCriteria = Criteria.where("storeNumber").is(Integer.valueOf(receivingSummaryLineSearch.getStoreNumber()));
                dynamicQuery.addCriteria(locationNumberCriteria);
            }

        }
        List<ReceivingLine> receivingLineList = mongoTemplate.find(dynamicQuery, ReceivingLine.class, "receive-line");
        for (ReceivingLine receivingLine : receivingLineList) {
            receivingLine.setBaseDivisionNumber(receivingSummaryLineSearch.getBaseDivisionNumber());
            receivingLine.setCostAmount(receivingSummaryLineSearch.getCostAmount());
            receivingLine.setMDSReceiveDate(receivingSummaryLineSearch.getReceiptDateStart().toLocalDate());
            receivingLine.setMDSReceiveDate(receivingSummaryLineSearch.getReceiptDateEnd().toLocalDate());
            receivingLine.setFinalDate(receivingSummaryLineSearch.getFinalDate());
            receivingLine.setCostAmount(receivingSummaryLineSearch.getCostAmount());
            receivingLine.setFinalDate(receivingSummaryLineSearch.getFinalDate());
            receivingLine.setItemNumber(receivingSummaryLineSearch.getItemNumber());
            receivingLine.setLineNumber(receivingSummaryLineSearch.getLineNumber());
            receivingLine.setPurchasedOrderId(receivingSummaryLineSearch.getPurchasedOrderId());
            receivingLine.setPurchaseOrderNumber(receivingSummaryLineSearch.getPurchaseOrderNumber());
            receivingLine.setPurchaseReceiptNumber(receivingSummaryLineSearch.getPurchaseReceiptNumber());
            receivingLine.setQuantity(receivingSummaryLineSearch.getReceivedQuantity());//check with Rupesh
            receivingLine.setPurchaseOrderReceiveID(receivingSummaryLineSearch.getPurchaseOrderReceiveID());
            receivingLine.setReceivedQuantityUnitOfMeasureCode(receivingSummaryLineSearch.getReceivedQuantityUnitOfMeasureCode());
            receivingLine.setReceiveSequenceNumber(receivingSummaryLineSearch.getReceiveSequenceNumber());
            receivingLine.setReceivedQuantity(receivingSummaryLineSearch.getReceivedQuantity());//check
            receivingLine.setReceivedWeightQuantity(receivingSummaryLineSearch.getReceivedWeightQuantity());
            receivingLine.setReceivingControlNumber(receivingSummaryLineSearch.getReceivingControlNumber());//check
            receivingLine.setRetailAmount(receivingSummaryLineSearch.getRetailAmount());
            receivingLine.setStoreNumber(receivingSummaryLineSearch.getLocationNumber());
            receivingLine.setTransactionType(receivingSummaryLineSearch.getTransactionType());
            receivingLine.setUpcNumber(receivingSummaryLineSearch.getUpcNumber());
            if (receiveSummaryLineValidator.validateVendorNumberUpdateSummary(receivingSummaryLineSearch, vendorNumber, countryCode)) {
                receivingLine.setVendorNumber(receivingSummaryLineSearch.getVendorNumber());
            } else {
                throw new InvalidValueException("Value of field vendorNumber passed is not valid");
            }
            receiveLines.add(receivingLine);
        }
        mongoTemplate.save(receiveLines, "receive-line");
        return receivingSummaryLineSearch;
    }


}


