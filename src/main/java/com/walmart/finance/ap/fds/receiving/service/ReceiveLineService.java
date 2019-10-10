
package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;

import java.util.Map;


public interface ReceiveLineService {
    ReceivingResponse getLineSummary(Map<String, String> allRequestParams);
}
