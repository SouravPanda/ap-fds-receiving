package com.walmart.finance.ap.fds.receiving.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public enum ReceivingLineParameters {

    ID("_id"),
    RECEIVINGCONTROLNUMBER("receivingControlNumber"),
    PORECEIVEID("purchaseOrderReceiveID")  ,
    STORENUMBER("storeNumber"),
    BASEDIVISIONNUMBER("baseDivisionNumber"),
    TRANSACTIONTYPE("transactionType"),
    FINALDATE("finalDate"),
    FINALTIME("finalTime")
    ;

    @Getter
    private String parameterName;

}
