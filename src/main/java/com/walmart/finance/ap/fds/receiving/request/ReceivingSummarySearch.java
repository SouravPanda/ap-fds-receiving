package com.walmart.finance.ap.fds.receiving.request;

import com.sun.istack.internal.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
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

    @NotNull
    @NotEmpty
    Integer countryCode;

    String[] itemNumbers;//to be checked

    String[] upcNumbers;//to be checked, y multiple

    LocalDateTime receiptDateStart;

    LocalDateTime receiptDateEnd;
}
