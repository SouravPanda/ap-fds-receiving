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
public class InvoiceFinTransProcessLogs {

    private String actionIndicator;

    private String memoComments;

    private Integer processStatusCode;

    private LocalDate processStatusTimestamp;

    private String statusUserId;
}
