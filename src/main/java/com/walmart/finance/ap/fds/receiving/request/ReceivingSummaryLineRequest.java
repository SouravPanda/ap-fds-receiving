package com.walmart.finance.ap.fds.receiving.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@NotNull
public class ReceivingSummaryLineRequest {

    @JsonProperty("receiveId")
    @Valid
    @NotEmpty(message = "Missing mandatory parameter,please enter a valid receiveId")
    private String receiptNumber;

    @Valid
    @NotEmpty(message = "Missing mandatory parameter,please enter a valid purchaseOrderId")
    private String purchaseOrderId;

    @JsonProperty("receiveDate")
    @Valid
    @NotNull(message = "Missing mandatory parameter,please enter a valid receiveDate")
    private LocalDate receiptDate;

    @Valid
    @NotNull(message = "Missing mandatory parameter,please enter a valid locationNumber")
    private Integer locationNumber;

    @Valid
    @NotEmpty(message = "Missing mandatory parameter,please enter a valid businessStatusCode")
    private String businessStatusCode;

    @JsonProperty("lineSequenceNumber")
    private String receiptLineNumber;

    @Valid
    @NotEmpty(message = "Please enter a valid inventoryMatchStatus")
    private String inventoryMatchStatus;

    @Valid
    @NotNull(message = "Missing mandatory parameter,please enter a valid meta")
    private Meta meta;

    private String _id;

    private String partitionKey;
}
