

 package com.walmart.finance.ap.fds.receiving.service.impl;

 import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
 import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
 import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
 import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
 import com.walmart.finance.ap.fds.receiving.service.ReceiveSummaryServiceImpl;
 import org.junit.Assert;
 import org.junit.Before;
 import org.junit.Test;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.Mockito;
 import org.mockito.MockitoAnnotations;
 import org.springframework.data.domain.PageImpl;
 import org.springframework.data.domain.PageRequest;
 import org.springframework.data.domain.Pageable;
 import org.springframework.data.domain.Sort;
 import org.springframework.data.mongodb.core.MongoTemplate;
 import org.springframework.data.mongodb.core.query.Criteria;
 import org.springframework.data.mongodb.core.query.Query;

 import java.time.LocalDate;
 import java.time.LocalDateTime;
 import java.time.LocalTime;
 import java.util.ArrayList;
 import java.util.List;

 import static org.mockito.Mockito.when;


    public class ReceiveSummaryServiceImplTest {
        @InjectMocks
        ReceiveSummaryServiceImpl receiveSummaryServiceImpl;

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
            Query query = new Query();

        @Test
        public void getReceiveSummaryTest() {
            ReceiveSummary receiveSummary = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21","4665267",
                    8264,18,0,LocalDate.of(1996,12,12), LocalTime.of(18,45,21),0,7688,1111,
                    0,0,'H',0.0,1.0,1,'P',2L,'k','L',
                    'M',LocalDateTime.of(1990,12,12,18,56,22),LocalDate.now(),
                    LocalDate.now(),9.0,7,0,0,LocalDateTime.now(),0,"JJJ","yyyy",LocalDateTime.now(),"99"
                    ,'K',"LLL");
            ReceiveSummary receiveSummaryAt = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21","4665207",
                    8064,18,0,LocalDate.of(1986,12,12), LocalTime.of(18,45,21),0,9788,1111,
                    0,0,'H',0.0,1.0,1,'P',2L,'k','L',
                    'M',LocalDateTime.of(1990,12,12,18,56,22),LocalDate.now(),
                    LocalDate.now(),9.0,7,0,0,LocalDateTime.now(),0,"JJJ","UU",LocalDateTime.now(),"99"
                    ,'K',"IIL");

            List listOfContent = new ArrayList<ReceiveSummary>();
            listOfContent.add(receiveSummary);
            listOfContent.add(receiveSummaryAt);

            Query query = new Query();
            Criteria criteria = Criteria.where("receivingControlNumber").is(466567).and("baseDivisionNumber").is(0).
                    and("MDSReceiveDate").is(LocalDate.of(1996,12,12)).and("transactionType").is(99)
                    .and("storeNumber").is(3680).and("purchaseOrderNumber").is("999").and("poReceiveId").is("87865").and("departmentNumber")
                    .is(0).and("vendorNumber").is(9986);
            query.addCriteria(criteria);

            Pageable pageable = PageRequest.of(pageNbr, pageSize);
            query.with(pageable);

            ReceivingSummaryResponse receivingSummaryResponse= new ReceivingSummaryResponse("7778",1122,99,"776",3680,0,
                    LocalDate.of(1986,12,12),'L',78,"hjhj",77,"user","ooi",LocalDateTime.now(),7.0,
                    9.0,9,"hh",8,0);

            ReceivingSummaryResponse receivingSummaryResponseAt= new ReceivingSummaryResponse("7708",1122,99,"776",3680,0,
                    LocalDate.of(1986,12,12),'L',78,"kkk",77,"user","ooi",LocalDateTime.now(),7.0,
                    9.0,0,"hh",0,0);

            List<ReceivingSummaryResponse> content = new ArrayList<>();
            content.add(receivingSummaryResponse);
            content.add(receivingSummaryResponseAt);

            when(mongoTemplate.find(Mockito.any(Query.class), Mockito.any(Class.class), Mockito.anyString())).thenReturn(listOfContent);
            when(receivingSummaryResponseConverter.convert(Mockito.any(ReceiveSummary.class))).thenReturn(receivingSummaryResponse);

            PageRequest pageRequest = new PageRequest(1, 1, Sort.unsorted());
            PageImpl<ReceivingLineResponse> pageImplResponse = new PageImpl(content, pageRequest, 1);

            when(mongoTemplate.count(query, ReceiveSummary.class)).thenReturn(2L);

            Assert.assertEquals(receiveSummaryServiceImpl.getReceiveSummary("777","77","8","88","66",
                    "99","675","987","18","WW8","776"
            ,"1980-12-12","1988-12-12",1,1,"creationDate",Sort.DEFAULT_DIRECTION).toString(),pageImplResponse.toString());
        }
    }




