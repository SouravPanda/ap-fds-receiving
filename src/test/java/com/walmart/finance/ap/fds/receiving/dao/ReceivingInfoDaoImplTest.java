package com.walmart.finance.ap.fds.receiving.dao;

import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.response.WHLinePOLineValue;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.junit.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.UOM_CODE_WH_EXCEPTION_RESOLUTION;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class ReceivingInfoDaoImplTest {

    @InjectMocks
    ReceivingLineDaoImpl receivingLineDao;

    @InjectMocks
    ReceivingSummaryDaoImpl receivingSummaryDao;

    @InjectMocks
    FreightDaoImpl freightDao;

    @Mock
    MongoTemplateWithRetry mongoTemplate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        receivingSummaryDao.setSummaryCollection("receivingSummary");
        receivingLineDao.setLineCollection("receivingLine");
        freightDao.setFreightCollection("freight");

    }

    @Test
    public void testSummaryAggregation() {

        List<Criteria> criteriaList =  new ArrayList<>();
        criteriaList.add(Criteria.where("_id").is("999403403|0000030006|3669|0"));

        when(mongoTemplate.aggregate(Mockito.any(Aggregation.class), eq("receivingSummary"), eq(ReceiveSummary.class)))
                .thenReturn(mockMongoTemplateSummary());

        List<ReceiveSummary>  receiveSummaryList = receivingSummaryDao.executeSummaryAggregation(criteriaList);

        Assert.assertNotNull(receiveSummaryList);
    }

    public AggregationResults<ReceiveSummary> mockMongoTemplateSummary() {

        ReceiveSummary receiveSummary = prepareReceiveSummary();

        AggregationResults<ReceiveSummary> receiveSummaryAggregationResults = new AggregationResults<>(new ArrayList<ReceiveSummary>() {
            {
                add(receiveSummary);
            }
        }, new Document());

        return receiveSummaryAggregationResults;
    }

    private ReceiveSummary prepareReceiveSummary() {

        ReceiveSummary receiveSummary = new ReceiveSummary("999403403|0000030006|3669|0",
                "999403403", 3669, 18, 99,
                LocalDate.of(1996, 12, 12), LocalTime.of(18, 45, 21),
                0, 7688, 1111, 0, 0,
                "H", 0.0, 99.0, 'A',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56,
                22), LocalDate.of(2019, 03, 14),
                LocalDate.now(), 9.0, 7, "0",
                0, LocalDateTime.now(), 0, "0000030006", "yyyy",
                LocalDateTime.now(), "4665267"
                , 'K', "LLL", 0.0, new Long(999403403), null, null, null, LocalDateTime.of(2019, 03, 14, 8, 45, 21),
                null,null);

        return receiveSummary;
    }

    @Test
    public void testLineAggregation() {

        List<Criteria> criteriaList =  new ArrayList<>();
        criteriaList.add(Criteria.where("_id").is("999403403|0000030006|3669|0"));

        when(mongoTemplate.aggregate(Mockito.any(Aggregation.class), eq("receivingLine"), eq(ReceivingLine.class)))
                .thenReturn(mockMongoTemplateLine());

        List<ReceivingLine>  receiveLineList = receivingLineDao.executeLineAggregation(criteriaList);

        Assert.assertNotNull(receiveLineList);
    }

    private AggregationResults<ReceivingLine> mockMongoTemplateLine() {

        List<ReceivingLine> receivingLines = prepareReceiveLine();

        AggregationResults<ReceivingLine> receivingLineAggregationResults =
                new AggregationResults<>(receivingLines, new Document());

        return receivingLineAggregationResults;
    }

    private List<ReceivingLine> prepareReceiveLine() {

        Map<String, WHLinePOLineValue> poLineValueMap = new HashMap<>();
        poLineValueMap.put(UOM_CODE_WH_EXCEPTION_RESOLUTION, new WHLinePOLineValue(UOM_CODE_WH_EXCEPTION_RESOLUTION,
                6, 30.0, 40.0));

        List<ReceivingLine> receivingLines = new ArrayList<ReceivingLine>() {
            {
                add(new ReceivingLine("999403403|0000030006|3669|2019-06-19|1", "0000030006",
                        10, 3777L, 94493, 7.0, 30.0, 40.0, "9",
                        89, 12, "1122", 99, 3669, 18,
                        LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 22,
                        LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 6, LocalDate.now(),
                        0, 1.9, "LL", 0, "ww",
                        null, poLineValueMap, 1, "N", "NSW CRASH TRNF", 1, new Long(999403403), "999403403|0000030006|3669|0",
                        null, null, null));
            }
        };

        return receivingLines;
    }

    @Test
    public void testFreightQuery() {

        FreightResponse freightResponse =
                new FreightResponse(new Long(31721227), "ARFW", "972035",123,"34567",20.00, LocalDate.now(),new Long(12),new Long(45),"4",0,"02","EA");

        List<FreightResponse> list = new ArrayList<FreightResponse>();
        list.add(freightResponse);
        when(mongoTemplate.find(Mockito.any(Query.class), eq(FreightResponse.class), eq("freight"))).
                thenReturn(list);

        List<FreightResponse> retResponse =  freightDao.executeQueryInFreight(new Query());

        Assert.assertEquals(freightResponse,retResponse.get(0));


    }
}
