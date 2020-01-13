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
    private String receiptNumber;

    private Integer receiptLineNumber;

    private Long itemNumber;

    private Integer quantity;

    private Double eachCostAmount;

    private Double eachRetailAmount;

    private Double numberOfCasesReceived;

    private Integer packQuantity;

    private String bottleDepositFlag;

    private String upc;

    private String itemDescription;

    private String unitOfMeasure;

    private String variableWeightInd;

    private Integer costMultiple;

    private String receivedWeightQuantity;

    private List<ReceiveMDSResponse> merchandises;

    private Double eachVendorCostAmount;

    private Double eachVendorRetailAmount;

    private Integer vendorPackQuantity;
}
