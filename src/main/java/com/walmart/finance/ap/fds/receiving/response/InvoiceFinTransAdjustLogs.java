package com.walmart.finance.ap.fds.receiving.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InvoiceFinTransAdjustLogs {
    private Integer adjustmentNbr;

    private Double costAdjustAmt;

    private LocalDate createTs;

    private String createUserId;

    private LocalDate dueDate;

    private Double origTxnCostAmt;

    private LocalDate postDate;

    private LocalDate transactionDate;
}
