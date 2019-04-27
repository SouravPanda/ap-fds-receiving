package com.walmart.store.receive.Response;

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
public class ReceivingLineResponse {
    private int receiptNumber;
    private Integer receiptLineNumber;
    private Integer itemNumber;
    private Integer vendorNumber;
    private Integer quantity;
    private Double eachCostAmount;
    private Double eachRetailAmount;
    private Double packQuantity;
    private int numberofCasesReceived;
    private Integer vendorStockNumber;
    private Integer bottleStockNumber;
    private int damaged;
    private Integer purchaseOrderNumber;
    private Integer purchaseReceiptNumber;
    private Integer purchasedOrderId;
    private Integer upc;
    private String itemDescription;
    private Double unitOfMeasure;
    private Double variableWeightInd;
    private Integer receivedWeightQuantity;
    private char transactionType;
    private Integer controlNumber;
    private Integer locationNumber;
    private Integer divisionNumber;
    private String finalDate;
    private String finalTimestamp;
    private Integer sequenceNumber;

}
