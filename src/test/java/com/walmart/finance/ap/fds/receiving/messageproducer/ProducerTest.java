package com.walmart.finance.ap.fds.receiving.messageproducer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@EnableKafka
@RunWith(SpringJUnit4ClassRunner.class)
public class ProducerTest {

    @Spy
    @InjectMocks
    private Producer producer;

    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendToEventHubException() {
        when(kafkaTemplate.send(Mockito.anyString(), Mockito.anyString())).thenThrow(Exception.class);
        producer.sendToEventHub(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void sendToEventHub() throws InterruptedException, ExecutionException, TimeoutException {
        TopicPartition topicPartition = new TopicPartition("topic", 2);
        RecordMetadata recordMetadata = new RecordMetadata(topicPartition, 1, 2, 0, null, 0, 0);
        SendResult<String, String> sendResult = new SendResult<>(null, recordMetadata);
        ListenableFuture<SendResult<String, String>> listenableFuture = mock(ListenableFuture.class);
        when(kafkaTemplate.send(Mockito.anyString(), Mockito.anyString())).thenReturn(listenableFuture);
        when(listenableFuture.get(10, TimeUnit.SECONDS)).thenReturn(sendResult);
        producer.sendToEventHub(Mockito.anyString(), Mockito.anyString());
    }
}