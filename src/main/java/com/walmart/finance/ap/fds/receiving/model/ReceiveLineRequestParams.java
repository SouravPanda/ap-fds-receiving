package com.walmart.finance.ap.fds.receiving.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ReceiveLineRequestParams {

    RECEIPTNUMBERS("receiptNumbers"),
    CONTROLNUMBER("controlNumber"),
    LOCATIONNUMBER("locationNumber"),
    DIVISIONNUMBER("divisionNumber"),
    TRANSACTIONTYPE("transactionType"),
    PURCHASEORDERID("purchaseOrderId");


    @Getter
    private String parameterName;
}
