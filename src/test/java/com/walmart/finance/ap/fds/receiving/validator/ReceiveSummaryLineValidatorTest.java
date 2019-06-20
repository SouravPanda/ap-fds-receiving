package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.request.Meta;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.SorRoutingCtx;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDate;

@PrepareForTest(ReceiveSummaryLineValidator.class)
@RunWith(PowerMockRunner.class)
public class ReceiveSummaryLineValidatorTest {

    @InjectMocks
    ReceiveSummaryLineValidator receiveSummaryLineValidator;

    @Test
    public void validateBusinessStatUpdateSummaryTest() {

        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);

        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                1, "9", meta);
        Assert.assertTrue(receiveSummaryLineValidator.validateBusinessStatUpdateSummary(receivingSummaryLineRequest));
    }

    @Test
    public void validateInventoryMatchStatusTest() {
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);

        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                1, "9", meta);
        Assert.assertTrue(receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest));
    }

    @Test
    public void validateBusinessStatUpdateSummaryNegativeTest(){
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);

        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "Y",
                1, "9", meta);
        Assert.assertFalse(receiveSummaryLineValidator.validateBusinessStatUpdateSummary(receivingSummaryLineRequest));

    }

    @Test
    public void validateInventoryMatchStatusValidationTest() {
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);

        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                1, "9.", meta);
        Assert.assertFalse(receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest));
    }

    @Test
    public void validateInventoryMatchStatusExceptionTest() {
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);

        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                1, "9a", meta);
        Assert.assertFalse(receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest));
    }
}

