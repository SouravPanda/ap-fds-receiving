package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.exception.ReceivingErrors;
import com.walmart.finance.ap.fds.receiving.model.ReceiveLineRequestParams;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLineParameters;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.Map;

public class ReceiveLineValidator {

    public static void validate(String countryCode, Map<String, String> allRequestParams) {
        Iterator<Map.Entry<String, String>> iterator = allRequestParams.entrySet().iterator();
        while (iterator.hasNext()) {
            // If invalid query param has been passed in request then remove it.
            try {
                Map.Entry<String, String> entry = iterator.next();
                ReceiveLineRequestParams.valueOf(entry.getKey().toUpperCase());
                if (StringUtils.isEmpty(entry.getValue())) {
                    iterator.remove();
                }
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException(ReceivingErrors.JUNKPARAMS.getParameterName(), ReceivingErrors.INVALIDQUERYPARAMS.getParameterName());
            }
        }
        if (allRequestParams.containsKey(ReceivingLineParameters.PURCHASEORDERID.getParameterName())) {
            allRequestParams.put(ReceivingConstants.PURCHASEORDERID, allRequestParams.get(ReceiveLineRequestParams.PURCHASEORDERID.getParameterName()).trim());
        }
        if (allRequestParams.containsKey(ReceivingLineParameters.RECEIPTNUMBER.getParameterName())) {
            allRequestParams.put(ReceivingConstants.RECEIPTNUMBER, allRequestParams.get(ReceiveLineRequestParams.RECEIPTNUMBER.getParameterName()));
        }
        if (allRequestParams.containsKey(ReceivingLineParameters.TRANSACTIONTYPE.getParameterName())) {
            allRequestParams.put(ReceivingConstants.TRANSACTIONTYPE, allRequestParams.get(ReceiveLineRequestParams.TRANSACTIONTYPE.getParameterName()));
        }
        if (allRequestParams.containsKey(ReceivingLineParameters.CONTROLNUMBER.getParameterName())) {
            allRequestParams.put(ReceivingConstants.CONTROLNUMBER, allRequestParams.get(ReceiveLineRequestParams.CONTROLNUMBER.getParameterName()));
        }
        if (allRequestParams.containsKey(ReceivingLineParameters.LOCATIONNUMBER.getParameterName())) {
            allRequestParams.put(ReceivingConstants.LOCATIONNUMBER, allRequestParams.get(ReceiveLineRequestParams.LOCATIONNUMBER.getParameterName()));
        }
        if (allRequestParams.containsKey(ReceivingLineParameters.DIVISIONNUMBER.getParameterName())) {
            allRequestParams.put(ReceivingConstants.DIVISIONNUMBER, allRequestParams.get(ReceiveLineRequestParams.DIVISIONNUMBER.getParameterName()));
        }
    }
}
