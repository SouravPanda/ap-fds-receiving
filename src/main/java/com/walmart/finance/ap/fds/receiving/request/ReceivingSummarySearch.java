package com.walmart.finance.ap.fds.receiving.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceivingSummarySearch {

  //  @NotNull(message = "PurchaseOrderNumber is mandatory and should be passed in the payload")
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
    LocalDateTime receiptDateStart;

    @NotNull
    @NotEmpty
    LocalDateTime receiptDateEnd;

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

}
