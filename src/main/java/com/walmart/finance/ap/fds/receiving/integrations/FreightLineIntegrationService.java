package com.walmart.finance.ap.fds.receiving.integrations;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;

import java.util.List;
import java.util.Map;

public interface FreightLineIntegrationService {

    Map<String, FreightResponse> getFreightLineAPIData(List<ReceiveSummary> summaries);
}
