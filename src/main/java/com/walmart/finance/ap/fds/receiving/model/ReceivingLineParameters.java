package com.walmart.finance.ap.fds.receiving.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ReceivingLineParameters {

    ID("_id"),
    RECEIVINGCONTROLNUMBER("receivingControlNumber"),
    RECEIVEID("receiveId"),
    STORENUMBER("storeNumber"),
    BASEDIVISIONNUMBER("baseDivisionNumber"),
    TRANSACTIONTYPE("transactionType"),
    FINALDATE("finalDate"),
    FINALTIME("finalTime"),
    ITEMNUMBER("itemNumber"),
    UPCNUMBER("upcNumber"),
    PURCHASEORDERID("purchaseOrderId"),
    SUMMARYREFERENCE("summaryReference");

    @Getter
    private String parameterName;
}
