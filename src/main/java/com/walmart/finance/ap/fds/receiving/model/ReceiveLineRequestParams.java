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
    PURCHASEORDERID("purchaseOrderId");

    @Getter
    private String parameterName;
}
