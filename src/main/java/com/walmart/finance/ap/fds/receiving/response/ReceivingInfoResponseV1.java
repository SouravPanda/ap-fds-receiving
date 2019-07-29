package com.walmart.finance.ap.fds.receiving.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReceivingInfoResponseV1 {

    private String authorizedBy;

    private LocalDate authorizedDate;

    private String carrierCode;

    private String controlNumber;

    private Integer departmentNumber;

    private Integer transactionType;

    private Integer divisionNumber;

    private Long lineCount;

    private Integer locationNumber;

    private Integer purchaseOrderId;

    private LocalDate receiptDate;

    private Long receiptNumber;

    private String receiptStatus;

    private Double totalCostAmount;

    private Double totalRetailAmount;

    private String trailerNumber;

    private Integer vendorNumber;

    private String memo;

    private Double bottleDepositAmount;

    private Integer controlSequenceNumber;

    private String vendorName;

    private String parentReceivingNbr;

    private Integer parentReceivingStoreNbr;

    private LocalDate parentReceivingDate;

    private String parentPurchaseOrderId;

    private Integer invoiceId;

    private String invoiceNumber;

    private Integer transactionId;

    private Integer txnSeqNbr;

    private Integer f6ASeqNbr;

    private String transactionNbr;

    private String countryCode;

    private Integer apCompanyId;

    private Double txnRetailAmt;

    private Double txnCostAmt;

    private Double txnDiscountAmt;

    private Double txnAllowanceAmt;

    private Integer vendorDeptNbr;

    private LocalDate postDate;

    private LocalDate dueDate;

    private String poNbr;

    private LocalDate transactionDate;

    private Integer claimNbr;

    private Integer accountNbr;

    private Integer deductTypeCode;

    private Integer txnBatchNbr;

    private String txnControlNbr;

    private String deliveryNoteId;

    private Integer origStoreNbr;

    private Integer origDivNbr;

    private Integer poDcNbr;

    private Integer poTypeCode;

    private Integer poDeptNbr;

    private Integer offsetAccountNbr;

    private String grocinvoiceInd;

    private LocalDate matchDate;

    private Integer processStatusCode;

    @JsonProperty("invoiceFinTransProcessLogs")
    private List<InvoiceFinTransProcessLogs> invoiceFinTransProcessLogs;

    @JsonProperty("invoiceFinTransAdjustLogs")
    private List<InvoiceFinTransAdjustLogs> invoiceFinTransAdjustLogs;

    @JsonProperty("invoiceFinDelNoteChangeLogs")
    private List<InvoiceFinDelNoteChangeLogs> invoiceFinDelNoteChangeLogs;

    @JsonProperty("receivingLine")
    List<ReceivingInfoLineResponse> receivingInfoLineResponses;
}
