package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ReceivingLineRequest {
    private Integer purchaseOrderReceiveID;
    private Integer lineNumber;
    private Integer itemNumber;
    private Integer vendorNumber;
    private Integer receivedQuantity;
    private Double costAmount;
    private Double retailAmount;
    private Double packQuantity;
    private int numberOfCasesReceived;
    private Integer vendorStockNumber;
    private Integer bottleStockNumber;
    private int damaged;
    private Integer receivingControlNumber;
    private Integer purchaseReceiptNumber;
    private Integer upcNumber;
    private String itemDescription;
    private Double unitOfMeasure;
    private Double variableWeightInd;
    private Integer receivedWeightQuantity;
    private Integer transactionType;
    private Integer storeNumber;
    private Integer baseDivisionNumber;
    private Date finalDate;
    private Timestamp finalTime;
    private Integer sequenceNumber;

}
