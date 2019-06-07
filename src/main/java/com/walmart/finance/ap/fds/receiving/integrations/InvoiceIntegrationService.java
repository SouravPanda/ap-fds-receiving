package com.walmart.finance.ap.fds.receiving.integrations;

import java.util.HashMap;

public interface InvoiceIntegrationService {

    InvoiceResponse[] getInvoice(HashMap<String, String> invoiceNbr);

}
