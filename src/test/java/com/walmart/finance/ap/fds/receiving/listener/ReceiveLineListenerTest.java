package com.walmart.finance.ap.fds.receiving.listener;

import com.walmart.finance.ap.fds.receiving.messageproducer.Producer;
import com.walmart.finance.ap.fds.receiving.request.Meta;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.SorRoutingCtx;
import com.walmart.finance.ap.fds.receiving.response.SuccessMessage;
import com.walmart.finance.ap.fds.receiving.response.SummaryLinePayload;
import com.walmart.finance.ap.fds.receiving.service.ReceiveSummaryServiceImpl;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDate;

import static org.mockito.Mockito.doNothing;

@PrepareForTest(ReceiveLineListener.class)
@RunWith(PowerMockRunner.class)
public class ReceiveLineListenerTest {

    @InjectMocks
    private ReceiveLineListener receiveLineListener;

    @Mock
    private Producer producer;

    @Mock
    private ReceiveSummaryValidator receiveSummaryValidator;

    @Mock
    private ReceiveSummaryServiceImpl receiveSummaryServiceImpl;


    @Test
    public void onReceiveLineCommit() {
        SuccessMessage summaryLineMessage = new SuccessMessage();
        SummaryLinePayload summaryLinePayload = new SummaryLinePayload();
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);
        String countryCode = "US";
        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                "1", "2", meta);
        Assert.assertNotNull(receivingSummaryLineRequest);
        summaryLinePayload.setBusinessStatusCode(receivingSummaryLineRequest.getBusinessStatusCode());
        summaryLinePayload.setLocationNumber(receivingSummaryLineRequest.getLocationNumber());
        summaryLinePayload.setPurchaseOrderId(receivingSummaryLineRequest.getPurchaseOrderId());
        summaryLinePayload.setReceiveId(receivingSummaryLineRequest.getReceiptNumber());
        summaryLinePayload.setReceiveDate(receivingSummaryLineRequest.getReceiptDate());
        summaryLinePayload.setInventoryMatchStatus(receivingSummaryLineRequest.getInventoryMatchStatus());
        summaryLinePayload.setLineSequenceNumber(receivingSummaryLineRequest.getReceiptLineNumber());
        summaryLineMessage.setPayload(summaryLinePayload);
        doNothing().when(producer).sendSummaryLineToEventHub(summaryLineMessage, "test");
        receiveLineListener.onReceiveLineCommit(receivingSummaryLineRequest);
    }
}
