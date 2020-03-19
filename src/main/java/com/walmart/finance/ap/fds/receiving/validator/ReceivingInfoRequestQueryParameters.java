package com.walmart.finance.ap.fds.receiving.validator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ReceivingInfoRequestQueryParameters {

    INVOICEID("invoiceId"),
    INVOICENUMBER("invoiceNumber"),
    PURCHASEORDERNUMBER("purchaseOrderNumber"),
    PURCHASEORDERID("purchaseOrderId"),
    RECEIPTNUMBERS("receiptNumbers"),
    TRANSACTIONTYPE("transactionType"),
    CONTROLNUMBER("controlNumber"),
    LOCATIONNUMBER("locationNumber"),
    DIVISIONNUMBER("divisionNumber"),
    VENDORNUMBER("vendorNumber"),
    DEPARTMENTNUMBER("departmentNumber"),
    ITEMNUMBERS("itemNumbers"),
    UPCNUMBERS("upcNumbers"),
    RECEIPTDATESTART("receiptDateStart"),
    RECEIPTDATEEND("receiptDateEnd"),
    LINENUMBERFLAG("lineNumberFlag"),
    COUNTRYCODE("countryCode"),
    LOCATIONTYPE("locationType"),
    TRANSACTIONID("transactionId"),
    TRANSACTIONSEQNBR("txnSeqNbr");


    @Getter
    private String queryParam;
}
