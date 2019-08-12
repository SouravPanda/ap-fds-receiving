package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.exception.InvalidValueException;
import com.walmart.finance.ap.fds.receiving.request.Meta;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.SorRoutingCtx;
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
    public void validateInventoryMatchStatusTest() {
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);
        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                "1", "9", meta);
        receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest);
    }

    @Test(expected = InvalidValueException.class)
    public void validateInventoryMatchStatusValidationTest() {
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);
        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                "1", "90", meta);
        receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest);
    }

    @Test(expected = InvalidValueException.class)
    public void validateInventoryMatchStatusExceptionTest() {
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);
        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                "1", "9a", meta);
        receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest);
    }

    @Test(expected = InvalidValueException.class)
    public void validateInventoryMatchStatusNumberFormatException() {
        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest();
        receivingSummaryLineRequest.setInventoryMatchStatus("abc");
        receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest);
    }

    @Test
    public void validateReceiptLineNumber() {
        receiveSummaryLineValidator.validateReceiptLineNumber("7");
    }

    @Test(expected = InvalidValueException.class)
    public void validateReceiptLineNumberException() {
        receiveSummaryLineValidator.validateReceiptLineNumber("a");
    }
}

