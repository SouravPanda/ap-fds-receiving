package com.walmart.finance.ap.fds.receiving.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.messageproducer.Producer;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.request.Meta;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.request.SorRoutingCtx;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;


@PrepareForTest(ReceiveSummaryListener.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class ReceiveSummaryListenerTest {

    @InjectMocks
    private ReceiveSummaryListener receiveSummaryListener;

    @Mock
    private Producer producer;

    @Test
    public void onReceiveSummaryCommit() {
        ReceiveSummary event = new ReceiveSummary();
        event.set_id("89384");
        event.setReceiveSequenceNumber(456);
        event.setBaseDivisionNumber(20);
        event.setTotalCostAmount(Double.parseDouble("30.00"));
        event.setVendorNumber(32);
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);
        ReceivingSummaryRequest receivingSummaryRequest = new ReceivingSummaryRequest("888", "998", LocalDate.of(2018, 10, 10),
                1, "P", meta);
        receiveSummaryListener.onReceiveSummaryCommit(receivingSummaryRequest);
    }

    @Test
    public void onReceiveSummaryCommitException() throws JsonProcessingException {
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("CA");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);
        ReceivingSummaryRequest receivingSummaryRequest = new ReceivingSummaryRequest("888", "998", LocalDate.of(2018, 10, 10),
                1, "D", meta);

        Mockito.doThrow(JsonProcessingException.class).when(producer).sendSummaryToEventHub(new ObjectMapper().writeValueAsString(receivingSummaryRequest),
                ReceivingConstants.RECEIVESUMMARYWAREHOUSE);
        receiveSummaryListener.onReceiveSummaryCommit(receivingSummaryRequest);
    }
}
