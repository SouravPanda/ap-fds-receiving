package com.walmart.finance.ap.fds.receiving.config;

import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;

import java.util.Comparator;

public class ReceivingLineComparator implements Comparator<ReceivingLine> {

    @Override
    public int compare(ReceivingLine receivingLine1, ReceivingLine receivingLine2) {

        if (receivingLine1.getLastUpdatedTimestamp().isBefore(receivingLine2.getLastUpdatedTimestamp())) {
            return -1;
        } else if (receivingLine1.getLastUpdatedTimestamp().isAfter(receivingLine2.getLastUpdatedTimestamp())) {
            return 1;
        }
        return 0;
    }
}
