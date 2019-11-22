package com.walmart.finance.ap.fds.receiving.integrations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class FinancialTxnResponseData {

    @JsonProperty("transactionId")
    private Long transactionId;

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
    private Long invoiceId;

    @JsonProperty("invoiceNbr")
    private String invoiceNumber;

    @JsonProperty("memo")
    private String memo;

    @JsonProperty("parentReceivingStoreNbr")
    private String parentReceivingStoreNbr;

    @JsonProperty("parentReceivingDate")
    private Date parentReceivingDate;

    @JsonProperty("parentPurchaseOrderId")
    private String parentPurchaseOrderId;

    @JsonProperty("receivingDate")
    private Date receivingDate;

    private Integer accountNbr;

    private Integer apCompanyId;

    private Integer claimNbr;

    private String countryCode;

    private Date createdDate;

    private String dataSyncStatus;

    private Integer deductTypeCode;

    private String deliveryNoteId;

    private Date dueDate;

    private Integer f6ASeqNbr;

    private String grocinvoiceInd;

    private List<InvoiceFinDelNoteChangeLogs> invoiceFinDelNoteChangeLogs;

    private List<InvoiceFinTransAdjustLogs> invoiceFinTransAdjustLogs;

    private List<InvoiceFinTransProcessLogs> invoiceFinTransProcessLogs;

    private Date matchDate;

    private Integer offsetAccountNbr;

    private Integer origDivNbr;

    private Integer origStoreNbr;

    private Integer poDcNbr;

    private Integer poDeptNbr;

    private Integer poTypeCode;

    private Date postDate;

    private Integer processStatusCode;

    private String receivingType;

    private String sourceInfo;

    private Date transactionDate;

    private String transactionNbr;

    private Double txnAllowanceAmt;

    private Integer txnBatchNbr;

    private String txnControlNbr;

    private Double txnDiscountAmt;

    private Double txnRetailAmt;

    private Integer txnSeqNbr;

    private Integer txnTypeCode;

    private String txnTypeDesc;

    private Date updateDate;
}
