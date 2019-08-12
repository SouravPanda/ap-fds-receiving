package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.exception.InvalidValueException;
import com.walmart.finance.ap.fds.receiving.request.Meta;
import com.walmart.finance.ap.fds.receiving.request.SorRoutingCtx;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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
    public void isWareHouseDataReplyTypeCd() {
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx("U", 30, "US");
        assertTrue(receiveSummaryValidator.isWareHouseData(sorRoutingCtx));
    }

    @Test
    public void isWareHouseDataReplyTypeCd2() {
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx("F", 30, "US");
        assertTrue(receiveSummaryValidator.isWareHouseData(sorRoutingCtx));
    }
}
