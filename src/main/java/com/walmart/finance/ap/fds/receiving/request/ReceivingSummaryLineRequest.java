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

    @Size(max = 10, min = 1)
    @NotEmpty(message = "Please enter a valid receiptNumber")
    String receiptNumber;

    @Size(max = 10, min = 1)
    @NotEmpty(message = "Please enter a valid controlNumber")
    String controlNumber;

    @NotNull(message = "receiptDate cannot be null")
    LocalDate receiptDate;

    @NotNull(message = "locationNumber cannot be null")
    Integer locationNumber;

    @NotEmpty(message = "Please enter a valid businessStatusCode")
    String businessStatusCode;

    Integer sequenceNumber;

    @NotEmpty(message = "inventoryMatchStatus cannot be null")
    String inventoryMatchStatus;

    Meta meta;

}
