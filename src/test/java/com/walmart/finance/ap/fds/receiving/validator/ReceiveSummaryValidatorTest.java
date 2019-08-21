package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.exception.InvalidValueException;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummaryRequestParams;
import com.walmart.finance.ap.fds.receiving.request.Meta;
import com.walmart.finance.ap.fds.receiving.request.SorRoutingCtx;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@PrepareForTest(ReceiveSummaryValidator.class)
@RunWith(PowerMockRunner.class)
public class ReceiveSummaryValidatorTest {

    @InjectMocks
    ReceiveSummaryValidator receiveSummaryValidator;

    @Test
    public void validateBusinessStatUpdateSummaryTest() {
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);
        receiveSummaryValidator.validateBusinessStatUpdateSummary("A");
    }

    @Test(expected = InvalidValueException.class)
    public void validateBusinessStatUpdateSummaryNegativeTest() {
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);
        receiveSummaryValidator.validateBusinessStatUpdateSummary("AD");
    }

    @Test
    public void isWareHouseData() {
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx("R", 36, "US");
        assertTrue(receiveSummaryValidator.isWareHouseData(sorRoutingCtx));
    }

    @Test
    public void isWareHouseDataInvalidCountry() {
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx("R", 36, "RUS");
        assertFalse(receiveSummaryValidator.isWareHouseData(sorRoutingCtx));
    }

    @Test
    public void isWareHouseDataInvalidCountry2() {
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx("F", 36, "RUS");
        assertFalse(receiveSummaryValidator.isWareHouseData(sorRoutingCtx));
    }

    @Test
    public void isWareHouseDataInvalidCountry3() {
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx("U", 36, "RUS");
        assertFalse(receiveSummaryValidator.isWareHouseData(sorRoutingCtx));
    }

    @Test
    public void isWareHouseDataValidCountry() {
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx("R", 30, "US");
        assertTrue(receiveSummaryValidator.isWareHouseData(sorRoutingCtx));
    }

    @Test
    public void isWareHouseDataReplyTypeCd() {
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx("U", 30, "US");
        assertTrue(receiveSummaryValidator.isWareHouseData(sorRoutingCtx));
    }

    @Test
    public void isWareHouseDataReplyType() {
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx("U", 36, "US");
        assertTrue(receiveSummaryValidator.isWareHouseData(sorRoutingCtx));
    }

    @Test
    public void isWareHouseDataReplyTypeCd2() {
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx("F", 30, "US");
        assertTrue(receiveSummaryValidator.isWareHouseData(sorRoutingCtx));
    }

    @Test
    public void isWareHouseDataReplyTypeCd3() {
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx("F", 36, "US");
        assertTrue(receiveSummaryValidator.isWareHouseData(sorRoutingCtx));
    }

    @Test
    public void validateSummaryArguments() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingConstants.PURCHASEORDERNUMBER, "111");
        allRequestParams.put(ReceivingConstants.TRANSACTIONTYPE, "99");
        allRequestParams.put(ReceivingConstants.LOCATIONNUMBER, "113");
        allRequestParams.put(ReceivingConstants.RECEIPTNUMBERS, "555");
        allRequestParams.put(ReceivingConstants.CONTROLNUMBER, "999");
        allRequestParams.put(ReceivingConstants.PURCHASEORDERID, "88");
        allRequestParams.put(ReceivingConstants.INVOICEID, "97");
        allRequestParams.put(ReceivingConstants.VENDORNUMBER, "111");
        allRequestParams.put(ReceivingConstants.RECEIVINGCONTROLNUMBER, "00");
        allRequestParams.put(ReceivingConstants.DIVISIONNUMBER, "77");
        allRequestParams.put(ReceivingConstants.ITEMNUMBERS, "55");
        allRequestParams.put(ReceivingConstants.UPCNUMBERS, "09");
        allRequestParams.put(ReceivingConstants.RECEIPTDATESTART, "08-09-2017");
        allRequestParams.put(ReceivingConstants.RECEIPTDATEEND, "09-09-2018");
        allRequestParams.put(ReceivingConstants.DEPARTMENTNUMBER, "0");
        allRequestParams.put(ReceivingConstants.INVOICENUMBER, "134");
        allRequestParams.put(ReceiveSummaryRequestParams.ORDER.getParameterName(),"");
        allRequestParams.put(ReceiveSummaryRequestParams.ORDERBY.getParameterName(),"");
        allRequestParams.put(ReceiveSummaryRequestParams.PAGENBR.getParameterName(),"");
        allRequestParams.put(ReceiveSummaryRequestParams.PAGESIZE.getParameterName(),"");
        ReceiveSummaryValidator.validate("US", allRequestParams);
    }

    @Test(expected = BadRequestException.class)
    public void validateInvalidSummaryArguments() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingConstants.PURCHASEORDERID, "111");
        allRequestParams.put(ReceivingConstants.TRANSACTIONTYPE, "99");
        allRequestParams.put(ReceivingConstants.WM_ENV, "113");
        ReceiveSummaryValidator.validate("US", allRequestParams);
    }

    @Test
    public void validateEmptySummaryArguments() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingConstants.PURCHASEORDERID, null);
        allRequestParams.put(ReceivingConstants.TRANSACTIONTYPE, "99");
        allRequestParams.put(ReceivingConstants.LOCATIONNUMBER, "113");
        allRequestParams.put(ReceivingConstants.RECEIPTNUMBERS, "555");
        allRequestParams.put(ReceivingConstants.CONTROLNUMBER, "999");
        allRequestParams.put(ReceivingConstants.DIVISIONNUMBER, "00");
        ReceiveSummaryValidator.validate("US", allRequestParams);
    }

}
