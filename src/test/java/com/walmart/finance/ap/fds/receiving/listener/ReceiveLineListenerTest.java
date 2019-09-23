package com.walmart.finance.ap.fds.receiving.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.walmart.finance.ap.fds.receiving.messageproducer.Producer;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.Meta;
import com.walmart.finance.ap.fds.receiving.request.SorRoutingCtx;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

import static org.mockito.Mockito.doThrow;

@RunWith(SpringJUnit4ClassRunner.class)
public class ReceiveLineListenerTest {

    @Spy
    @InjectMocks
    private ReceiveLineListener receiveLineListener;

    @Mock
    private Producer producer;

    @Test
    public void onReceiveLineCommit() {
        ReceivingLine receivingLine = new ReceivingLine();
        receivingLine.set_id("89384");
        receivingLine.setInventoryMatchStatus(2);
        receivingLine.setBaseDivisionNumber(20);
        receivingLine.setCostAmount(Double.parseDouble("32.00"));
        receivingLine.setPurchasedOrderId(234);
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);
        String countryCode = "US";
        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                "1", "2", meta);
        //String value = "{\"schema\":{\"type\":\"struct\",\"fields\":[{\"type\":\"int16\",\"optional\":false,\"field\":\"STORE_NBR\"},{\"type\":\"int16\",\"optional\":false,\"field\":\"BASE_DIV_NBR\"},{\"type\":\"int32\",\"optional\":false,\"field\":\"TRANSACTION_TYPE\"},{\"type\":\"string\",\"optional\":false,\"field\":\"CONTROL_NBR\"},{\"type\":\"int32\",\"optional\":false,\"field\":\"CONTROL_NBR_TYPE\"},{\"type\":\"int64\",\"optional\":false,\"name\":\"org.apache.kafka.connect.data.Timestamp\",\"version\":1,\"field\":\"RECV_TIMESTAMP\"},{\"type\":\"int32\",\"optional\":false,\"name\":\"org.apache.kafka.connect.data.Date\",\"version\":1,\"field\":\"TRANSACTION_DATE\"},{\"type\":\"int32\",\"optional\":false,\"name\":\"org.apache.kafka.connect.data.Time\",\"version\":1,\"field\":\"TRANSACTION_TIME\"},{\"type\":\"int32\",\"optional\":false,\"name\":\"org.apache.kafka.connect.data.Date\",\"version\":1,\"field\":\"FINAL_DATE\"},{\"type\":\"int32\",\"optional\":false,\"name\":\"org.apache.kafka.connect.data.Time\",\"version\":1,\"field\":\"FINAL_TIME\"},{\"type\":\"string\",\"optional\":false,\"field\":\"TOTAL_MATCH_IND\"},{\"type\":\"int32\",\"optional\":false,\"field\":\"ORIG_DEST_ID\"},{\"type\":\"int16\",\"optional\":false,\"field\":\"ACCTG_DIV_NBR\"},{\"type\":\"double\",\"optional\":false,\"field\":\"TOTAL_RETAIL_AMT\"},{\"type\":\"double\",\"optional\":false,\"field\":\"TOTAL_SALE_AMT\"},{\"type\":\"double\",\"optional\":false,\"field\":\"TOTAL_COST_AMT\"},{\"type\":\"int16\",\"optional\":false,\"field\":\"CONTROL_SEQ_NBR\"},{\"type\":\"int32\",\"optional\":false,\"name\":\"org.apache.kafka.connect.data.Date\",\"version\":1,\"field\":\"RPR_DATE\"},{\"type\":\"int32\",\"optional\":false,\"field\":\"RPR_SEQ_NBR\"},{\"type\":\"int32\",\"optional\":false,\"field\":\"CASES_RECV\"},{\"type\":\"int32\",\"optional\":false,\"field\":\"CASES_SHORT_OVER\"},{\"type\":\"int32\",\"optional\":false,\"field\":\"CASES_DAMAGED\"},{\"type\":\"double\",\"optional\":false,\"field\":\"INVOICE_COST\"},{\"type\":\"int16\",\"optional\":false,\"field\":\"SITE_STORE_NBR\"},{\"type\":\"int32\",\"optional\":false,\"field\":\"SEQUENCE_NBR\"},{\"type\":\"int16\",\"optional\":false,\"field\":\"LINE_NBR\"},{\"type\":\"int32\",\"optional\":false,\"field\":\"ITEM_NBR\"},{\"type\":\"string\",\"optional\":false,\"field\":\"UPC_NBR\"},{\"type\":\"double\",\"optional\":false,\"field\":\"ITEM_QTY\"},{\"type\":\"int16\",\"optional\":false,\"field\":\"ACCTG_DEPT_NBR\"},{\"type\":\"string\",\"optional\":false,\"field\":\"USERID\"},{\"type\":\"string\",\"optional\":false,\"field\":\"TERMID\"},{\"type\":\"double\",\"optional\":false,\"field\":\"COST_AMT\"},{\"type\":\"int16\",\"optional\":false,\"field\":\"COST_MULT\"},{\"type\":\"double\",\"optional\":false,\"field\":\"STORE_SALE_AMT\"},{\"type\":\"int16\",\"optional\":false,\"field\":\"STORE_SALE_MULT\"}],\"optional\":false},\"payload\":{\"STORE_NBR\":2741,\"BASE_DIV_NBR\":1,\"TRANSACTION_TYPE\":1,\"CONTROL_NBR\":\"000000001029380\",\"CONTROL_NBR_TYPE\":0,\"RECV_TIMESTAMP\":1556263028579,\"TRANSACTION_DATE\":18012,\"TRANSACTION_TIME\":26224000,\"FINAL_DATE\":18012,\"FINAL_TIME\":26225000,\"TOTAL_MATCH_IND\":\"I\",\"ORIG_DEST_ID\":860024,\"ACCTG_DIV_NBR\":28,\"TOTAL_RETAIL_AMT\":707.04,\"TOTAL_SALE_AMT\":707.04,\"TOTAL_COST_AMT\":478.0,\"CONTROL_SEQ_NBR\":1,\"RPR_DATE\":18012,\"RPR_SEQ_NBR\":442,\"CASES_RECV\":1,\"CASES_SHORT_OVER\":0,\"CASES_DAMAGED\":0,\"INVOICE_COST\":0.0,\"SITE_STORE_NBR\":2741,\"SEQUENCE_NBR\":14,\"LINE_NBR\":1,\"ITEM_NBR\":570021281,\"UPC_NBR\":\"0000610764863650\",\"ITEM_QTY\":24.0,\"ACCTG_DEPT_NBR\":95,\"USERID\":\"rscrawf   \",\"TERMID\":\"ASNAUDIT  \",\"COST_AMT\":16.0,\"COST_MULT\":12,\"STORE_SALE_AMT\":1.98,\"STORE_SALE_MULT\":1}}";
        receiveLineListener.onReceiveLineCommit(receivingSummaryLineRequest);
    }

    @Test
    public void onReceiveLineCommitException() {
        doThrow(JsonProcessingException.class).when(producer).sendSummaryLineToEventHub(Mockito.anyString(), Mockito.anyString());
        receiveLineListener.onReceiveLineCommit(Mockito.any());
    }
}
