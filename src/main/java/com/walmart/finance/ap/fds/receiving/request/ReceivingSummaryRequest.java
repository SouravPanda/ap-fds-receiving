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

    private Meta meta;

}
