

package com.walmart.finance.ap.fds.receiving.service.impl;

import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveSummaryServiceImpl;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryLineValidator;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryValidator;
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


    @Mock
    ReceiveSummaryValidator receiveSummaryValidator;

    @Mock
    ReceiveSummaryLineValidator receiveSummaryLineValidator;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    int pageNbr = 1;
    int pageSize = 1;
    Query query = new Query();

    @Test
    public void getReceiveSummaryTest() {
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

        List<String> listOfReceiptNumbers= new ArrayList<>();
        listOfReceiptNumbers.add("99");
        listOfReceiptNumbers.add("89");

        List<String> listOfItemNumbers= new ArrayList<>();
        listOfItemNumbers.add("99K");
        listOfItemNumbers.add("89P");

        List<String> listOfUpcNumbers= new ArrayList<>();
        listOfItemNumbers.add("9K");
        listOfItemNumbers.add("89P");

        Query query = new Query();
        Criteria criteria = Criteria.where("receivingControlNumber").is(466567).and("baseDivisionNumber").is(0).
                and("MDSReceiveDate").is(LocalDate.of(1996, 12, 12)).and("transactionType").is(99)
                .and("storeNumber").is(3680).and("purchaseOrderNumber").is("999").and("poReceiveId").is("87865").and("departmentNumber")
                .is(0).and("vendorNumber").is(9986);
        query.addCriteria(criteria);

        Pageable pageable = PageRequest.of(pageNbr, pageSize);
        query.with(pageable);

        ReceivingSummaryResponse receivingSummaryResponse = new ReceivingSummaryResponse("7778", 1122, 99, "776", 3680, 0,
                LocalDate.of(1986, 12, 12), 'L', 78, "HH89", "77", "user",  LocalDateTime.now(), 9.0,7.0,
                "9LLL",0L,0, 9,"LL", 0, "PP",0, 0,"jjj");

        ReceivingSummaryResponse receivingSummaryResponseAt = new ReceivingSummaryResponse("7778", 1122, 99, "776", 3680, 0,
                LocalDate.of(1986, 12, 12), 'L', 78, "998H", "77", "user",  LocalDateTime.now(), 9.0,7.0,
                "9LLL",0L,0, 9,"LL", 0, "PP",0, 0,"jjj");

        List<ReceivingSummaryResponse> content = new ArrayList<>();
        content.add(receivingSummaryResponse);
        content.add(receivingSummaryResponseAt);

        when(mongoTemplate.find(Mockito.any(Query.class), Mockito.any(Class.class), Mockito.anyString())).thenReturn(listOfContent);
        when(receivingSummaryResponseConverter.convert(Mockito.any(ReceiveSummary.class))).thenReturn(receivingSummaryResponse);

        PageRequest pageRequest = new PageRequest(1, 1, Sort.unsorted());
        PageImpl<ReceivingLineResponse> pageImplResponse = new PageImpl(content, pageRequest, 1);

        when(mongoTemplate.count(query, ReceiveSummary.class)).thenReturn(2L);

      /*  Assert.assertEquals(receiveSummaryServiceImpl.getReceiveSummary("777", "77", "8", listOfReceiptNumbers, "66",
                "99", "675", "987", "18", "WW8", "776"
                , "1980-12-12", "1988-12-12", "1990-11-11", listOfItemNumbers, listOfUpcNumbers).toString(), pageImplResponse.toString());*/
    }

    @Test
    public void updateReceiveSummaryTest() {
        Integer vendorNumber = 122663;
        String countryCode = "US";
        Boolean isWareHouseData=false;
        ReceiveSummary receiveSummary = new ReceiveSummary("553683865|999997|6565|0|99|0|0", "553683865",
                6565, 18, 0, LocalDate.of(1996, 12, 12), LocalTime.of(18, 45, 21),
                0, 122663, 1111,
                0, 0, 'H', 0.0, 1.0, 1, 'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, 0, 0, LocalDateTime.now(), 0,
                "999997", "yyyy", LocalDateTime.now(), "99"
                , 'K', "LLL");
        ReceivingSummaryRequest receivingSummaryRequest = new ReceivingSummaryRequest(65267L, 33383L, "56HKKL", 0, "0", 8897, 99, 122663, 997, 999L, "kkk",
                LocalDate.now(),
                LocalDateTime.of(1990, 12, 12, 18, 56, 22),
                LocalTime.now(),
                LocalDateTime.of(1991, 12, 12, 18, 56, 22),
                "UUU", 11.0, 11.9, 988, 2222, 2228, 7665,
                'A', 11.8, 22.9, 90, 'A', 'B', 'C', 88.0,
                44, 49, "hh", 'J', "99",null);
        String id = "553683865|999997|6565|0|99|0|0";
        when(mongoTemplate.findById((Mockito.any()), Mockito.any(Class.class), Mockito.anyString())).thenReturn(receiveSummary);
        Mockito.when(receiveSummaryValidator.validateBusinessStatUpdateSummary(receivingSummaryRequest)).thenReturn(true);
        Mockito.when(receiveSummaryValidator.validateVendorNumberUpdateSummary(receivingSummaryRequest, vendorNumber, countryCode)).thenReturn(true);
      //  Assert.assertEquals(receiveSummaryServiceImpl.updateReceiveSummary(receivingSummaryRequest, vendorNumber, countryCode).toString(),receivingSummaryRequest.toString());
    }

    @Test
    public void updateReceiveSummaryLineTest() {
        Integer vendorNumber = 122663;
        String countryCode = "US";
        ReceiveSummary receiveSummary = new ReceiveSummary("553683865|999997|6565|0|99|0|0", "553683865",
                6565, 18, 0, LocalDate.of(1996, 12, 12), LocalTime.of(18, 45, 21), 0,
                122663, 1111,
                0, 0, 'H', 0.0, 1.0, 1, 'P', 2L,
                'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, 0, 0, LocalDateTime.now(), 0, "999997",
                "yyyy", LocalDateTime.now(), "99"
                , 'K', "LLL");
        ReceivingSummaryLineRequest receivingSummaryLineSearch = new ReceivingSummaryLineRequest(65267L, 33383L, 99,"56HKKL",
                0,0,LocalDate.now(),LocalTime.now(),9,99,0,98,0,8.9,8.7,0, "0", 8897L,'A','N','L',LocalDate.now(),22.0,0,0,0,
                LocalDateTime.of(1990, 12, 12, 18, 56, 22),9,
                "UUU","user","purchase", 11.0, "hyhh",LocalDateTime.of(1998, 12, 12, 18, 56, 22),LocalDateTime.of(2000, 12, 12, 18, 56, 22),
                "988", 2222,
                2228,"bbb", 7665,0,0, 11.8, 22.9, 0,0,0,0,null);
        ReceivingLine receivingLine = new ReceivingLine("112|1804823|8264|18|0|1995-10-17|1995-10-17T18:45:21|122", "4665267",
                0, 3777, 94493, 0, 0.0, 0.0, "9",
                89, 12, "1122", 99, 8264, 18,
                LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 22,
                LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 0, LocalDate.now(),
                0, 1.9, "LL",0);
        String id ="56HKKL|33383|0|0|99|1998-12-12T18:56:22|2000-12-12T18:56:22";
        String lineId = "56HKKL|33383|0|0|99|2019-06-04|21:08:43.981|0";
        Mockito.when(mongoTemplate.findById(Mockito.eq(id),Mockito.eq(ReceiveSummary.class),Mockito.eq("receiving-summary"))).thenReturn(receiveSummary);
        Mockito.when(mongoTemplate.findById(Mockito.eq(lineId),Mockito.eq(ReceivingLine.class),Mockito.eq("receive-line"))).thenReturn(receivingLine);
        Mockito.when(receiveSummaryLineValidator.validateControlType(receivingSummaryLineSearch)).thenReturn(true);
        Mockito.when(receiveSummaryLineValidator.validateBusinessStatUpdateSummary(receivingSummaryLineSearch)).thenReturn(true);
        Mockito.when(receiveSummaryLineValidator.validateVendorNumberUpdateSummary(receivingSummaryLineSearch, vendorNumber, countryCode)).thenReturn(true);
//        Assert.assertEquals(receiveSummaryServiceImpl.updateReceiveSummaryAndLine(receivingSummaryLineSearch, countryCode,vendorNumber).toString(),receivingSummaryLineSearch.toString());
    }
}




