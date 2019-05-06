
package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.request.ReceiveLineSearch;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;


public interface ReceiveLineService {
   ReceivingLineResponse getLineSummary(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime, String sequenceNumber);
   ReceivingLineResponse getReceiveLine(ReceiveLineSearch receivingLineSearch);
   Page<ReceivingLineResponse> getReceiveLineSearch(ReceiveLineSearch receivingLineSearch, int pageNbr, int pageSize, String orderBy, Sort.Direction order);
}

