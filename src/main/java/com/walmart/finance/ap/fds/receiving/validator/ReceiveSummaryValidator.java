package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.common.ReceiveSummaryBusinessStat;
import com.walmart.finance.ap.fds.receiving.exception.InvalidValueException;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class ReceiveSummaryValidator {
    List<ReceiveSummaryBusinessStat> businessStatList = Arrays.asList(ReceiveSummaryBusinessStat.values());

    public boolean validateBusinessStatUpdateSummary(ReceivingSummaryRequest receivingSummaryRequest) {
        for (ReceiveSummaryBusinessStat businessStat : businessStatList) {
            if (businessStat.toString().equals(receivingSummaryRequest.getBusinessStatusCode())) {
                return true;
            }
        }
        return false;

    }

}



