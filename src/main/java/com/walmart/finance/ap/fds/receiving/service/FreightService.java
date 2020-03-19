package com.walmart.finance.ap.fds.receiving.service;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;

public interface FreightService {

    ReceivingResponse getFreightById(String id);
}
