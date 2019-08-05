package com.walmart.finance.ap.fds.receiving.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ReceiveSummaryRequestParams {

    PURCHASEORDERNUMBER("purchaseOrderNumber"),
    PURCHASEORDERID("purchaseOrderId"),
    RECEIPTNUMBERS("receiptNumbers"),
    ITEMNUMBERS("itemNumbers"),
    UPCNUMBERS("upcNumbers"),
    CONTROLNUMBER("controlNumber"),
    LOCATIONNUMBER("locationNumber"),
    TRANSACTIONTYPE("transactionType"),
    DIVISIONNUMBER("divisionNumber"),
    VENDORNUMBER("vendorNumber"),
    DEPARTMENTNUMBER("departmentNumber"),
    INVOICEID("invoiceId"),
    INVOICENUMBER("invoiceNumber"),
    RECEIPTDATESTART("receiptDateStart"),
    RECEIPTDATEEND("receiptDateEnd");

    @Getter
    private String parameterName;

}
