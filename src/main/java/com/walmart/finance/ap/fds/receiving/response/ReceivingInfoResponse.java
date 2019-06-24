package com.walmart.finance.ap.fds.receiving.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private String purchaseOrderId;

    private Integer receiptNumber;

    private Integer transactionType;

    private String controlNumber;

    private Integer locationNumber;

    private Integer divisionNumber;

    private LocalDate receiptDate;

    private Character receiptStatus;

    private Integer vendorNumber;

    private String carrierCode;

    private String trailerNumber;

    private Double totalCostAmount;

    private Double totalRetailAmount;

    private Long lineCount;

    private Integer departmentNumber;

    @JsonProperty("receivingLine")
    List<ReceivingInfoLineResponse> receivingInfoLineResponses;
}
