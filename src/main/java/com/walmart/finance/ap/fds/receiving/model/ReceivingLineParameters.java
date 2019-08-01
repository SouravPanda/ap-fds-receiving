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
    FINALDATE("finalDate"),
    FINALTIME("finalTime"),
    ITEMNUMBER("itemNumber"),
    UPCNUMBER("upcNumber"),
    PURCHASEORDERID("purchaseOrderId"),
    BUSINESSSTATUSCODE("businessStatusCode"),
    INVENTORYMATCHSTATUS("inventoryMatchStatus"),
    SUMMARYREFERENCE("summaryReference"),
    DATASYNCSTATUS("dataSyncStatus"),
    RECEIPTNUMBER("receiptNumber"),
    LASTUPDATEDDATE("lastUpdatedDate");


    @Getter
    private String parameterName;

}
