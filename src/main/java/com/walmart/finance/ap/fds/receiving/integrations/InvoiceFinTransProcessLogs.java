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
public class InvoiceFinTransProcessLogs {

    private String actionIndicator;

    private String memoComments;

    private Integer processStatusCode;

    private Date processStatusTimestamp;

    private String statusUserId;
}
