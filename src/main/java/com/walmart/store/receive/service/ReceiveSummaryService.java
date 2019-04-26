package com.walmart.store.receive.service;

import com.walmart.store.receive.Request.ReceivingSummaryRequest;
import com.walmart.store.receive.Response.ReceivingSummaryResponse;
import com.walmart.store.receive.pojo.ReceiveSummary;



public interface ReceiveSummaryService {
  /*  List<ReceivingSummaryResponse> updateReceiveSummary(ReceivingSummaryRequest receivingSummaryRequest );*/
    ReceiveSummary saveReceiveSummary(ReceivingSummaryRequest receivingSummaryRequest);
   ReceivingSummaryResponse getReceiveSummary(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime);
}
