package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReceiveSummaryService {

    ReceivingResponse updateReceiveSummary(ReceivingSummaryRequest receivingSummarySearch, String countryCode);

    ReceivingResponse updateReceiveSummaryAndLine(ReceivingSummaryLineRequest receivingSummaryLineSearch, String countryCode);

    ReceivingResponse getReceiveSummary(String countryCode, String purchaseOrderNumber, String purchaseOrderId, String receiptNumber, String transactionType, String controlNumber, String locationNumber,
                                        String divisionNumber, String vendorNumber, String departmentNumber, String invoiceId, String invoiceNumber, String receiptDateStart, String receiptDateEnd, List<String> itemNumbers, List<String> upcNumbers);// Map<String,String> allRequestParam);

}
