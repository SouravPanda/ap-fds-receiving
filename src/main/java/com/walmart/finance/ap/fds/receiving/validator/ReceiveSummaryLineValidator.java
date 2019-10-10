package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.exception.InvalidValueException;
import com.walmart.finance.ap.fds.receiving.exception.ReceivingErrors;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReceiveSummaryLineValidator {
    public static final Logger log = LoggerFactory.getLogger(ReceiveSummaryLineValidator.class);

    public void validateInventoryMatchStatus(ReceivingSummaryLineRequest receivingSummaryLineRequest) {
        try {
            Integer invMatchStatus = Integer.valueOf(receivingSummaryLineRequest.getInventoryMatchStatus());
            if (!(invMatchStatus >= 0 && invMatchStatus <= 9)) {
                throw new InvalidValueException(ReceivingErrors.INVALIDINVENTORYMATCHSTATUSCODE.getParameterName(), ReceivingErrors.INVALIDINVENTORYMATCHSTATUSDETAILS.getParameterName());
            }
        } catch (NumberFormatException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new InvalidValueException(ReceivingErrors.INVALIDINVENTORYMATCHSTATUSCODE.getParameterName(), ReceivingErrors.INVALIDINVENTORYMATCHSTATUSDETAILS.getParameterName());
        }
    }

    public void validateReceiptLineNumber(String receiptLineNumber) {
        try {
            if (StringUtils.isNotEmpty(receiptLineNumber)) {
                Integer.valueOf(receiptLineNumber);
            }
        } catch (NumberFormatException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new InvalidValueException(ReceivingErrors.INVALIDLINESEQUENCE.getParameterName(), ReceivingErrors.INVALIDLINESEQUENCEDETAILS.getParameterName());
        }
    }
}



