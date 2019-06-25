package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.PropertySource;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@PropertySource("application.properties")
public class ReceivingSummaryLineRequest {

    @Valid
    @NotEmpty(message = "Missing mandatory parameter,please enter a valid receiptNumber")
    private String receiptNumber;

    @Valid
    @NotEmpty(message = "Missing mandatory parameter,please enter a valid controlNumber")
    private String controlNumber;

    @Valid
    @NotNull(message = "Missing mandatory parameter,please enter a valid receiptDate")
    private LocalDate receiptDate;

    @Valid
    @NotNull(message = "Missing mandatory parameter,please enter a valid locationNumber")
    private Integer locationNumber;

    @Valid
    @NotEmpty(message = "Missing mandatory parameter,please enter a valid businessStatusCode")
    private String businessStatusCode;

    Integer sequenceNumber;

    @Valid
    @NotEmpty(message = "Please enter a valid inventoryMatchStatus")
    private String inventoryMatchStatus;

    private Meta meta;

}
