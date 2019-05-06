package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveLineSearch {
    Long purchaseOrderId;
    Long receiptNumber;
    Integer transactionType;
    String controlNumber;
    Integer locationNumber;
    Integer divisionNumber;
    Integer countryCode;
}
