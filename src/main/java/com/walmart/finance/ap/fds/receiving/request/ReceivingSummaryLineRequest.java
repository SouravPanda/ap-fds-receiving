package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

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
    @Range(min = 1,max = 1)
    String businessStatusCode;


    @NotNull
    @NotEmpty
    Integer sequenceNumber;


    @NotEmpty
    @NotNull
    Integer inventoryMatchStatus;

    Meta meta;

}
