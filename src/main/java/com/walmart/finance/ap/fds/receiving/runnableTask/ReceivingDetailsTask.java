package com.walmart.finance.ap.fds.receiving.runnableTask;

import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoResponseV1;
import com.walmart.finance.ap.fds.receiving.service.ReceivingInfoService;
import org.slf4j.MDC;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ReceivingDetailsTask implements Callable<List<ReceivingInfoResponseV1>> {

    private Map<String, String> allRequestParams;

    private ReceivingInfoService receivingInfoService;

    public ReceivingDetailsTask(Map<String, String> allRequestParams, ReceivingInfoService receivingInfoService, Map<String, String> copyOfContextMap) {
        this.allRequestParams = allRequestParams;
        this.receivingInfoService =  receivingInfoService;
        MDC.setContextMap(copyOfContextMap);
    }

    @Override
    public List<ReceivingInfoResponseV1> call() throws Exception {
        return receivingInfoService.getReceivingInfoWoFinTxn(allRequestParams);
    }
}
