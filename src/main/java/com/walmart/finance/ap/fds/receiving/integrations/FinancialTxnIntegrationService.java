package com.walmart.finance.ap.fds.receiving.integrations;

import java.util.List;
import java.util.Map;

public interface FinancialTxnIntegrationService {

    List<FinancialTxnResponseData> getFinancialTxnDetails(Map<String, String> allRequestParams);
}
