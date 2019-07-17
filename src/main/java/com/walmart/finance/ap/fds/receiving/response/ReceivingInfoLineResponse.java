package com.walmart.finance.ap.fds.receiving.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Response Structure for Receiving Info
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ReceivingInfoLineResponse {
    private Integer receiptNumber;
    private Integer receiptLineNumber;
    private Integer itemNumber;
    private Integer vendorNumber;
    private Integer quantity;
    private Double eachCostAmount;
    private Double eachRetailAmount;
    private Integer packQuantity;
    private Integer numberofCasesReceived;
    private String purchaseOrderId;
    private String unitOfMeasure;
    private String variableWeightInd;
    private String receivedWeightQuantity;
    private Integer transactionType;
    private Integer locationNumber;
    private Integer divisionNumber;
    private String purchaseOrderNumber;
    private String controlNumber;
    private Double bottleDepositAmount;
    private List<ReceiveMDSResponse> merchandises;


}
