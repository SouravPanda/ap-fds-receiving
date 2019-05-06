package com.walmart.finance.ap.fds.receiving.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ReceivingSummarySearch {

    String purchaseOrderNumber;

    Long purchaseOrderId;

    Long receiptNumber;

    Integer transactionType;

    String controlNumber;

    Integer locationNumber;

    Integer divisionNumber;

    Integer vendorNumber;

    Integer departmentNumber;

    Long invoiceId;

    String invoiceNumber;

    /*String[] itemNumbers;//to be checked

    String[] upcNumbers;//to be checked, y multiple*/

    LocalDateTime receiptDateStart;

    LocalDateTime receiptDateEnd;
}
