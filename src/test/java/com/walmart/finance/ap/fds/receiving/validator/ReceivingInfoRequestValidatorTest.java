package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.exception.MandatoryPatameterMissingException;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ReceivingInfoRequestValidatorTest {

    @InjectMocks
    ReceivingInfoRequestValidator receivingInfoRequestValidator;

    @Test
    public void validateScneario1() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingInfoRequestQueryParameters.INVOICEID.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), null);
        allRequestParams.put("notExist","123");
        ReceivingInfoRequestValidator.validate("US", allRequestParams);
        assertTrue(allRequestParams.get("scenario").equalsIgnoreCase(ReceivingInfoRequestCombinations.INVOICEID.name()));
    }

    @Test
    public void validateScneario2() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam(), "1234");
        ReceivingInfoRequestValidator.validate("US", allRequestParams);
        assertTrue(allRequestParams.get("scenario").equalsIgnoreCase(ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERNUMBER_INVOICENUMBER.name()));
    }

    @Test
    public void validateScneario3() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTNUMBERS.getQueryParam(), "1234");
        ReceivingInfoRequestValidator.validate("US", allRequestParams);
        assertTrue(allRequestParams.get("scenario").equalsIgnoreCase(ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERNUMBER_RECEIPTNUMBERS.name()));
    }

    @Test
    public void validateScneario4() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam(), "1234");
        ReceivingInfoRequestValidator.validate("US", allRequestParams);
        assertTrue(allRequestParams.get("scenario").equalsIgnoreCase(ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERNUMBER_LOCATIONNUMBER.name()));
    }

    @Test
    public void validateScneario5() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), "1234");
        ReceivingInfoRequestValidator.validate("US", allRequestParams);
        assertTrue(allRequestParams.get("scenario").equalsIgnoreCase(ReceivingInfoRequestCombinations.VENDORNUMBER_LOCATIONNUMBER_INVOICENUMBER.name()));
    }

    @Test
    public void validateScneario6() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTNUMBERS.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), "1234");
        ReceivingInfoRequestValidator.validate("US", allRequestParams);
        assertTrue(allRequestParams.get("scenario").equalsIgnoreCase(ReceivingInfoRequestCombinations.VENDORNUMBER_LOCATIONNUMBER_RECEIPTNUMBERS.name()));
    }

    @Test
    public void validateScneario7() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingInfoRequestQueryParameters.PURCHASEORDERID.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), "1234");
        ReceivingInfoRequestValidator.validate("US", allRequestParams);
        assertTrue(allRequestParams.get("scenario").equalsIgnoreCase(ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERID.name()));
    }

    @Test
    public void validateScneario8() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam(), "1234");
        ReceivingInfoRequestValidator.validate("US", allRequestParams);
        assertTrue(allRequestParams.get("scenario").equalsIgnoreCase(ReceivingInfoRequestCombinations.LOCATIONNUMBER_PURCHASEORDERNUMBER_RECEIPTDATESTART_RECEIPTDATEEND.name()));
    }

    @Test
    public void validateScneario9() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam(), "1234");
        ReceivingInfoRequestValidator.validate("US", allRequestParams);
        assertTrue(allRequestParams.get("scenario").equalsIgnoreCase(ReceivingInfoRequestCombinations.LOCATIONNUMBER_INVOICENUMBER_RECEIPTDATESTART_RECEIPTDATEEND.name()));
    }

    @Test
    public void validateScneario10() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), "1234");
        ReceivingInfoRequestValidator.validate("US", allRequestParams);
        assertTrue(allRequestParams.get("scenario").equalsIgnoreCase(ReceivingInfoRequestCombinations.LOCATIONNUMBER_VENDORNUMBER_RECEIPTDATESTART_RECEIPTDATEEND.name()));
    }

    @Test(expected = MandatoryPatameterMissingException.class)
    public void inValidScneario() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam(), "1234");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam(), "1234");
        ReceivingInfoRequestValidator.validate("US", allRequestParams);
        assertTrue(allRequestParams.get("scenario").equalsIgnoreCase(ReceivingInfoRequestCombinations.LOCATIONNUMBER_VENDORNUMBER_RECEIPTDATESTART_RECEIPTDATEEND.name()));
    }
}