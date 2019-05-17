package com.walmart.finance.ap.fds.receiving.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ReceivingSummaryRequest {


    //TODO need to check the json property names
    @NotEmpty
    private String receivingControlNumber;

    @NotEmpty
    private Integer poReceiveId;

    @NotEmpty
    private Integer storeNumber;

    @NotEmpty
    private Integer baseDivisionNumber;

    @NotEmpty
    private Integer transactionType;

    @NotEmpty
    private LocalDate finalDate;

    @NotEmpty
    private LocalTime finalTime;

    @NotEmpty
    private Integer controlType;

    @NotEmpty
    private Integer poId;

    @NotEmpty
    private Integer vendorNumber;

    @NotEmpty
    private Integer accountNumber;

    private Integer controlSequenceNumber;

    private Integer receiveSequenceNumber;

    @NotEmpty
    private char matchIndicator;

    @NotEmpty
    private Double totalCostAmount;

    @NotEmpty
    private Double totalRetailAmount;

    @NotEmpty
    private Integer freightBillId;

    @NotEmpty
    private Character businessStatusCode;

    @NotEmpty
    private Long freightBillExpandID;

    private Character claimPendingIndicator;

    private Character freeAstrayIndicator;

    private Character freightConslIndicator;

    @NotEmpty
    private LocalDateTime initialReceiveTimestamp;

    private LocalDate mdsReceiveDate;

    private LocalDate receiveProcessDate;

    private Double receiveWeightQuantity;

    @NotEmpty
    private String sequenceNumber;

    @NotEmpty
    private Integer departmentNumber;

    private Integer casesReceived;

    @NotEmpty
    private LocalDateTime finalizedLoadTimestamp;

    @NotEmpty
    private Integer finalizedSequenceNumber;

    @NotEmpty
    private String userId;



}
