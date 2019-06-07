
package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveSummaryServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@WebMvcTest(ReceivingLineController.class)
public class ReceivingSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ReceiveSummaryServiceImpl receiveSummaryServiceImpl;

    @Mock
    MongoTemplate mongoTemplate;

    @Mock
    ReceivingSummaryResponseConverter receivingSummaryResponseConverter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    int pageNbr = 1;
    int pageSize = 1;

    @Test
    public void getReceiveSummaryTest() throws Exception {


        ReceiveSummary receiveSummary = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "4665267",
                8264, 18, 0, LocalDate.of(1996, 12, 12), LocalTime.of(18, 45, 21), 0, 7688, 1111,
                0, 0, 'H', 0.0, 1.0, 1, 'P', 2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, 0, 0, LocalDateTime.now(), 0, "JJJ", "yyyy", LocalDateTime.now(), "99"
                , 'K', "LLL");
        ReceiveSummary receiveSummaryAt = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "4665207",
                8064, 18, 0, LocalDate.of(1986, 12, 12), LocalTime.of(18, 45, 21), 0, 9788, 1111,
                0, 0, 'H', 0.0, 1.0, 1, 'P', 2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, 0, 0, LocalDateTime.now(), 0, "JJJ", "UU", LocalDateTime.now(), "99"
                , 'K', "IIL");

        List listOfContent = new ArrayList<ReceiveSummary>();
        listOfContent.add(receiveSummary);
        listOfContent.add(receiveSummaryAt);

        Query query = new Query();
        Criteria criteria = Criteria.where("receivingControlNumber").is(466567).and("baseDivisionNumber").is(0).
                and("MDSReceiveDate").is(LocalDate.of(1996, 12, 12)).and("transactionType").is(99)
                .and("storeNumber").is(3680).and("purchaseOrderNumber").is("999").and("poReceiveId").is("87865").and("departmentNumber")
                .is(0).and("vendorNumber").is(9986);
        query.addCriteria(criteria);

        Pageable pageable = PageRequest.of(pageNbr, pageSize);
        query.with(pageable);

        ReceivingSummaryResponse receivingSummaryResponse = new ReceivingSummaryResponse("7778", 1122, 99, "776", 3680, 0,
                LocalDate.of(1986, 12, 12), 'L', 78, "hjhj", "77", "user",  LocalDateTime.now(), 9.0,7.0,
                "9LLL",0L,0, 9,"LL", 0, "PP",0, 0,"jjj");

        ReceivingSummaryResponse receivingSummaryResponseAt = new ReceivingSummaryResponse("999778", 10022, 99, "776", 3680, 0,
                LocalDate.of(1986, 12, 12), 'L', 78, "hjhj", "77", "user",  LocalDateTime.now(), 9.0,7.0,
                "9LLL",0L,0, 9,"LL", 0, "PP",0, 0,"88");

        List<ReceivingSummaryResponse> content = new ArrayList<>();
        content.add(receivingSummaryResponse);
        content.add(receivingSummaryResponseAt);

        List<String> listOfReceiptNumbers= new ArrayList<>();
        listOfReceiptNumbers.add("99");
        listOfReceiptNumbers.add("89");

        List<String> listOfItemNumbers= new ArrayList<>();
        listOfItemNumbers.add("99K");
        listOfItemNumbers.add("89P");

        List<String> listOfUpcNumbers= new ArrayList<>();
        listOfItemNumbers.add("9K");
        listOfItemNumbers.add("89P");

        PageRequest pageRequest = new PageRequest(1, 1, Sort.unsorted());
        PageImpl<ReceivingSummaryResponse> pageImplResponse = new PageImpl(content, pageRequest, 1);


        when(mongoTemplate.find(Mockito.any(Query.class), Mockito.any(Class.class), Mockito.anyString())).thenReturn(listOfContent);
        when(receivingSummaryResponseConverter.convert(Mockito.any(ReceiveSummary.class))).thenReturn(receivingSummaryResponse);

        Mockito.when(receiveSummaryServiceImpl.getReceiveSummary("777", "77", "8", listOfReceiptNumbers, "66",
                "99", "675", "987", "18", "WW8", "776"
                , "1980-12-12", "1988-12-12", "1990-12-12", listOfItemNumbers, listOfUpcNumbers)).thenReturn(content);

    }

    @Test
    public void updateSummaryTest(){
        ReceivingSummaryRequest receivingSummaryRequest = new ReceivingSummaryRequest("0","0",LocalDate.now(),0,"A",'A',"TT",null);
        Mockito.when(receiveSummaryServiceImpl.updateReceiveSummary(receivingSummaryRequest,"US")).thenReturn(receivingSummaryRequest);
    }

    @Test
    public void updateSummaryAndLineTest(){
        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("0","0",LocalDate.now(),0,"A",'A',"TT",9,0,null);
        Mockito.when(receiveSummaryServiceImpl.updateReceiveSummaryAndLine(receivingSummaryLineRequest,"US")).thenReturn(receivingSummaryLineRequest);
    }
}



