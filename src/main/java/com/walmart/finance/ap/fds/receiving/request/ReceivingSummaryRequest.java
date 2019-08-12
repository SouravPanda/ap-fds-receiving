package com.walmart.finance.ap.fds.receiving.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.PropertySource;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PropertySource("application.properties")
@NotNull
public class ReceivingSummaryRequest {

    @JsonProperty("receiveId")
    @NotEmpty(message = "Missing mandatory parameter,please enter a valid receiveId")
    private String receiptNumber;

    @NotEmpty(message = "Missing mandatory parameter,please enter a valid purchaseOrderId")
    private String purchaseOrderId;

    @JsonProperty("receiveDate")
    @NotNull(message = "Missing mandatory parameter,please enter a valid receiveDate")
    private LocalDate receiptDate;

    @NotNull(message = "Missing mandatory parameter,please enter a valid locationNumber")
    private Integer locationNumber;

    @NotEmpty(message = "Missing mandatory parameter,please enter a valid businessStatusCode")
    private String businessStatusCode;

    @Valid
    @NotNull(message = "Missing mandatory parameter,please enter a valid meta")
    private Meta meta;
}
