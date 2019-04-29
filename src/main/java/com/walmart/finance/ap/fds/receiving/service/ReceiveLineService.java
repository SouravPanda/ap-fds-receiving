
package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;

public interface ReceiveLineService {
   ReceivingLineResponse getLineSummary(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime, String sequenceNumber);
}

