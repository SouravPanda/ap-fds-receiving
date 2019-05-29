package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.common.ReceiveSummaryBusinessStat;
import com.walmart.finance.ap.fds.receiving.exception.InvalidValueException;
import com.walmart.finance.ap.fds.receiving.integrations.VendorIntegrationServiceImpl;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummarySearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ReceiveSummaryValidator {

    @Autowired
    VendorIntegrationServiceImpl vendorIntegrationService;

    public static final String purchaseOrderNumber = "purchaseOrderNumber";
    public static final String purchaseOrderId = "purchaseOrderId";
    public static final String receiptNumbers = "receiptNumbers";
    public static final String transactionType = "transactionType";
    public static final String controlNumber = "controlNumber";
    public static final String locationNumber = "locationNumber";
    public static final String divisionNumber = "divisionNumber";
    public static final String vendorNumber = "vendorNumber";
    public static final String departmentNumber = "departmentNumber";
    public static final String invoiceId = "invoiceId";
    public static final String invoiceNumber = "invoiceNumber";
    public static final String receiptDateStart = "receiptDateStart";
    public static final String receiptDateEnd = "receiptDateEnd";
    public static final Integer pageNbr = 0;
    public static final Integer pageSize = 10;
    public static final String orderBy = "creationDate";
    public static final Sort.Direction order = Sort.Direction.DESC;
    boolean verdict = false;
    List<ReceiveSummaryBusinessStat> businessStatList = Arrays.asList(ReceiveSummaryBusinessStat.values());
    public static final List<Object> comparisonList = new ArrayList<Object>();

    public void validate(Map<String, String> allRequestParam) {
        comparisonList.add(purchaseOrderNumber);
        comparisonList.add(purchaseOrderId);
        comparisonList.add(receiptNumbers);
        comparisonList.add(transactionType);
        comparisonList.add(controlNumber);
        comparisonList.add(locationNumber);
        comparisonList.add(divisionNumber);
        comparisonList.add(vendorNumber);
        comparisonList.add(departmentNumber);
        comparisonList.add(invoiceId);
        comparisonList.add(invoiceNumber);
        comparisonList.add(receiptDateStart);
        comparisonList.add(receiptDateEnd);
        comparisonList.add(pageNbr);
        comparisonList.add(pageSize);
        comparisonList.add(orderBy);
        comparisonList.add(order);

        for (Map.Entry<String, String> entry : allRequestParam.entrySet())
            for (int i = 0; i < comparisonList.size(); i++) {
                if (entry.getKey().equalsIgnoreCase(comparisonList.get(i).toString())) {
                    verdict = true;
                }
            }
        if (verdict == false)
            throw new InvalidValueException("Incorrect fields passed");
    }

    public boolean validateVendorNumberUpdateSummary(ReceivingSummarySearch receivingSummarySearch, Integer vendorNumber, String countryCode) {
        if (vendorIntegrationService.getVendorBySupplierNumberAndCountryCode(vendorNumber, countryCode).equals(receivingSummarySearch.getVendorNumber())) {
            return !verdict;
        }
        return verdict;
    }

    public boolean validateBusinessStatUpdateSummary(ReceivingSummarySearch receivingSummarySearch) {
        for (ReceiveSummaryBusinessStat businessStat : businessStatList) {
            if (businessStat.toString().equalsIgnoreCase(receivingSummarySearch.getBusinessStatusCode())) {
                verdict = true;
                break;
            }
        }
        return verdict;

    }

    public boolean validateControlType(ReceivingSummarySearch receivingSummarySearch) {
        Set<String> controlNumberSet = new HashSet<>();
        controlNumberSet.add("0");
        controlNumberSet.add("1");
        controlNumberSet.add("2");
        controlNumberSet.add("3");
        controlNumberSet.add("99");
        if (controlNumberSet.contains(receivingSummarySearch.getControlNumber())) {
            verdict = true;
        }
        return verdict;
    }

}



