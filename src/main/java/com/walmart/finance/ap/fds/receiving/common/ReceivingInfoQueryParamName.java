package com.walmart.finance.ap.fds.receiving.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public enum ReceivingInfoQueryParamName {

    COUNTRYCODE("countryCode"), INVOICEID("invoiceId"), INVOICENUMBER("invoiceNumber"), PURCHASEORDERNUMBER("purchaseOrderNumber"), PURCHASEORDERID("purchaseOrderId"), TRANSACTIONTYPE("transactionType"), CONTROLNUMBER("controlNumber"), LOCATIONNUMBER("locationNumber"), DIVISIONNUMBER("divisionNumber"), VENDORNUMBER("vendorNumber"), DEPARTMENTNUMBER("departmentNumber"), RECEIPTDATESTART("receiptDateStart"), RECEIPTDATEEND("receiptDateEnd"), LINENUMBERFLAG("lineNumberFlag");

    @Getter
    @Setter
    String queryParamName;
}
