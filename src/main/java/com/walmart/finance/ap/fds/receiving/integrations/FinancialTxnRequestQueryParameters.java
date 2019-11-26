package com.walmart.finance.ap.fds.receiving.integrations;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * For Enum key is Info request query param name
 * value is Fin Txn Requesr query param name associated with the info request query param name
 */
@AllArgsConstructor
public enum FinancialTxnRequestQueryParameters {

    DIVISIONNUMBER("divNbr"),
    INVOICENUMBER("invoiceNumber"),
    DEPARTMENTNUMBER("deptNbr"),
    PURCHASEORDERID("purchaseOrderId"),
    RECEIPTNUMBERS("receiverNbr"),
    LOCATIONNUMBER("origStoreNbr"),
    CONTROLNUMBER("txnControlNbr"),
    TRANSACTIONTYPE("txnTypeCode"),
    VENDORNUMBER("vendorNumber"),
    INVOICEID("invoiceId"),
    RECEIPTDATESTART("txnStartDate"),
    RECEIPTDATEEND("txnEndDate"),
    PURCHASEORDERNUMBER("poNumber");

    @Getter
    private String finTxnRequestQueryParam;
}
