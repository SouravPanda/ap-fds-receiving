package com.walmart.finance.ap.fds.receiving.integrations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FinancialTxnResponseData {

    @JsonProperty("transactionId")
    private Integer transactionId;

    @JsonProperty("purchaseOrderId")
    private Integer purchaseOrderId;

    @JsonProperty("receiverNbr")
    private String receiveId;

    @JsonProperty("storeNbr")
    private Integer storeNumber;

    @JsonProperty("vendorNbr")
    private Integer vendorNumber;

    @JsonProperty("divNbr")
    private Integer divisionNumber;

    @JsonProperty("txnCostAmt")
    private Double totalCostAmount;

    @JsonProperty("vendorDeptNbr")
    private Integer departmentNumber;

    @JsonProperty("poNbr")
    private String poNumber;

    @JsonProperty("statusUserId")
    private String authorizedBy;

    @JsonProperty("processStatusTimestamp")
    private Date authorizedDate;

    @JsonProperty("txnVendorName")
    private String vendorName;

    @JsonProperty("parentReceivingNbr")
    private String parentReceivingNbr;

    @JsonProperty("invoiceId")
    private Integer invoiceId;

    @JsonProperty("invoiceNbr")
    private String invoiceNumber;

    @JsonProperty("memo")
    private String memo;

    @JsonProperty("parentReceivingStoreNbr")
    private Integer parentReceivingStoreNbr;

    @JsonProperty("parentReceivingDate")
    private Date parentReceivingDate;

    @JsonProperty("parentPurchaseOrderId")
    private String parentPurchaseOrderId;

    @JsonProperty("receivingDate")
    private Date receivingDate;
}
