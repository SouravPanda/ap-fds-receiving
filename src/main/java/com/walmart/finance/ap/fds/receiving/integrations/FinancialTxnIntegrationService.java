package com.walmart.finance.ap.fds.receiving.integrations;

import java.util.Map;

public interface FinancialTxnIntegrationService {

    FinancialTxnResponse[] getFinancialTxnDetails(Map<String, String> queryParamMap);
}
