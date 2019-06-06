package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ReceivingSummaryLineRequest {
    Long purchaseOrderId;

    @NotNull
    @NotEmpty
    Long receiptNumber;

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

    @NotNull
    @NotEmpty
    LocalDate finalDate;

    @NotNull
    @NotEmpty
    LocalTime finalTime;

    Integer controlType;

    Integer vendorNumber;

    Integer accountNumber;

    Integer controlSequenceNumber;

    Integer receiveSequenceNumber;

    Double totalCostAmount;

    Double totalRetailAmount;

    Integer freightBillId;

    String businessStatusCode;

    Long freightBillExpandID;

    Character claimPendingIndicator;

    Character freeAstrayIndicator;

    Character freightConslIndicator;

    //  LocalDateTime initialReceiveTimestamp;

    LocalDate MDSReceiveDate;

    //  LocalDate receiveProcessDate;

    Double receiveWeightQuantity;

    @NotNull
    @NotEmpty
    Integer sequenceNumber;

    Integer departmentNumber;

    Integer casesReceived;

    LocalDateTime finalizedLoadTimestamp;

    Integer finalizedSequenceNumber;

    String poReceiveId;//  receiptNumber //Long

    String userId;

    // LocalDateTime creationDate;

    String purchaseOrderNumber;


    Double receivedWeightQuantity;

    String receivedQuantityUnitOfMeasureCode;

    LocalDateTime receiptDateStart;

    LocalDateTime receiptDateEnd;

    String receivingControlNumber;//purchaseOrderId //Long

    Integer storeNumber;//locationNumber

    Integer baseDivisionNumber;

    String purchaseOrderReceiveID;

    Integer lineNumber;

    Integer itemNumber;

    Integer receivedQuantity;

    Double costAmount;

    Double retailAmount;

    Integer purchaseReceiptNumber;

    Integer purchasedOrderId;

    Integer upcNumber;

    @NotEmpty
    @NotNull
    Integer inventoryMatchStatus;

    Meta meta;

}
