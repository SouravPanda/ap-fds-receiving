package com.walmart.finance.ap.fds.receiving.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ReceiveLineRequestParams {

    RECEIPTNUMBERS("receiptNumbers"),
    CONTROLNUMBER("controlNumber"),
    LOCATIONNUMBER("locationNumber"),
    DIVISIONNUMBER("divisionNumber"),
    ORDERBY("orderBy"),
    ORDER("order"),
    PAGENBR("pageNbr"),
    PAGESIZE("pageSize"),
    TRANSACTIONTYPE("transactionType"),
    PURCHASEORDERNUMBER("purchaseOrderNumber"),
    PURCHASEORDERID("purchaseOrderId"),
    ITEMNUMBERS("itemNumbers"),
    UPCNUMBERS("upcNumbers"),
    VENDORNUMBER("vendorNumber"),
    DEPARTMENTNUMBER("departmentNumber"),
    INVOICEID("invoiceId"),
    INVOICENUMBER("invoiceNumber"),
    RECEIPTDATESTART("receiptDateStart"),
    RECEIPTDATEEND("receiptDateEnd");

    @Getter
    private String parameterName;
}
