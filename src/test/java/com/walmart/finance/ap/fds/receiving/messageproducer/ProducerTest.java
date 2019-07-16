package com.walmart.finance.ap.fds.receiving.messageproducer;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.doThrow;


@RunWith(SpringJUnit4ClassRunner.class)
public class ProducerTest {

    @Spy
    @InjectMocks
    private  Producer producer;

    @Mock
    private KafkaTemplate kafkaTemplate;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test(expected = Exception.class)
    public void sendReceiveSummary() {

        ReceiveSummary receiveSummary = new ReceiveSummary("abc","2",2,2,2,null,null,2,2,2,2,2,'A',0.0,0.0,'A',2L,'A','A','A',null,null,null,0.0,2,2,2,null,2,"2","A",null,"A",'A',"A");
        producer.sendReceiveSummary(receiveSummary,"fds-db-dev.test.receive-summary");
        doThrow(new Exception()).when(producer).sendReceiveSummary(receiveSummary,"fds-db-dev.test.receive-summary");
    }

    @Test(expected = Exception.class)
    public void sendReceiveLine() {
        ReceivingLine receiveLine = new ReceivingLine();
        receiveLine.setPurchasedOrderId(345);
        receiveLine.setBaseDivisionNumber(34);
        receiveLine.set_id("3243434|998945|48545");
        producer.sendReceiveLine(receiveLine,"fds-db-dev.test.receive-summary");
        doThrow(new Exception()).when(producer).sendReceiveLine(receiveLine,"fds-db-dev.test.receive-summary");
    }
}