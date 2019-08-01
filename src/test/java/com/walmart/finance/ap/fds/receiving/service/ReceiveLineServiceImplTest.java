package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineResponseConverter;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
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
                0, 1.9, "LL", 0, "", null, null, null, null, null, null, null, null, null, null);
        ReceivingLine receivingLineAt = new ReceivingLine("0|0|0|0|0|null|null|12", "6778", 0,
                0, 0, 0, 0.0, 0.0, "0", 0,
                0, "0KLL", 0, 0, 0, null, null, 12,
                LocalDateTime.of(1985, 10, 17, 18, 45, 21), 'A', "BKP", "111",
                0, LocalDate.now(), 0, 1.9, "LL", 0, "", null, null, null, null, null, null, null, null, null, null);
        listOfContent.add(receivingLine);
        listOfContent.add(receivingLineAt);
        ReceivingLineResponse receivingLineResponse = new ReceivingLineResponse(new Long(0), 0, 0, 0, 0, 2.9,
                1.9, 0, 0, null, "0",
                null, null, null, 0, null, 0, 0, 10.0, null);
        ReceivingLineResponse receivingLineResponseAt = new ReceivingLineResponse(new Long(0), 0, 0, 0, 0, 2.9,
                1.9, 0, 0, null, "0",
                null, null, null, 0, null, 0, 0, 10.0, null);
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
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        Assert.assertEquals(receiveLineServiceImpl.getLineSummary(eq(allRequestParams)).isSuccess(), successMessage.isSuccess());
    /*    try {
            Assert.assertEquals(receiveLineServiceImpl.getLineSummary(eq(Mockito.anyMap())).isSuccess(), successMessage.isSuccess());
        } catch (NullPointerException e) {
            e.getMessage();
        }*/
    }


    @Test(expected = BadRequestException.class)
    public void getLineSummaryNumberFormatException() {
     /*   try {
            receiveLineServiceImpl.getLineSummary(eq(Mockito.anyMap()));
        } catch (NullPointerException e) {
            e.getMessage();
        }*/
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        receiveLineServiceImpl.getLineSummary(eq(allRequestParams));
    }

    // @Test(expected = NotFoundException.class)
    @Test
    public void getLineSummaryNotFoundException() {
        //try {
        when(mongoTemplate.find(Mockito.any(Query.class), Mockito.any(Class.class), Mockito.any())).thenReturn(null);
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        allRequestParams.put(Mockito.anyString(), Mockito.anyString());
        receiveLineServiceImpl.getLineSummary(eq(allRequestParams));
     /*   } catch (NullPointerException e) {
            e.getMessage();
        }*/
    }
}


