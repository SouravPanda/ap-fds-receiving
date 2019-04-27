
package com.walmart.store.receive.service;

import com.walmart.store.receive.Request.ReceivingSummaryRequest;
import com.walmart.store.receive.Response.ReceivingLineResponse;
import com.walmart.store.receive.Response.ReceivingSummaryResponse;
import com.walmart.store.receive.pojo.ReceiveSummary;


public interface ReceiveLineService {
   ReceivingLineResponse getLineSummary(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime, String sequenceNumber);
}

