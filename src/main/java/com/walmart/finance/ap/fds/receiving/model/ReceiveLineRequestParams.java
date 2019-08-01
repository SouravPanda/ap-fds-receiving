package com.walmart.finance.ap.fds.receiving.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ReceiveLineRequestParams {

    RECEIPTNUMBER("receiptNumber"),
    CONTROLNUMBER("controlNumber"),
    LOCATIONNUMBER("locationNumber"),
    DIVISIONNUMBER("divisionNumber"),
    TRANSACTIONTYPE("transactionType"),
    PURCHASEORDERID("purchaseOrderId");


    @Getter
    private String parameterName;
}
