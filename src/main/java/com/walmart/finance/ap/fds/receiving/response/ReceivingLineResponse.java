package com.walmart.finance.ap.fds.receiving.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ReceivingLineResponse {
    private Integer receiptNumber;
    private Integer receiptLineNumber;
    private Integer itemNumber;
    private Integer vendorNumber;
    private Integer quantity;
    private Double eachCostAmount;
    private Double eachRetailAmount;
    private Integer packQuantity;
    private Integer numberofCasesReceived;
    private Integer vendorStockNumber;
    private Integer bottleDepositAmount;
    private String damaged;
    private Integer purchaseOrderNumber;
    private Integer parentReceiptNumber;
    private Integer purchasedOrderId;
    private Integer upc;
    private String itemDescription;
    private String unitOfMeasure;
    private String variableWeightInd;
    private String receivedWeightQuantity;
    private Integer transactionType;
    private Integer controlNumber;
    private Integer locationNumber;
    private Integer divisionNumber;

}
