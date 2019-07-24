package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.exception.MandatoryPatameterMissingException;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.Map;

public class ReceivingInfoRequestValidator {

    public static void validate(String countryCode, Map<String, String> allRequestParams) {
        Iterator<Map.Entry<String, String>> iterator = allRequestParams.entrySet().iterator();
        while (iterator.hasNext()) {
            // If invalid query param has been passed in request then remove it.
            try {
                Map.Entry<String, String> entry = iterator.next();
                ReceivingInfoRequestQueryParameters.valueOf(entry.getKey().toUpperCase());
                if (StringUtils.isEmpty(entry.getValue())) {
                    iterator.remove();
                }
            } catch (IllegalArgumentException ex) {
                iterator.remove();
            }
        }

        /*
         Here parameter is valid : Checking below conditions
         1) Valid combination present or not
         2) On Valid combination which FT endpoints are getting called.
         3) value should be list or string : Convert from string to list when needed.
         */
        if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.INVOICEID.getQueryParam())) {
            allRequestParams.put("scenario", ReceivingInfoRequestCombinations.INVOICEID.name());
        }
        if (!allRequestParams.containsKey("scenario") && allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam())) {
            if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam())) {
                if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam())) {
                    allRequestParams.put("scenario", ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERNUMBER_INVOICENUMBER.name());
                } else if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.RECEIPTNUMBERS.getQueryParam())) {
                    allRequestParams.put("scenario", ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERNUMBER_RECEIPTNUMBERS.name());
                } else if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam())) {
                    allRequestParams.put("scenario", ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERNUMBER_LOCATIONNUMBER.name());
                }
            } else if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam())) {
                if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam())) {
                    allRequestParams.put("scenario", ReceivingInfoRequestCombinations.VENDORNUMBER_LOCATIONNUMBER_INVOICENUMBER.name());
                } else if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.RECEIPTNUMBERS.getQueryParam())) {
                    allRequestParams.put("scenario", ReceivingInfoRequestCombinations.VENDORNUMBER_LOCATIONNUMBER_RECEIPTNUMBERS.name());
                }
            } else if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.PURCHASEORDERID.getQueryParam())) {
                allRequestParams.put("scenario", ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERID.name());
            }
        }
//        if (!allRequestParams.containsKey("scenario") && allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam())) {
//            if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.CONTROLNUMBER.getQueryParam())) {
//                if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam()) ||
//                        allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam())) {
//                    allRequestParams.put("scenario", "-1");
//                }
//            } else if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam())
//                    || allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam())
//                    || allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam())) {
//                if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam())
//                        || allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam())) {
//                    allRequestParams.put("scenario", "-2");
//                }
//            }
//        }
        // Valid combination does not exist.
        if (!allRequestParams.containsKey("scenario")) {
            throw new MandatoryPatameterMissingException("Please refine request criteria.", "Add or remove few more parameters.");
        } else {
            allRequestParams.put(ReceivingInfoRequestQueryParameters.COUNTRYCODE.getQueryParam(), countryCode);
        }
    }
}
