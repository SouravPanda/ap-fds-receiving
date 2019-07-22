package com.walmart.finance.ap.fds.receiving.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ReceivingLineResponse {
    private Long receiptNumber;
    private Integer receiptLineNumber;
    private Integer itemNumber;
    private Integer vendorNumber;
    private Integer quantity;
    private Double eachCostAmount;
    private Double eachRetailAmount;
    private Integer packQuantity;
    private Integer numberofCasesReceived;
//    private Integer vendorStockNumber;
//    private Integer bottleDepositAmount;
//    private String damaged;
    private String purchaseOrderNumber;
//    private Integer parentReceiptNumber;
    private String purchaseOrderId;
//    private String upc;
//    private String itemDescription;
    private String unitOfMeasure;
    private String variableWeightInd;
    private String receivedWeightQuantity;
    private Integer transactionType;
    private String controlNumber;
    private Integer locationNumber;
    private Integer divisionNumber;
    private Double bottleDepositAmount;
    private List<ReceiveMDSResponse> merchandises;
}
