package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.exception.MandatoryPatameterMissingException;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.Map;

import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.LOCATION_TYPE_STORE;
import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.LOCATION_TYPE_WAREHOUSE;

public class ReceivingInfoRequestValidator {
    private ReceivingInfoRequestValidator() {
    }

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

        if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam()) &&
                ( allRequestParams.get(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam()).equals(LOCATION_TYPE_STORE) ||
                        allRequestParams.get(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam()).equals(LOCATION_TYPE_WAREHOUSE))) {

        /*
         Here parameter is valid : Checking below conditions
         1) Valid combination present or not
         2) On Valid combination which FT endpoints are getting called.
         3) value should be list or string : Convert from string to list when needed.
         */
            if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.INVOICEID.getQueryParam())) {
                allRequestParams.put(ReceivingConstants.SCENARIO, ReceivingInfoRequestCombinations.INVOICEID.name());
            }
            if (!allRequestParams.containsKey(ReceivingConstants.SCENARIO) && allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam()) &&
                    allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam())) {
                if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam())) {
                    allRequestParams.put(ReceivingConstants.SCENARIO, ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERNUMBER_INVOICENUMBER.name());
                } else if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.RECEIPTNUMBERS.getQueryParam())) {
                    allRequestParams.put(ReceivingConstants.SCENARIO, ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERNUMBER_RECEIPTNUMBERS.name());
                } else if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam())) {
                    allRequestParams.put(ReceivingConstants.SCENARIO, ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERNUMBER_LOCATIONNUMBER.name());
                }
            }
            if (!allRequestParams.containsKey(ReceivingConstants.SCENARIO) && allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam()) &&
                    allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam())) {
                if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam())) {
                    allRequestParams.put(ReceivingConstants.SCENARIO, ReceivingInfoRequestCombinations.VENDORNUMBER_LOCATIONNUMBER_INVOICENUMBER.name());
                } else if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.RECEIPTNUMBERS.getQueryParam())) {
                    allRequestParams.put(ReceivingConstants.SCENARIO, ReceivingInfoRequestCombinations.VENDORNUMBER_LOCATIONNUMBER_RECEIPTNUMBERS.name());
                }
            }
            if (!allRequestParams.containsKey(ReceivingConstants.SCENARIO) && allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam()) &&
                    allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.PURCHASEORDERID.getQueryParam())) {
                allRequestParams.put(ReceivingConstants.SCENARIO, ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERID.name());
            }
            if (!allRequestParams.containsKey(ReceivingConstants.SCENARIO) && allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam())
                    && allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam())
                    && allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam())) {
                if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam())) {
                    allRequestParams.put(ReceivingConstants.SCENARIO, ReceivingInfoRequestCombinations.LOCATIONNUMBER_PURCHASEORDERNUMBER_RECEIPTDATESTART_RECEIPTDATEEND.name());
                } else if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam())) {
                    allRequestParams.put(ReceivingConstants.SCENARIO, ReceivingInfoRequestCombinations.LOCATIONNUMBER_INVOICENUMBER_RECEIPTDATESTART_RECEIPTDATEEND.name());
                } else if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam())) {
                    allRequestParams.put(ReceivingConstants.SCENARIO, ReceivingInfoRequestCombinations.LOCATIONNUMBER_VENDORNUMBER_RECEIPTDATESTART_RECEIPTDATEEND.name());
                } else if ( allRequestParams.get(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam()).equals(LOCATION_TYPE_STORE)) {
                    allRequestParams.put(ReceivingConstants.SCENARIO, ReceivingInfoRequestCombinations.LOCATIONNUMBER_RECEIPTDATESTART_RECEIPTDATEEND.name());
                }
            }

            if (allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.TRANSACTIONID.getQueryParam()) &&
                    allRequestParams.containsKey(ReceivingInfoRequestQueryParameters.TXNSEQNBR.getQueryParam())) {
                allRequestParams.put(ReceivingConstants.SCENARIO, ReceivingInfoRequestCombinations.TRANSACTIONID_TRANSACTIONSEQNBR.name());
            }

            // Valid combination does not exist.
            if (!allRequestParams.containsKey(ReceivingConstants.SCENARIO)) {
                throw new MandatoryPatameterMissingException("Please refine request criteria.", "Add or remove few more parameters.");
            } else {
                allRequestParams.put(ReceivingInfoRequestQueryParameters.COUNTRYCODE.getQueryParam(), countryCode);
            }
        } else {
            throw new MandatoryPatameterMissingException("Please refine request criteria.", "Please provide " +
                    "'locationType' as 'S' or 'W'");
        }
    }
}
