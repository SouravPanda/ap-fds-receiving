package com.walmart.finance.ap.fds.receiving.integrations;

import java.util.List;
import java.util.Map;

public interface InvoiceIntegrationService {

    List<InvoiceResponseData> getInvoice(Map<String, String> invoiceNbr);

}
