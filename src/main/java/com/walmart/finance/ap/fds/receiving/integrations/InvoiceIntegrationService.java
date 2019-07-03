package com.walmart.finance.ap.fds.receiving.integrations;

import java.util.HashMap;
import java.util.List;

public interface InvoiceIntegrationService {

    List<InvoiceResponseData> getInvoice(HashMap<String, String> invoiceNbr);

}
