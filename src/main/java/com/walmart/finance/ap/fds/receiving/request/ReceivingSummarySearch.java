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

    String receiptNumber;

    Integer transactionType;

    String controlNumber;

    Integer locationNumber;

    Integer divisionNumber;

    Integer vendorNumber;

    Integer departmentNumber;

    Long invoiceId;

    String invoiceNumber;

    Integer countryCode;

    /*String[] itemNumbers;//to be checked

    String[] upcNumbers;//to be checked, y multiple*/

    LocalDateTime receiptDateStart;

    LocalDateTime receiptDateEnd;
}
