package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ReceivingSummaryLineRequest {

    @NotEmpty(message = "Please enter a valid receiptNumber")
    private String receiptNumber;

    @NotEmpty(message = "Please enter a valid controlNumber")
    private String controlNumber;

    @NotNull(message = "receiptDate cannot be null")
    private LocalDate receiptDate;

    @NotNull(message = "locationNumber cannot be null")
    private Integer locationNumber;

    @NotEmpty(message = "Please enter a valid businessStatusCode")
    private String businessStatusCode;

    Integer sequenceNumber;

    @NotEmpty(message = "inventoryMatchStatus cannot be null")
    private String inventoryMatchStatus;

    private Meta meta;

}
