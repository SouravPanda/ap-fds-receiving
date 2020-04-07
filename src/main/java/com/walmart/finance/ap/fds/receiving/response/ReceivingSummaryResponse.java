package com.walmart.finance.ap.fds.receiving.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceivingSummaryResponse {

    @NotEmpty
    private String purchaseOrderId;

    @NotEmpty
    private String receiptNumber;

    @NotEmpty
    private Integer transactionType;

    @NotEmpty
    private String controlNumber;

    @NotEmpty
    private Integer locationNumber;

    @NotEmpty
    private Integer divisionNumber;

    @NotEmpty
    private LocalDate receiptDate;

    @NotEmpty
    private Character receiptStatus;

    @NotEmpty
    private Integer vendorNumber;

    @NotEmpty
    private Long freightId;

    @NotEmpty
    private Double totalCostAmount;

    @NotEmpty
    private Double totalRetailAmount;

//    @NotEmpty
//    private Integer parentReceiptId;

//    @NotEmpty
//    private String parentReceiptNumber;

    @NotEmpty
    private Long lineCount;

    @NotEmpty
    private Integer departmentNumber;

//    private Integer supplierNumber;

//    @NotEmpty
//    private String parentPurchaseOrderId;

//    @NotEmpty
//    private  Integer parentTransactionType;

//    @NotEmpty
//    private String parentControlNumber;

//    @NotEmpty
//    private Integer parentLocationNumber;

//    @NotEmpty
//    private Integer parentDivisionNumber;

//    @NotEmpty
//    private String memo;

    private Integer controlSequenceNumber;

    private Double bottleDepositAmount;

}
