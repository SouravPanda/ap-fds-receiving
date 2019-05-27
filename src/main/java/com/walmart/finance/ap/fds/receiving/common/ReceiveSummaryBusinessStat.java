package com.walmart.finance.ap.fds.receiving.common;

public enum ReceiveSummaryBusinessStat {

    A("ACTIVE"),
    C("CANCEL"),
    D("DELETE"),
    I("INACTIVE"),
    M("MOVE"),
    X("P1A REMOVE"),
    Z("US STORE RECEIVING VISIBILITY");

    private String value;

    private ReceiveSummaryBusinessStat(String value)
    {
        this.value = value;
    }
};

