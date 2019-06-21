
package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;


public interface ReceiveLineService {
    ReceivingResponse getLineSummary(String purchaseOrderId, String receiptNumbers, String transactionType, String controlNumber, String locationNumber, String divisionNumber);
}

