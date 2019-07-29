package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;

import java.util.Map;

/**
 * Service layer interface for receiving info API.
 */
public interface ReceivingInfoService {

    ReceivingResponse getInfoSeviceData(Map<String, String> allRequestParams);
}
