package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoResponseV1;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;

import java.util.List;
import java.util.Map;

/**
 * Service layer interface for receiving info API.
 */
public interface ReceivingInfoService {

    ReceivingResponse getInfoSeviceData(Map<String, String> allRequestParams);

    ReceivingResponse getInfoSeviceDataV1(Map<String, String> allRequestParams);

    List<ReceivingInfoResponseV1> getReceivingInfoWoFinTxn(Map<String, String> allRequestParams);
}
