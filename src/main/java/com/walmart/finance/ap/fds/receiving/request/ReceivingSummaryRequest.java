package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PropertySource("application.properties")
public class ReceivingSummaryRequest {

    @NotEmpty(message = "Please enter a valid receiptNumber")
    private String receiptNumber;

    @NotEmpty(message = "Please enter a valid controlNumber")
    private String controlNumber;

    @NotNull(message = "Please enter a valid receiptDate")
    private LocalDate receiptDate;

    @NotNull(message = "Please enter a valid locationNumber")
    private Integer locationNumber;

    @NotEmpty(message = "Please enter a valid businessStatusCode")
    private String businessStatusCode;

    private Meta meta;

}
