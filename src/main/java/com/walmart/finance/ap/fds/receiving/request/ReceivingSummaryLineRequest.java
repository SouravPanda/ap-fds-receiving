package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ReceivingSummaryLineRequest {

    //Key2
    @NotNull
    @NotEmpty
    String receiptNumber;

    // Key1
    @NotNull
    @NotEmpty
    String controlNumber;

    //Key3
    @NotNull
    @NotEmpty
    private LocalDate receiptDate;

    //key4
    @NotNull
    @NotEmpty
    Integer locationNumber;

    @NotEmpty
    @NotNull
    String businessStatusCode;

    Character typeIndicator;

    String writeIndicator;

    @NotNull
    @NotEmpty
    Integer sequenceNumber;


    @NotEmpty
    @NotNull
    Integer inventoryMatchStatus;

    Meta meta;

}
