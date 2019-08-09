package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.common.ReceiveSummaryBusinessStat;
import com.walmart.finance.ap.fds.receiving.exception.InvalidValueException;
import com.walmart.finance.ap.fds.receiving.request.SorRoutingCtx;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReceiveSummaryValidator {
    public static final Logger log = LoggerFactory.getLogger(ReceiveSummaryValidator.class);

    public void validateBusinessStatUpdateSummary(String businessStatusCode) {
        try {
            ReceiveSummaryBusinessStat.valueOf(businessStatusCode);
        } catch (IllegalArgumentException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new InvalidValueException("Value of field  businessStatusCode passed is not valid", "it should be one among A,C,D,I,M,X,Z");
        }
    }

    /**
     * This all are mandatory parameters so null checks are present in request itself.
     */
    //TODO : Put this hardcode value in constant or enum.
    public Boolean isWareHouseData(SorRoutingCtx sorRoutingCtx) {
        if ((sorRoutingCtx.getLocationCountryCd().equals("US"))
                && (sorRoutingCtx.getReplnTypCd().equals("R") || sorRoutingCtx.getReplnTypCd().equals("U") || sorRoutingCtx.getReplnTypCd().equals("F"))
                && (sorRoutingCtx.getInvProcAreaCode() == 36 || sorRoutingCtx.getInvProcAreaCode() == 30)) {
            return true;
        }
        return false;
    }
}



