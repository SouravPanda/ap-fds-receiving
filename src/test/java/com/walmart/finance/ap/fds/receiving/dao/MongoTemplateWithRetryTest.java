package com.walmart.finance.ap.fds.receiving.dao;

import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class MongoTemplateWithRetryTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private MongoTemplateWithRetry mongoTemplateWithRetry;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFind() {

        FreightResponse freightResponse =
                new FreightResponse(new Long(31721227), "ARFW", "972035",123,"34567",20.00, LocalDate.now(),new Long(12),new Long(45),"4",0,"02","EA");

        List<FreightResponse> list = new ArrayList<FreightResponse>();

        when(mongoTemplate.find(Mockito.any(), eq(FreightResponse.class), eq("freight"))).
                thenReturn(list);

        List<FreightResponse> retList = mongoTemplateWithRetry.find(new Query(),FreightResponse.class, "freight");

        Assert.assertNotNull(retList);
    }

    @Test
    public void testAggregate() {

        when(mongoTemplate.aggregate(Mockito.any(Aggregation.class), eq("receivingLine"), eq(ReceivingLine.class))).
                thenReturn(Mockito.mock(AggregationResults.class));

        AggregationResults<ReceivingLine> retaggregationResults = mongoTemplateWithRetry.aggregate(
                Mockito.mock(Aggregation.class),"receivingLine",ReceivingLine.class);

        Assert.assertNotNull(retaggregationResults);
    }

    @Test
    public void tesFindByID() {

        FreightResponse freightResponse =
                new FreightResponse(new Long(31721227), "ARFW", "972035",123,
                        "34567",20.00, LocalDate.now(),new Long(12),new Long(45),"4",
                        0,"02","EA");

        when(mongoTemplate.findById(Mockito.any(), Mockito.any(), Mockito.anyString())).
                thenReturn(freightResponse);

        FreightResponse retResponse = mongoTemplateWithRetry.findById(
                new Long(123),FreightResponse.class, "freight");

        Assert.assertEquals(freightResponse, retResponse);
    }
}
