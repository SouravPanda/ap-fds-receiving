package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ReceiveSummaryService {

    ReceivingResponse updateReceiveSummary(ReceivingSummaryRequest receivingSummarySearch, String countryCode);

    ReceivingResponse updateReceiveSummaryAndLine(ReceivingSummaryLineRequest receivingSummaryLineSearch, String countryCode);

    ReceivingResponse getReceiveSummary(Map<String, String> allRequestParams);

}
