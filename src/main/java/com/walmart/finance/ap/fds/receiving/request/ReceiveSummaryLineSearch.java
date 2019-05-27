package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveSummaryLineSearch {

    Long purchaseOrderId;

    Long receiptNumber;

    Integer transactionType;

    String controlNumber;

    Integer locationNumber;

    Integer divisionNumber;

    LocalDate finalDate;

    LocalTime finalTime;

    Integer controlType;

    Integer vendorNumber;

    Integer accountNumber;

    Integer controlSequenceNumber;

    Integer receiveSequenceNumber;

    Double totalCostAmount;

    Double totalRetailAmount;

    Integer freightBillId;

    Character businessStatusCode;

    Long freightBillExpandID;

    Character claimPendingIndicator;

    Character freeAstrayIndicator;

    Character freightConslIndicator;

  //  LocalDateTime initialReceiveTimestamp;

    LocalDate MDSReceiveDate;

  //  LocalDate receiveProcessDate;

    Double receiveWeightQuantity;

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


}
