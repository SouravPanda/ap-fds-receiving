package com.walmart.finance.ap.fds.receiving.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ReceiveSummaryCosmosDBParameters {

    ID("_id"),
    RECEIVINGCONTROLNUMBER("receivingControlNumber"),
    STORENUMBER("storeNumber"),
    BASEDIVISIONNUMBER("baseDivisionNumber"),
    TRANSACTIONTYPE("transactionType"),
    FINALDATE("finalDate"),
    FINALTIME("finalTime"),
    CONTROLTYPE("controlType"),
    VENDORNUMBER("vendorNumber"),
    ACCOUNTNUMBER("accountNumber"),
    CONTROLSEQUENCENUMBER("controlSequenceNumber"),
    RECEIVESEQUENCENUMBER("receiveSequenceNumber"),
    MATCHINDICATOR("matchIndicator"),
    TOTALCOSTAMOUNT("totalCostAmount"),
    TOTALRETAILAMOUNT("totalRetailAmount"),
    BUSINESSSTATUSCODE("businessStatusCode"),
    FREIGHTBILLEXPANDID("freightBillExpandID"),
    CLAIMPENDINGINDICATOR("claimPendingIndicator"),
    FREEASTRAYINDICATOR("freeAstrayIndicator"),
    FREIGHTCONSLINDICATOR("freightConslIndicator"),
    RECEIVETIMESTAMP("receiveTimestamp"),
    RECEIVINGDATE("receivingDate"),
    RECEIVEPROCESSDATE("receiveProcessDate"),
    RECEIVEWEIGHTQUANTITY("receiveWeightQuantity"),
    SEQUENCENUMBER("sequenceNumber"),
    DEPARTMENTNUMBER("departmentNumber"),
    CASESRECEIVED("casesReceived"),
    FINALIZEDLOADTIMESTAMP("finalizedLoadTimestamp"),
    FINALIZEDSEQUENCENUMBER("finalizedSequenceNumber"),
    RECEIVEID("receiveId"),
    USERID("userId"),
    CREATIONDATE("creationDate"),
    PURCHASEORDERNUMBER("poNumber"),
    TYPEINDICATOR("typeIndicator"),
    WRITEINDICATOR("writeIndicator"),
    PURCHASEORDERID("purchaseOrderId");

    @Getter
    private String parameterName;
}
