package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ReceivingSummaryLineRequest {

    @Valid
    @NotEmpty(message = "{validation.receiptNumber.notEmpty}")
    private String receiptNumber;

    @Valid
    @NotEmpty(message = "{validation.controlNumber.notEmpty}")
    private String controlNumber;

    @Valid
    @NotNull(message = "{validation.receiptDate.notEmpty}")
    private LocalDate receiptDate;

    @Valid
    @NotNull(message = "{validation.locationNumber.notEmpty}")
    private Integer locationNumber;

    @Valid
    @NotEmpty(message = "{validation.businessStatusCode.notEmpty}")
    private String businessStatusCode;

    Integer sequenceNumber;

    @Valid
    @NotEmpty(message = "{validation.inventoryMatchStatus.notEmpty}")
    private String inventoryMatchStatus;

    private Meta meta;

}
