package com.walmart.finance.ap.fds.receiving.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ReceivingLineParameters {

    ID("_id"),
    RECEIVINGCONTROLNUMBER("receivingControlNumber"),
    CONTROLNUMBER("controlNumber"),
    RECEIVEID("receiveId"),
    STORENUMBER("storeNumber"),
    LOCATIONNUMBER("locationNumber"),
    BASEDIVISIONNUMBER("baseDivisionNumber"),
    DIVISIONNUMBER("divisionNumber"),
    TRANSACTIONTYPE("transactionType"),
    ITEMNUMBER("itemNumber"),
    UPCNUMBER("upcNumber"),
    PURCHASEORDERID("purchaseOrderId"),
    INVENTORYMATCHSTATUS("inventoryMatchStatus"),
    SUMMARYREFERENCE("summaryReference"),
    ORDERBY("orderBy"),
    ORDER("order"),
    PAGENBR("pageNbr"),
    PAGESIZE("pageSize"),
    DATASYNCSTATUS("dataSyncStatus"),
    RECEIPTNUMBERS("receiptNumbers"),
    PURCHASEORDERNUMBER("purchaseOrderNumber"),
    LASTUPDATEDDATE("lastUpdatedDate");


    @Getter
    private String parameterName;

}
