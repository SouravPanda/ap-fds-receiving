package com.walmart.finance.ap.fds.receiving.validator;


import com.walmart.finance.ap.fds.receiving.request.Meta;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.request.SorRoutingCtx;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDate;

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

        ReceivingSummaryRequest receivingSummaryRequest = new ReceivingSummaryRequest("888", "998", LocalDate.of(2018, 10, 10),
                1, "A", meta);
        Assert.assertTrue(receiveSummaryValidator.validateBusinessStatUpdateSummary(receivingSummaryRequest));
    }

    @Test
    public void validateBusinessStatUpdateSummaryNegativeTest() {

        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);

        ReceivingSummaryRequest receivingSummaryRequest = new ReceivingSummaryRequest("888", "998", LocalDate.of(2018, 10, 10),
                1, "Y", meta);
        Assert.assertFalse(receiveSummaryValidator.validateBusinessStatUpdateSummary(receivingSummaryRequest));
    }
}
