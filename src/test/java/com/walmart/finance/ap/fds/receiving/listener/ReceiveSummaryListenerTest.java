package com.walmart.finance.ap.fds.receiving.listener;

import com.walmart.finance.ap.fds.receiving.messageproducer.Producer;
import com.walmart.finance.ap.fds.receiving.request.Meta;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.request.SorRoutingCtx;
import com.walmart.finance.ap.fds.receiving.response.SuccessMessage;
import com.walmart.finance.ap.fds.receiving.response.SummaryPayload;
import com.walmart.finance.ap.fds.receiving.service.ReceiveSummaryServiceImpl;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

import static org.mockito.Mockito.doNothing;


@PrepareForTest(ReceiveSummaryListener.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class ReceiveSummaryListenerTest {

    @InjectMocks
    private ReceiveSummaryListener receiveSummaryListener;

    @Mock
    private Producer producer;

    @Mock
    private ReceiveSummaryValidator receiveSummaryValidator;

    @Mock
    private ReceiveSummaryServiceImpl receiveSummaryServiceImpl;

    @Test
    public void onReceiveSummaryCommit() {
        SuccessMessage summaryMessage = new SuccessMessage();
        SummaryPayload summaryPayload = new SummaryPayload();
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);
        ReceivingSummaryRequest receivingSummaryRequest = new ReceivingSummaryRequest("888", "998", LocalDate.of(2018, 10, 10),
                1, "Z", meta);
        Assert.assertNotNull(receivingSummaryRequest);
        summaryPayload.setBusinessStatusCode(receivingSummaryRequest.getBusinessStatusCode());
        summaryPayload.setLocationNumber(receivingSummaryRequest.getLocationNumber());
        summaryPayload.setPurchaseOrderId(receivingSummaryRequest.getPurchaseOrderId());
        summaryPayload.setReceiveId(receivingSummaryRequest.getReceiptNumber());
        summaryPayload.setReceiveDate(receivingSummaryRequest.getReceiptDate());
        summaryMessage.setPayload(summaryPayload);
        doNothing().when(producer).sendSummaryToEventHub(summaryMessage, "test");
        receiveSummaryListener.onReceiveSummaryCommit(receivingSummaryRequest);
    }
}
