package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public interface ReceiveSummaryService {
    ReceiveSummary saveReceiveSummary(ReceivingSummaryRequest receivingSummaryRequest);

    Page<ReceivingSummaryResponse> getReceiveSummary(String purchaseOrderNumber, String purchaseOrderId, String receiptNumber, String transactionType, String controlNumber, String locationNumber,
                                                     String divisionNumber, String vendorNumber, String departmentNumber, String invoiceId, String invoiceNumber, String receiptDateStart, String receiptDateEnd, int pageNbr, int pageSize, String orderBy, Sort.Direction order);// Map<String,String> allRequestParam);

    ReceivingSummaryRequest updateReceiveSummary(ReceivingSummaryRequest receivingSummarySearch, Integer vendorNumber, String countryCode);

    ReceivingSummaryLineRequest updateReceiveSummaryAndLine(ReceivingSummaryLineRequest receivingSummaryLineSearch, String countryCode, Integer vendorNumber);
}
