package com.walmart.finance.ap.fds.receiving.integrations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FinancialTxnResponseData {

    @JsonProperty("transactionId")
    private Integer transactionId;

    @JsonProperty("purchaseOrderId")
    private Integer receivingControlNumber; //POID

    @JsonProperty("receiverNbr")
    private String poReceiveId;

    @JsonProperty("storeNbr")
    private Integer storeNumber;

    @JsonProperty("vendorNbr")
    private Integer vendorNumber;

    @JsonProperty("divNbr")
    private Integer baseDivisionNumber;

    @JsonProperty("txnCostAmt")
    private Double totalCostAmount;

    @JsonProperty("vendorDeptNbr")
    private Integer departmentNumber;

    @JsonProperty("poNbr")
    private String poNumber;
}
