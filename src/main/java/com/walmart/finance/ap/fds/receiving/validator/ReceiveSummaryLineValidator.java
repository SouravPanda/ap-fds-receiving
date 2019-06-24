package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.common.ReceiveSummaryBusinessStat;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ReceiveSummaryLineValidator {
    public static final Logger log = LoggerFactory.getLogger(ReceiveSummaryLineValidator.class);


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
        try {
            if (receivingSummaryLineRequest.getInventoryMatchStatus().contains(".")) {
                return false;
            }
            Integer inv_match_status = Integer.parseInt(receivingSummaryLineRequest.getInventoryMatchStatus());
            if (inv_match_status >= 0 && inv_match_status <= 9) {
                return true;
            }

        } catch (NumberFormatException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }
}



