package com.walmart.finance.ap.fds.receiving.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Response Structure for Receiving Info
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReceivingInfoResponse {

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

    @JsonProperty("receivingLine")
    List<ReceivingInfoLineResponse> receivingInfoLineResponses;
}
