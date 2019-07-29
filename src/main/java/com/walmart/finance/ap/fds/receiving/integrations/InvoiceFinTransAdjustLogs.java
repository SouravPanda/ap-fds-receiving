package com.walmart.finance.ap.fds.receiving.integrations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InvoiceFinTransAdjustLogs {
    private Integer adjustmentNbr;

    private Double costAdjustAmt;

    private Date createTs;

    private String createUserId;

    private Date dueDate;

    private Double origTxnCostAmt;

    private Date postDate;

    private Date transactionDate;
}
