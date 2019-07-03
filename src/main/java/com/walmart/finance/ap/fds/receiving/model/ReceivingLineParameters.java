package com.walmart.finance.ap.fds.receiving.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ReceivingLineParameters {

    ID("_id"),
    RECEIVINGCONTROLNUMBER("receivingControlNumber"),
    PORECEIVEID("purchaseOrderReceiveID")  ,
    STORENUMBER("storeNumber"),
    BASEDIVISIONNUMBER("baseDivisionNumber"),
    TRANSACTIONTYPE("transactionType"),
    FINALDATE("finalDate"),
    FINALTIME("finalTime"),
    ITEMNUMBER("itemNumber"),
    UPCNUMBER("upcNumber")
    ;

    @Getter
    private String parameterName;

}
