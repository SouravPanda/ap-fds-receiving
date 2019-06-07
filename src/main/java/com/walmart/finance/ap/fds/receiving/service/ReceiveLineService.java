
package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import org.springframework.data.domain.Sort;

import java.util.List;


public interface ReceiveLineService {
    List<ReceivingLineResponse> getLineSummary(String purchaseOrderId, String receiptNumbers, String transactionType, String controlNumber, String locationNumber, String divisionNumber);
}

