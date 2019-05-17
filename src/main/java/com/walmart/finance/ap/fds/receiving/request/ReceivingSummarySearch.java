package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceivingSummarySearch {

    Long purchaseOrderNumber;

    Long purchaseOrderId;

    String receiptNumbers;

    Integer transactionType;

    String controlNumber;

    Integer locationNumber;

    Integer divisionNumber;

    Integer vendorNumber;

    Integer departmentNumber;

    Long invoiceId;

    String invoiceNumber;

    LocalDateTime receiptDateStart;

    LocalDateTime receiptDateEnd;
}
