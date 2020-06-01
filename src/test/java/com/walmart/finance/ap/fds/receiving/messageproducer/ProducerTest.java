package com.walmart.finance.ap.fds.receiving.messageproducer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.common.ReceivingUtils;
import com.walmart.finance.ap.fds.receiving.request.Meta;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.SorRoutingCtx;
import com.walmart.finance.ap.fds.receiving.response.SuccessMessage;
import com.walmart.finance.ap.fds.receiving.response.SummaryLinePayload;
import com.walmart.finance.ap.fds.receiving.response.SummaryPayload;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.mockito.Mockito.when;

@EnableBinding(CustomSource.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class ProducerTest {

    @Spy
    @InjectMocks
    private Producer producer;

    @Mock
    private CustomSource customSource;

    @Mock
    private MessageChannel messageChannel;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendSummaryToEventHub() throws IOException {
        SuccessMessage summaryMessage = new SuccessMessage();
        summaryMessage.setSuccess(ReceivingConstants.TRUE);
        summaryMessage.setObjectName(ReceivingConstants.APPLICATION_TYPE_SUMMARY);
        summaryMessage.setMessageTimeStamp(LocalDateTime.now().atZone(ZoneId.of(ReceivingConstants.ZONE_ID)).toInstant().toEpochMilli());
        summaryMessage.setOperation(ReceivingConstants.OPERATION_TYPE);
        summaryMessage.setDomain(ReceivingConstants.DOMAIN_NAME);
        summaryMessage.set_id("1048771056|0000082301|4900|0");
        summaryMessage.setPartitionKey("7026|2019|11");
        SummaryPayload summaryPayload = new SummaryPayload();
        summaryMessage.setPayload(summaryPayload);
        when(customSource.summaryTopic()).thenReturn(messageChannel);
        ObjectMapper mapper = new ObjectMapper();
        String value = mapper.writeValueAsString(summaryMessage);
        ObjectNode valueTree = (ObjectNode) mapper.readTree(value);
        when(messageChannel.send(MessageBuilder.withPayload(valueTree).build())).thenReturn(Boolean.TRUE);
       producer.sendSummaryToEventHub(summaryMessage, "test");
    }

    @Test
    public void sendSummaryLineToEventHub() throws IOException {
        SuccessMessage summaryLineMessage = new SuccessMessage();
        SummaryLinePayload summaryLinePayload = new SummaryLinePayload();
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);
        summaryLineMessage.setPayload(summaryLinePayload);
        when(customSource.lineSummaryTopic()).thenReturn(messageChannel);
        ObjectMapper mapper = new ObjectMapper();
        String value = mapper.writeValueAsString(summaryLineMessage);
        ObjectNode valueTree = (ObjectNode) mapper.readTree(value);
        when(messageChannel.send(MessageBuilder.withPayload(valueTree).build())).thenReturn(Boolean.TRUE);
        producer.sendSummaryLineToEventHub(summaryLineMessage, "");
    }

    @Test
    public void sendSummaryToEventHubtestException() throws IOException {
        SuccessMessage summaryMessage = new SuccessMessage();
        when(customSource.summaryTopic()).thenReturn(messageChannel);
        ObjectMapper mapper = new ObjectMapper();
        String value = mapper.writeValueAsString(summaryMessage);
        ObjectNode valueTree = (ObjectNode) mapper.readTree(value);
        when(messageChannel.send(MessageBuilder.withPayload(valueTree).build())).thenReturn(Boolean.TRUE);
        return;
    }
    }

