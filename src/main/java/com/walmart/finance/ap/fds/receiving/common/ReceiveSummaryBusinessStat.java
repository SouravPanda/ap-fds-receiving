package com.walmart.finance.ap.fds.receiving.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ReceiveSummaryBusinessStat {

    A("ACTIVE"),
    C("CANCEL"),
    D("DELETE"),
    I("INACTIVE"),
    M("MOVE"),
    X("P1A REMOVE"),
    Z("US STORE RECEIVING VISIBILITY");

    @Getter
    private String value;
}

