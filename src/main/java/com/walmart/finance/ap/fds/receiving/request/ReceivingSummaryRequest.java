package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceivingSummaryRequest {


    Long purchaseOrderNumber;

    Long purchaseOrderId;

    @NotNull
    @NotEmpty
    String receiptNumbers;

    @NotNull
    @NotEmpty
    Integer transactionType;

    @NotNull
    @NotEmpty
    String controlNumber;

    @NotNull
    @NotEmpty
    Integer locationNumber;

    @NotNull
    @NotEmpty
    Integer divisionNumber;

    Integer vendorNumber;

    Integer departmentNumber;

    Long invoiceId;

    String invoiceNumber;

    @NotNull
    @NotEmpty
    LocalDate finalDate;

    LocalDateTime receiptDateStart;

    @NotNull
    @NotEmpty
    LocalTime finalTime;

    LocalDateTime receiptDateEnd;

    @NotEmpty
    @NotNull
    String businessStatusCode;

    Double costAmount;

    Double retailAmount;

    // LocalDate finalDate;

    // LocalTime finalTime;

    Integer controlType;

    Integer accountNumber;

    Integer controlSequenceNumber;

    Integer receiveSequenceNumber;

    Character matchIndicator;

    Double totalCostAmount;

    Double totalRetailAmount;

    Integer freightBillId;

    // Long freightBillExpandID;

    Character claimPendingIndicator;

    Character freeAstrayIndicator;

    Character freightConslIndicator;

    // LocalDateTime initialReceiveTimestamp;

    Double receiveWeightQuantity;

    Integer sequenceNumber;

    Integer casesReceived;

    // LocalDateTime finalizedLoadTimestamp;

    // Integer finalizedSequenceNumber;

    String userId;

    //  LocalDateTime creationDate;

    Character typeIndicator;

    String writeIndicator;

    Meta meta;


}
