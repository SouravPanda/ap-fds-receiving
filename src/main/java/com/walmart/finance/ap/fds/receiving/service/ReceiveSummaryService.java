package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReceiveSummaryService {
    ReceiveSummary saveReceiveSummary(ReceivingSummaryRequest receivingSummaryRequest);

  //  Page<ReceivingSummaryResponse> getReceiveSummary(String purchaseOrderNumber, String purchaseOrderId, String receiptNumber, String transactionType, String controlNumber, String locationNumber,
  //                                                   String divisionNumber, String vendorNumber, String departmentNumber, String invoiceId, String invoiceNumber, String receiptDateStart, String receiptDateEnd, int pageNbr, int pageSize, String orderBy, Sort.Direction order);// Map<String,String> allRequestParam);

    ReceivingSummaryRequest updateReceiveSummary(ReceivingSummaryRequest receivingSummarySearch, String countryCode);

    ReceivingSummaryLineRequest updateReceiveSummaryAndLine(ReceivingSummaryLineRequest receivingSummaryLineSearch, String countryCode);

    List<ReceivingSummaryResponse> getReceiveSummary(String countryCode, String purchaseOrderNumber, String purchaseOrderId, List<String> receiptNumber, String transactionType, String controlNumber, String locationNumber,
                                                     String divisionNumber, String vendorNumber, String departmentNumber, String invoiceId, String invoiceNumber, String receiptDateStart, String receiptDateEnd, List<String> itemNumbers, List<String> upcNumbers);// Map<String,String> allRequestParam);

}
