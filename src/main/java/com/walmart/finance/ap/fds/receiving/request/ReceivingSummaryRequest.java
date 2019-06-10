package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceivingSummaryRequest {

    @Size(max = 10, min = 1)
    @NotEmpty(message = "Please enter a valid receiptNumber")
    String receiptNumber;

    @Size(max = 10, min = 1)
    @NotEmpty(message = "Please enter a valid controlNumber")
    String controlNumber;

    @NotNull(message = "receiptDate cannot be null")
    private LocalDate receiptDate;

    @NotNull(message = "locationNumber cannot be null")
    Integer locationNumber;

    @Size(max = 1, min = 1)
    @NotEmpty(message = "Please enter a valid businessStatusCode")
    String businessStatusCode;


    Meta meta;


}
