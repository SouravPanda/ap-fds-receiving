package com.walmart.finance.ap.fds.receiving.integrations;

import java.util.List;

public interface InvoiceIntegrationService {

    InvoiceResponse getInvoiceByInvoiceId(Long invoiceId);

    List<InvoiceResponse> getInvoiceByinvoiceNbr(String invoiceNbr);

}
