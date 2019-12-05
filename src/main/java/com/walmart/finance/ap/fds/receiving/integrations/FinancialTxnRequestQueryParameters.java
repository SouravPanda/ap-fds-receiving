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
    INVOICENUMBER("invoiceNbr"),
    DEPARTMENTNUMBER("deptNbr"),
    PURCHASEORDERID("purchaseOrderId"),
    RECEIPTNUMBERS("receiverNbr"),
    LOCATIONNUMBER("origStoreNbr"),
    CONTROLNUMBER("txnControlNbr"),
    TRANSACTIONTYPE("txnTypeCode"),
    VENDORNUMBER("vendorNbr"),
    INVOICEID("invoiceId"),
    RECEIPTDATESTART("txnStartDate"),
    RECEIPTDATEEND("txnEndDate"),
    PURCHASEORDERNUMBER("poNbr"),
    LOCATIONTYPE("locationType");

    @Getter
    private String finTxnRequestQueryParam;
}
