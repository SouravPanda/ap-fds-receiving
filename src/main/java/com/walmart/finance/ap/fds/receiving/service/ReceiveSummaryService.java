package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummarySearch;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public interface ReceiveSummaryService {
   ReceiveSummary saveReceiveSummary(ReceivingSummaryRequest receivingSummaryRequest);
   ReceivingSummaryResponse getReceiveSummary(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime);
   Page<ReceivingSummaryResponse> getReceiveSummarySearch(ReceivingSummarySearch receivingSummarySearch,int pageNbr, int pageSize, String orderBy, Sort.Direction order);
}
