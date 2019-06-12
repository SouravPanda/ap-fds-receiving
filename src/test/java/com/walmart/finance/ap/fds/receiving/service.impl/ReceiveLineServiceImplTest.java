package com.walmart.finance.ap.fds.receiving.service.impl;

import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineResponseConverter;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineServiceImpl;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
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

    int pageNbr = 1;
    int pageSize = 1;

    @Test
    public void getLineSummaryTest() throws Exception {

       List<String> orderByproperties = new ArrayList<>();
        orderByproperties.add("creationDate");


        List listOfContent = new ArrayList<ReceivingLine>();
        ReceivingLine receivingLine = new ReceivingLine("112|1804823|8264|18|0|1995-10-17|1995-10-17T18:45:21|122", "4665267",
                0, 3777, 94493, 0, 0.0, 0.0, "9",
                89, 12, "1122", 99, 8264, 18,
                LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 22,
                LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 0, LocalDate.now(),
                0, 1.9, "LL",0,"");
        ReceivingLine receivingLineAt = new ReceivingLine("0|0|0|0|0|null|null|12", "6778", 0,
                0, 0, 0, 0.0, 0.0, "0", 0,
                0, "0KLL", 0, 0, 0, null, null, 12,
                LocalDateTime.of(1985, 10, 17, 18, 45, 21), 'A', "BKP", "111",
                0, LocalDate.now(), 0, 1.9, "LL",0,"");
        listOfContent.add(receivingLine);
        listOfContent.add(receivingLineAt);

        Query query = new Query();

        Criteria criteria = Criteria.where("receivingControlNumber").is(466567).and("purchaseOrderReceiveID").is(1).and("transactionType").is(0)
                .and("baseDivisionNumber").is(44).and("storeNumber").is(112);
        query.addCriteria(criteria);
        Pageable pageable = PageRequest.of(pageNbr, pageSize);
        query.with(pageable);
/*
        ReceivingLineResponse receivingLineResponse = new ReceivingLineResponse(0, 0, 0, 0, 0, 2.9,
                1.9, 0, 0, 0, "KKO09", 0, "0",
                "0", "0", null, null, null, 0, "0", 0, 0);
                        ReceivingLineResponse receivingLineResponseAt = new ReceivingLineResponse(0, 0, 0, 0, 0, 2.9,
                1.9, 0, 0, 0, "KKO09", 0, "0",
                "0", "0", null, null, null, 0, "0", 0, 0);
*/
        ReceivingLineResponse receivingLineResponse = new ReceivingLineResponse(0, 0, 0, 0, 0, 2.9,
                1.9, 0, 0,"0",
                null, null, null, 0, 0, 0);
        ReceivingLineResponse receivingLineResponseAt = new ReceivingLineResponse(0, 0, 0, 0, 0, 2.9,
                1.9, 0, 0,"0",
                 null, null, null, 0,  0, 0);
        List<ReceivingLineResponse> content = new ArrayList<>();
        content.add(receivingLineResponse);
        content.add(receivingLineResponseAt);

        when(mongoTemplate.find(Mockito.any(Query.class), Mockito.any(Class.class), Mockito.anyString())).thenReturn(listOfContent);
        when(receivingLineResponseConverter.convert(Mockito.any(ReceivingLine.class))).thenReturn(receivingLineResponse);

        PageRequest pageRequest = new PageRequest(1, 1, Sort.unsorted());
        PageImpl<ReceivingLineResponse> pageImplResponse = new PageImpl(content, pageRequest, 1);

        when(mongoTemplate.count(query, ReceivingLine.class)).thenReturn(2L);

    Assert.assertEquals(receiveLineServiceImpl.getLineSummary("78887","1", "1", "777","87","88"),content);

    }

}


