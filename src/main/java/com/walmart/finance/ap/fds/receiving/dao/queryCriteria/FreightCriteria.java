package com.walmart.finance.ap.fds.receiving.dao.queryCriteria;


import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;

import java.util.ArrayList;
import java.util.List;

public class FreightCriteria {

    public static List<Long> getFreightCriteria(List<ReceiveSummary> receiveSummaries) {

        List<Long> freightIds = new ArrayList<>();

        receiveSummaries.forEach(receiveSummary-> {
            if (receiveSummary.getFreightBillExpandId() != null) {
                freightIds.add(receiveSummary.getFreightBillExpandId());
                }
        });

        return freightIds;
    }
}
