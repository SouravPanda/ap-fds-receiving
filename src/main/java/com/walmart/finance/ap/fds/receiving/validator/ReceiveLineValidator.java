package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
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
                ReceivingLineParameters.valueOf(entry.getKey().toUpperCase());
                if (StringUtils.isEmpty(entry.getValue())) {
                    iterator.remove();
                }
            } catch (IllegalArgumentException ex) {
                iterator.remove();
            }
        }
        if (allRequestParams.containsKey(ReceivingLineParameters.PURCHASEORDERID.getParameterName())) {
            allRequestParams.put(ReceivingConstants.PURCHASEORDERID, allRequestParams.get(ReceivingLineParameters.PURCHASEORDERID).trim());
        }
        if (allRequestParams.containsKey(ReceivingLineParameters.RECEIPTNUMBER.getParameterName())) {
            allRequestParams.put(ReceivingConstants.RECEIVEID, allRequestParams.get(ReceivingLineParameters.RECEIPTNUMBER));
        }
        if (allRequestParams.containsKey(ReceivingLineParameters.TRANSACTIONTYPE.getParameterName())) {
            allRequestParams.put(ReceivingConstants.TRANSACTIONTYPE, allRequestParams.get(ReceivingLineParameters.TRANSACTIONTYPE));
        }
        if (allRequestParams.containsKey(ReceivingLineParameters.CONTROLNUMBER.getParameterName())) {
            allRequestParams.put(ReceivingConstants.CONTROLNUMBER, allRequestParams.get(ReceivingLineParameters.CONTROLNUMBER));
        }
        if (allRequestParams.containsKey(ReceivingLineParameters.LOCATIONNUMBER.getParameterName())) {
            allRequestParams.put(ReceivingConstants.LOCATIONNUMBER, allRequestParams.get(ReceivingLineParameters.LOCATIONNUMBER));
        }
        if (allRequestParams.containsKey(ReceivingLineParameters.DIVISIONNUMBER.getParameterName())) {
            allRequestParams.put(ReceivingConstants.BASEDIVISONNUMBER, allRequestParams.get(ReceivingLineParameters.DIVISIONNUMBER));
        }
    }
}
