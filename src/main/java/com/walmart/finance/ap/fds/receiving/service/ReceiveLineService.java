
package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.request.ReceiveLineSearch;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;


public interface ReceiveLineService {
   Page<ReceivingLineResponse> getLineSummary(String purchaseOrderId,String receiptNumbers, String transactionType,String controlNumber, String locationNumber, String divisionNumber,int pageNbr, int pageSize, String orderBy,Sort.Direction order);
   Page<ReceivingLineResponse> getReceiveLineSearch(ReceiveLineSearch receivingLineSearch, int pageNbr, int pageSize, String orderBy);
}

