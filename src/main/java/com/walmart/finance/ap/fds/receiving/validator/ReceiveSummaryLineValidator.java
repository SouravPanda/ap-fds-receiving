package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.common.ReceiveSummaryBusinessStat;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ReceiveSummaryLineValidator {


    List<ReceiveSummaryBusinessStat> businessStatList = Arrays.asList(ReceiveSummaryBusinessStat.values());

    public boolean validateBusinessStatUpdateSummary(ReceivingSummaryLineRequest receivingSummaryLineRequest) {
        for (ReceiveSummaryBusinessStat businessStat : businessStatList) {
            if (businessStat.toString().equals(receivingSummaryLineRequest.getBusinessStatusCode())) {
                return true;
            }
        }
        return false;

    }

    public boolean validateInventoryMatchStatus(ReceivingSummaryLineRequest receivingSummaryLineRequest) {
        if (receivingSummaryLineRequest.getInventoryMatchStatus() >= 0 && receivingSummaryLineRequest.getInventoryMatchStatus() <= 9) {
            return true;
        }
        return false;
    }
}



