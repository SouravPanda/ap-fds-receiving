package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.common.ReceiveSummaryBusinessStat;
import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.exception.ReceivingErrors;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummaryRequestParams;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class ReceiveSummaryValidator {

    public static void validate(String countryCode, Map<String, String> allRequestParams) {
        Iterator<Map.Entry<String, String>> iterator = allRequestParams.entrySet().iterator();
        while (iterator.hasNext()) {
            // If invalid query param has been passed in request then remove it.
            try {
                Map.Entry<String, String> entry = iterator.next();
                ReceiveSummaryRequestParams.valueOf(entry.getKey().toUpperCase());
                if (StringUtils.isEmpty(entry.getValue())) {
                    iterator.remove();
                }
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException(ReceivingErrors.JUNKPARAMS.getParameterName(),ReceivingErrors.INVALIDQUERYPARAMS.getParameterName());
            }
        }
        if (allRequestParams.containsKey(ReceiveSummaryRequestParams.PURCHASEORDERNUMBER.getParameterName())) {
            allRequestParams.put(ReceivingConstants.PURCHASEORDERNUMBER, allRequestParams.get(ReceiveSummaryRequestParams.PURCHASEORDERID).trim());
        }
        if (allRequestParams.containsKey(ReceiveSummaryRequestParams.PURCHASEORDERID.getParameterName())) {
            allRequestParams.put(ReceivingConstants.PURCHASEORDERID, allRequestParams.get(ReceiveSummaryRequestParams.PURCHASEORDERID.getParameterName()));
        }
        if (allRequestParams.containsKey(ReceiveSummaryRequestParams.TRANSACTIONTYPE.getParameterName())) {
            allRequestParams.put(ReceivingConstants.TRANSACTIONTYPE, allRequestParams.get(ReceiveSummaryRequestParams.TRANSACTIONTYPE.getParameterName()));
        }
        if (allRequestParams.containsKey(ReceiveSummaryRequestParams.CONTROLNUMBER.getParameterName())) {
            allRequestParams.put(ReceivingConstants.CONTROLNUMBER, allRequestParams.get(ReceiveSummaryRequestParams.CONTROLNUMBER.getParameterName()));
        }
        if (allRequestParams.containsKey(ReceiveSummaryRequestParams.LOCATIONNUMBER.getParameterName())) {
            allRequestParams.put(ReceivingConstants.LOCATIONNUMBER, allRequestParams.get(ReceiveSummaryRequestParams.LOCATIONNUMBER.getParameterName()));
        }
        if (allRequestParams.containsKey(ReceiveSummaryRequestParams.DIVISIONNUMBER.getParameterName())) {
            allRequestParams.put(ReceivingConstants.DIVISIONNUMBER, allRequestParams.get(ReceiveSummaryRequestParams.DIVISIONNUMBER.getParameterName()));
        }
        if (allRequestParams.containsKey(ReceiveSummaryRequestParams.INVOICEID.getParameterName())) {
            allRequestParams.put(ReceivingConstants.INVOICEID, allRequestParams.get(ReceiveSummaryRequestParams.INVOICEID.getParameterName()));
        }
        if (allRequestParams.containsKey(ReceiveSummaryRequestParams.INVOICENUMBER.getParameterName())) {
            allRequestParams.put(ReceivingConstants.INVOICENUMBER, allRequestParams.get(ReceiveSummaryRequestParams.INVOICENUMBER.getParameterName()));
        }
        if (allRequestParams.containsKey(ReceiveSummaryRequestParams.RECEIPTDATESTART.getParameterName())) {
            allRequestParams.put(ReceivingConstants.RECEIPTDATESTART, allRequestParams.get(ReceiveSummaryRequestParams.RECEIPTDATESTART.getParameterName()));
        }
        if (allRequestParams.containsKey(ReceiveSummaryRequestParams.RECEIPTDATEEND.getParameterName())) {
            allRequestParams.put(ReceivingConstants.RECEIPTDATEEND, allRequestParams.get(ReceiveSummaryRequestParams.RECEIPTDATEEND.getParameterName()));
        }
        if (allRequestParams.containsKey(ReceiveSummaryRequestParams.VENDORNUMBER.getParameterName())) {
            allRequestParams.put(ReceivingConstants.VENDORNUMBER, allRequestParams.get(ReceiveSummaryRequestParams.VENDORNUMBER.getParameterName()));
        }
        if (allRequestParams.containsKey(ReceiveSummaryRequestParams.DEPARTMENTNUMBER.getParameterName())) {
            allRequestParams.put(ReceivingConstants.DEPARTMENTNUMBER, allRequestParams.get(ReceiveSummaryRequestParams.DEPARTMENTNUMBER.getParameterName()));
        }
        if (allRequestParams.containsKey(ReceiveSummaryRequestParams.RECEIPTNUMBERS.getParameterName())) {
            allRequestParams.put(ReceivingConstants.RECEIPTNUMBERS, allRequestParams.get(ReceiveSummaryRequestParams.RECEIPTNUMBERS.getParameterName()));
        }
    }

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



