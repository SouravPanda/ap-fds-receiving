package com.walmart.finance.ap.fds.receiving.messageproducer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    @Mock
    private MySQLApi mySQLApi;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendSummaryToEventHub()  {
        when(customSource.summaryTopic()).thenReturn(messageChannel);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objNode = mapper.createObjectNode();
        Assert.assertNotNull(objNode);
        when(messageChannel.send(MessageBuilder.withPayload(objNode).build())).thenReturn(Boolean.TRUE);
        producer.sendSummaryToEventHub(objNode, "");
    }

    @Test
    public void sendSummaryLineToEventHub()  {
        when(customSource.lineSummaryTopic()).thenReturn(messageChannel);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objNode = mapper.createObjectNode();
        Assert.assertNotNull(objNode);
        when(messageChannel.send(MessageBuilder.withPayload(objNode).build())).thenReturn(Boolean.TRUE);
        producer.sendSummaryLineToEventHub(objNode, "");
    }
}
