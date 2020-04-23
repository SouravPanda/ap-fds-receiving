package com.walmart.finance.ap.fds.receiving.runnableTask;

import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnIntegrationService;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnResponseData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class FinancialTransactionTask implements Callable<List<FinancialTxnResponseData>> {

    private Map<String, String> allRequestParams;

    private FinancialTxnIntegrationService financialTxnIntegrationService;

    public FinancialTransactionTask(Map<String, String> allRequestParams, FinancialTxnIntegrationService financialTxnIntegrationService) {
        this.allRequestParams = allRequestParams;
        this.financialTxnIntegrationService = financialTxnIntegrationService;
    }


    @Override
    public List<FinancialTxnResponseData> call() throws Exception {
        return financialTxnIntegrationService.getFinancialTxnDetails(allRequestParams);
    }
}
