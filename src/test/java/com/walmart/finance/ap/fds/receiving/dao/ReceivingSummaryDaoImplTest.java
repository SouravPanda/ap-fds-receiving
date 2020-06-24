package com.walmart.finance.ap.fds.receiving.dao;

import com.mongodb.client.result.UpdateResult;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.response.WHLinePOLineValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.UOM_CODE_WH_EXCEPTION_RESOLUTION;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@PrepareForTest(ReceivingSummaryDaoImpl.class)
@RunWith(PowerMockRunner.class)
public class ReceivingSummaryDaoImplTest {

    @InjectMocks
    ReceivingSummaryDaoImpl receivingSummaryDaoImpl;

    @Mock
    MongoTemplate mongoTemplate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test

    public void updateReceiveSummary() {
        ReceiveSummary receiveSummary = new ReceiveSummary("9|8|1|0", "8",
                6565, 18, 0, LocalDate.of(1995, 10, 16), LocalTime.of(18, 30, 00),
                0, 122663, 1111,
                0, 0, "H", 0.0, 1.0, 'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.of(1995, 10, 16),
                LocalDate.of(1995, 10, 16), 9.0, 7, "0", 0, (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), 0,
                "999997", "yyyy", (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), "9"
                , 'K', "LLL", null, null, null, null, null, LocalDateTime.now(), null, null);

        when(mongoTemplate.findAndModify(Mockito.any(Query.class), Mockito.any(Update.class), refEq(FindAndModifyOptions.options().returnNew(true)),
                eq(ReceiveSummary.class), Mockito.any())).thenReturn(receiveSummary);

        receivingSummaryDaoImpl.updateReceiveSummary(Mockito.any(Query.class), Mockito.any(Update.class), refEq(FindAndModifyOptions.options().returnNew(true)),
                eq(ReceiveSummary.class), Mockito.any());

    }

    @Test
    public void updateReceiveSummaryAndLine() {

        Map<String, WHLinePOLineValue> poLineValueMap = new HashMap<>();
        poLineValueMap.put(UOM_CODE_WH_EXCEPTION_RESOLUTION, new WHLinePOLineValue(UOM_CODE_WH_EXCEPTION_RESOLUTION,
                0, 0.0, 0.0));
        ReceivingLine receivingLine = new ReceivingLine("9|8|1|0|1", "8",
                0, 3777L, 94493, 0.0, 0.0, 0.0, "9",
                89, 12, "1122", 99, 8264, 18,
                LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 1,
                LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 0, LocalDate.now(),
                0, 1.9, "LL", 9, "OO", null, poLineValueMap, null, null, null, null, null, null, null, null, null);
        when(mongoTemplate.findAndModify(Mockito.any(Query.class), Mockito.any(Update.class), refEq(FindAndModifyOptions.options().returnNew(true)),
                eq(ReceivingLine.class), Mockito.any())).thenReturn(receivingLine);
        receivingSummaryDaoImpl.updateReceiveSummaryAndLine(Mockito.any(Query.class), Mockito.any(Update.class), refEq(FindAndModifyOptions.options().returnNew(true)),
                eq(ReceivingLine.class), Mockito.any());
    }

    @Test
    public void updateReceiveSummaryAndLines() {
        UpdateResult updateResult = mock(UpdateResult.class);
        when(mongoTemplate.updateMulti(Mockito.any(Query.class), Mockito.any(Update.class),
                eq(ReceivingLine.class), Mockito.any())).thenReturn(updateResult);
        receivingSummaryDaoImpl.updateReceiveSummaryAndLines(Mockito.any(Query.class), Mockito.any(Update.class),
                eq(ReceivingLine.class), Mockito.any());
    }
}