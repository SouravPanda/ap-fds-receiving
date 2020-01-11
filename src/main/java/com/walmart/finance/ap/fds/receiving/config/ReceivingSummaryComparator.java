package com.walmart.finance.ap.fds.receiving.config;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;

import java.util.Comparator;

public class ReceivingSummaryComparator implements Comparator<ReceiveSummary> {

    @Override
    public int compare(ReceiveSummary receiveSummary1, ReceiveSummary receiveSummary2) {
        if(receiveSummary1.getDateReceived().isBefore(receiveSummary2.getDateReceived())) {
            return -1;
        } else if (receiveSummary1.getDateReceived().isAfter(receiveSummary2.getDateReceived())) {
            return 1;
        }
        return 0;
    }
}
