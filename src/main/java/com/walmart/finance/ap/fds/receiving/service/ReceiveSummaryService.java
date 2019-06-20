package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.response.SuccessMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReceiveSummaryService {

    SuccessMessage updateReceiveSummary(ReceivingSummaryRequest receivingSummarySearch, String countryCode);

    SuccessMessage updateReceiveSummaryAndLine(ReceivingSummaryLineRequest receivingSummaryLineSearch, String countryCode);

    SuccessMessage getReceiveSummary(String countryCode, String purchaseOrderNumber, String purchaseOrderId, List<String> receiptNumber, String transactionType, String controlNumber, String locationNumber,
                                     String divisionNumber, String vendorNumber, String departmentNumber, String invoiceId, String invoiceNumber, String receiptDateStart, String receiptDateEnd, List<String> itemNumbers, List<String> upcNumbers);// Map<String,String> allRequestParam);

}
