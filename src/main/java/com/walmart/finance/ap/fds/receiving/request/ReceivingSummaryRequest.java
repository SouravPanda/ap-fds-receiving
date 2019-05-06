package com.walmart.finance.ap.fds.receiving.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ReceivingSummaryRequest {


    //TODO need to check the json property names
    @NotEmpty
    private String receivingControlNumber;
    @NotEmpty
    private Integer storeNumber;
    @NotEmpty
    private Integer transactionType;
/*    @NotEmpty
    private LocalDateTime receiptDateStart;
    @NotEmpty
    private LocalDateTime receiptDateEnd;*/

    @NotEmpty
    private LocalDate finalDate;

    @NotEmpty
    private LocalTime finalTime;

    @NotEmpty
    private Integer controlType;


    @NotEmpty

    private Integer vendorNumber;


    @NotEmpty
    private Integer accountNumber;


    private Integer controlSequenceNumber;


    private Integer receiveSequenceNumber;


    @NotEmpty
    private char matchIndicator;


    private Double totalCostAmount;


    private Double totalRetailAmount;


    private Integer freightBillId;

    private Character businessStatusCode;

    private Long freightBillExpandID;

    private Character claimPendingIndicator;


    private Character freeAstrayIndicator;

    private Character freightConslIndicator;


    private LocalDateTime initialReceiveTimestamp;

    private LocalDate mdsReceiveDate;

    private LocalDate receiveProcessDate;

    private Double receiveWeightQuantity;


    private String sequenceNumber;


    private Integer departmentNumber;

    private Integer casesReceived;


    private LocalDateTime finalizedLoadTimestamp;


    private Integer finalizedSequenceNumber;

    @NotEmpty
    private Integer poReceiveId;


    @NotEmpty
    private Integer baseDivisionNumber;

    @NotEmpty
    private String userId;


}
