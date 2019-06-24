package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineResponseConverter;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@PrepareForTest(ReceiveLineServiceImpl.class)
@RunWith(PowerMockRunner.class)
public class ReceiveLineServiceImplTest {

    @InjectMocks
    ReceiveLineServiceImpl receiveLineServiceImpl;

    @Mock
    ReceivingLineResponseConverter receivingLineResponseConverter;

    @Mock
    MongoTemplate mongoTemplate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getLineSummaryTest() throws Exception {

        List listOfContent = new ArrayList<ReceivingLine>();
        ReceivingLine receivingLine = new ReceivingLine("112|1804823|8264|18|0|1995-10-17|1995-10-17T18:45:21|122", "4665267",
                0, 3777, 94493, 0, 0.0, 0.0, "9",
                89, 12, "1122", 99, 8264, 18,
                LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 22,
                LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 0, LocalDate.now(),
                0, 1.9, "LL", 0, "");
        ReceivingLine receivingLineAt = new ReceivingLine("0|0|0|0|0|null|null|12", "6778", 0,
                0, 0, 0, 0.0, 0.0, "0", 0,
                0, "0KLL", 0, 0, 0, null, null, 12,
                LocalDateTime.of(1985, 10, 17, 18, 45, 21), 'A', "BKP", "111",
                0, LocalDate.now(), 0, 1.9, "LL", 0, "");
        listOfContent.add(receivingLine);
        listOfContent.add(receivingLineAt);

        ReceivingLineResponse receivingLineResponse = new ReceivingLineResponse(0, 0, 0, 0, 0, 2.9,
                1.9, 0, 0,null, "0",
                null, null, null, 0, null, 0, 0,10.0);
        ReceivingLineResponse receivingLineResponseAt = new ReceivingLineResponse(0, 0, 0, 0, 0, 2.9,
                1.9, 0, 0,null, "0",
                null, null, null, 0, null, 0, 0,10.0);
        List<ReceivingLineResponse> content = new ArrayList<>();
        content.add(receivingLineResponse);
        content.add(receivingLineResponseAt);

        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(content);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.of(2018, 10, 10, 0, 40, 0));

        Query mockQuery = Mockito.mock(Query.class);

        when(mockQuery.limit(Mockito.anyInt())).thenReturn(mockQuery);
        when(mongoTemplate.find(Mockito.any(Query.class), Mockito.any(Class.class), Mockito.any())).thenReturn(listOfContent);
        when(receivingLineResponseConverter.convert(Mockito.any(ReceivingLine.class))).thenReturn(receivingLineResponse);

        Assert.assertEquals(receiveLineServiceImpl.getLineSummary("78887", "1", "1", "777", "87", "88").getData(), successMessage.getData());

    }

}


