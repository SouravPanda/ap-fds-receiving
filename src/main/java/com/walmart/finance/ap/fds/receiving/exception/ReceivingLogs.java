package com.walmart.finance.ap.fds.receiving.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ReceivingLogs {
    SEARCHCRITERIAFORGET("Inside getSearchCriteriaForGet method"),
    BEFORESIZESUMMARY("Before : size of recesummary list -"),
    INVOICEFROMINVSUMMARY("Inside getInvoiceFromInvoiceSummary method"),
    AFTERSIZESUMMARY("After : size of recesummary list -");

    @Getter
    private String parameterName;
}
