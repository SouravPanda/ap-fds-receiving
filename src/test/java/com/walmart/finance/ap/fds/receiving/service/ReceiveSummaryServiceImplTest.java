package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.exception.ContentNotFoundException;
import com.walmart.finance.ap.fds.receiving.exception.InvalidValueException;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.integrations.InvoiceIntegrationService;
import com.walmart.finance.ap.fds.receiving.integrations.InvoiceResponseData;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.request.Meta;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.request.SorRoutingCtx;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryLineValidator;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryValidator;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@PrepareForTest(ReceiveSummaryServiceImpl.class)
@RunWith(PowerMockRunner.class)
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

    @Mock
    InvoiceIntegrationService invoiceIntegrationService;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void getReceiveSummaryHappyPathTest() {
        ReceiveSummary receiveSummary = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "4665267",
                8264, 18, 0, LocalDate.of(1996, 12, 12),
                LocalTime.of(18, 45, 21), 0, 7688, 1111,
                0, 0, 'H', 0.0, 1.0,
                'P', 2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, 0, 0, LocalDateTime.now(), 0,
                "JJJ", "yyyy", LocalDateTime.now(), "99"
                , 'K', "LLL",new Long(0));
        ReceiveSummary receiveSummaryAt = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "4665207",
                8064, 18, 0, LocalDate.of(1986, 12, 12), LocalTime.of(18, 45, 21),
                0, 9788, 1111,
                0, 0, 'H', 0.0, 1.0,  'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, 0, 0, LocalDateTime.now(), 0,
                "JJJ", "UU", LocalDateTime.now(), "99"
                , 'K', "IIL",new Long(0));

        List listOfContent = new ArrayList<ReceiveSummary>();
        listOfContent.add(receiveSummary);
        listOfContent.add(receiveSummaryAt);

        List<String> listOfReceiptNumbers = new ArrayList<>();
        listOfReceiptNumbers.add("99");
        listOfReceiptNumbers.add("89");

        List<String> listOfItemNumbers = new ArrayList<>();
        listOfItemNumbers.add("99");
        listOfItemNumbers.add("89");

        List<String> listOfUpcNumbers = new ArrayList<>();
        listOfItemNumbers.add("9");
        listOfItemNumbers.add("89");

        Query query = new Query();

        ReceivingSummaryResponse receivingSummaryResponse = new ReceivingSummaryResponse("7778", new Long(1122), 99, "776",
                3680, 0,
                LocalDate.of(1986, 12, 12), 'L', 78, "HH89", "77",
                9.0, 7.0,
                1L, 0,0);

        ReceivingSummaryResponse receivingSummaryResponseAt = new ReceivingSummaryResponse("7778", new Long(1122), 99,
                "776", 3680, 0,
                LocalDate.of(1986, 12, 12), 'L', 78, "998H", "77",
                9.0, 7.0,
                0L, 0,0);

        FreightResponse freightResponse = new FreightResponse("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "0", "0");
        FreightResponse freightResponseAt = new FreightResponse("46652|18048|8264|18|18|1995-10-17|18:45:21", "0", "0");

        List<FreightResponse> listOfFreight = new ArrayList<>();
        listOfFreight.add(freightResponse);
        listOfFreight.add(freightResponseAt);

        List<ReceivingSummaryResponse> content = new ArrayList<>();
        content.add(receivingSummaryResponse);
        content.add(receivingSummaryResponseAt);

        InvoiceResponseData invoiceResponseData = new InvoiceResponseData("656", "267", "000", "999",
                "777", "0", "998", "9986", "098");
        List<InvoiceResponseData> invoiceResponseDataList = new ArrayList<>();
        invoiceResponseDataList.add(new InvoiceResponseData("656", "267", "000", "999",
                "777", "0", "998", "9986", "098"));
        invoiceResponseDataList.add(new InvoiceResponseData("656", "267", "000", "99",
                "77", "0", "98", "9986", "098"));

        ReceivingLine receivingLine = new ReceivingLine("4665267|1804823|8264|18|18|1995-10-17|18:45:21|0", "JJJ", 0, 0,0, 0, 0.0, 0.0, "776", 0, 0, "444", 1, 1, 1, null,null, 2, null, 'W', "DB2", null, 2, null, 1, 0.0, null, null, null,null,new Long(0));
        List<ReceivingLine> listOfReceiveLines = new ArrayList<>();
        listOfReceiveLines.add(receivingLine);

        Query dynamicQuery = new Query();
        Criteria criteriaNew = Criteria.where("purchaseOrderNumber").is("999").and("receivingControlNumber").is("000").and("storeNumber")
                .is(998).and("departmentNumber").is(98);
        dynamicQuery.addCriteria(criteriaNew);

        Mockito.when(invoiceIntegrationService.getInvoice(Mockito.any())).thenReturn(invoiceResponseDataList);

        when(receivingSummaryResponseConverter.convert(Mockito.any(ReceiveSummary.class))).thenReturn(receivingSummaryResponse);

        when(mongoTemplate.count(query, ReceiveSummary.class)).thenReturn(2L);
        Query mockQuery = Mockito.mock(Query.class);

        when(mockQuery.limit(Mockito.anyInt())).thenReturn(mockQuery);
        when(mongoTemplate.find(Mockito.any(Query.class), Mockito.any(Class.class), Mockito.any())).thenReturn(listOfContent, listOfContent, listOfContent, listOfContent, listOfContent, listOfContent, listOfReceiveLines, listOfFreight);


        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(content);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.now());

        Assert.assertEquals(receiveSummaryServiceImpl.getReceiveSummary("US", "77", "8", listOfReceiptNumbers, "99",
                "99", "675", "987", "18", "0", "776"
                , "1980", "1988-12-12", "1990-11-11", listOfItemNumbers, listOfUpcNumbers).getData(), successMessage.getData().subList(0, 1));
    }

    @Test
    public void getReceiveSummaryElsePathTest(){

        ReceiveSummary receiveSummary = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "4665207",
                8064, 18, 0, LocalDate.of(1986, 12, 12), LocalTime.of(18, 45, 21),
                0, 9788, 1111,
                0, 0, 'H', 0.0, 1.0,  'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, 0, 0, LocalDateTime.now(), 0,
                "JJJ", "UU", LocalDateTime.now(), "99"
                , 'K', "IIL",new Long(0));

        List listOfContent = new ArrayList<ReceiveSummary>();
        listOfContent.add(receiveSummary);

        List<String> listOfReceiptNumbers = new ArrayList<>();
        listOfReceiptNumbers.add("99");
        listOfReceiptNumbers.add("89");

        List<String> listOfItemNumbers = new ArrayList<>();
        listOfItemNumbers.add("99");
        listOfItemNumbers.add("89");

        List<String> listOfUpcNumbers = new ArrayList<>();
        listOfItemNumbers.add("9");
        listOfItemNumbers.add("89");

        ReceivingSummaryResponse receivingSummaryResponse = new ReceivingSummaryResponse("7778", new Long(1122), 99, "776",
                3680, 0,
                LocalDate.of(1986, 12, 12), 'L', 78, "HH89", "77",
                9.0, 7.0,
                0L, 0,0);

        ReceivingSummaryResponse receivingSummaryResponseAt = new ReceivingSummaryResponse("7778", new Long(1122), 99,
                "776", 3680, 0,
                LocalDate.of(1986, 12, 12), 'L', 78, "998H", "77",
                9.0, 7.0,
                0L, 0,0);

        FreightResponse freightResponse = new FreightResponse("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "0", "0");
        FreightResponse freightResponseAt = new FreightResponse("46652|18048|8264|18|18|1995-10-17|18:45:21", "0", "0");

        List<FreightResponse> listOfFreight = new ArrayList<>();
        listOfFreight.add(freightResponse);
        listOfFreight.add(freightResponseAt);

        List<ReceivingSummaryResponse> content = new ArrayList<>();
        content.add(receivingSummaryResponse);
        content.add(receivingSummaryResponseAt);

        ReceivingLine receivingLine = new ReceivingLine("4665267|1804823|8264|18|18|1995-10-17|18:45:21|0", "JJJ", 0, 0,0, 0, 0.0, 0.0, "776", 0, 0, "444", 1, 1, 1, null,null, 2, null, 'W', "DB2", null, 2, null, 1, 0.0, null, null, null, null,new Long(0));
        ReceivingLine receivingLineAt = new ReceivingLine("4665267|1804823|8264|18|18|1995-10-17|18:45:21|1", "JJJ", 0, 0,0, 0, 0.0, 0.0, "776", 0, 0, "444", 1, 1, 1, null,null, 2, null, 'W', "DB2", null, 2, null, 1, 0.0, null, null, null, null,new Long(0));

        List<ReceivingLine> listOfReceiveLines = new ArrayList<>();
        listOfReceiveLines.add(receivingLine);
        listOfReceiveLines.add(receivingLineAt);

        Query dynamicQuery = new Query();
        Criteria criteriaNew = Criteria.where("purchaseOrderNumber").is("999").and("receivingControlNumber").is("000").and("storeNumber")
                .is(998).and("departmentNumber").is(98);
        dynamicQuery.addCriteria(criteriaNew);

        //Mockito.when(invoiceIntegrationService.getInvoice(Mockito.any())).thenReturn(invoiceResponseDataList);

        when(receivingSummaryResponseConverter.convert(Mockito.any(ReceiveSummary.class))).thenReturn(receivingSummaryResponse);

        when(mongoTemplate.count(dynamicQuery, ReceiveSummary.class)).thenReturn(2L);
        Query mockQuery = Mockito.mock(Query.class);

        when(mockQuery.limit(Mockito.anyInt())).thenReturn(mockQuery);
        when(mongoTemplate.find(Mockito.any(Query.class), Mockito.any(Class.class), Mockito.any())).thenReturn(listOfContent, listOfReceiveLines, listOfFreight);


        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(content);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.now());

        Assert.assertEquals(receiveSummaryServiceImpl.getReceiveSummary("US", null, "8", listOfReceiptNumbers, "99",
                "99", "675", "987", "18", "0", null
                , null, "1988-12-12", "1990-11-11", listOfItemNumbers, listOfUpcNumbers).getData(), successMessage.getData().subList(0, 1));
    }

    @Test
    public void getReceiveSummaryIfPurchaseOrderPathTest(){

        ReceiveSummary receiveSummary = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "4665207",
                8064, 18, 0, LocalDate.of(1986, 12, 12), LocalTime.of(18, 45, 21),
                0, 9788, 1111,
                0, 0, 'H', 0.0, 1.0,  'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, 0, 0, LocalDateTime.now(), 0,
                "JJJ", "UU", LocalDateTime.now(), "99"
                , 'K', "IIL",new Long(0));

        List listOfContent = new ArrayList<ReceiveSummary>();
        listOfContent.add(receiveSummary);

        List<String> listOfReceiptNumbers = new ArrayList<>();
        listOfReceiptNumbers.add("99");
        listOfReceiptNumbers.add("89");

        List<String> listOfItemNumbers = new ArrayList<>();
        listOfItemNumbers.add("99");
        listOfItemNumbers.add("89");

        List<String> listOfUpcNumbers = new ArrayList<>();
        listOfItemNumbers.add("9");
        listOfItemNumbers.add("89");

        ReceivingSummaryResponse receivingSummaryResponse = new ReceivingSummaryResponse("7778", new Long(1122), 99, "776",
                3680, 0,
                LocalDate.of(1986, 12, 12), 'L', 78, "HH89", "77",
                9.0, 7.0,
                0L, 0,0);

        ReceivingSummaryResponse receivingSummaryResponseAt = new ReceivingSummaryResponse("7778", new Long(1122), 99,
                "776", 3680, 0,
                LocalDate.of(1986, 12, 12), 'L', 78, "998H", "77",
                9.0, 7.0,
                0L, 0,0);

        FreightResponse freightResponse = new FreightResponse("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "0", "0");
        FreightResponse freightResponseAt = new FreightResponse("46652|18048|8264|18|18|1995-10-17|18:45:21", "0", "0");

        List<FreightResponse> listOfFreight = new ArrayList<>();
        listOfFreight.add(freightResponse);
        listOfFreight.add(freightResponseAt);

        List<ReceivingSummaryResponse> content = new ArrayList<>();
        content.add(receivingSummaryResponse);
        content.add(receivingSummaryResponseAt);

        ReceivingLine receivingLine = new ReceivingLine("4665267|1804823|8264|18|18|1995-10-17|18:45:21|0", "JJJ", 0, 0,0, 0, 0.0, 0.0, "776", 0, 0, "444", 1, 1, 1, null,null, 2, null, 'W', "DB2", null, 2, null, 1, 0.0, null, null, null, null,new Long(0));
        List<ReceivingLine> listOfReceiveLines = new ArrayList<>();
        listOfReceiveLines.add(receivingLine);

        Query dynamicQuery = new Query();
        Criteria criteriaNew = Criteria.where("purchaseOrderNumber").is("999").and("receivingControlNumber").is("000").and("storeNumber")
                .is(998).and("departmentNumber").is(98);
        dynamicQuery.addCriteria(criteriaNew);

        List<InvoiceResponseData> invoiceResponseDataList = new ArrayList<>();
        Mockito.when(invoiceIntegrationService.getInvoice(Mockito.any())).thenReturn(invoiceResponseDataList);

        when(receivingSummaryResponseConverter.convert(Mockito.any(ReceiveSummary.class))).thenReturn(receivingSummaryResponse);

        when(mongoTemplate.count(dynamicQuery, ReceiveSummary.class)).thenReturn(2L);
        Query mockQuery = Mockito.mock(Query.class);

        when(mockQuery.limit(Mockito.anyInt())).thenReturn(mockQuery);
        when(mongoTemplate.find(Mockito.any(Query.class), Mockito.any(Class.class), Mockito.any())).thenReturn(listOfContent, listOfReceiveLines, listOfFreight);


        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(content);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.now());

        Assert.assertEquals(receiveSummaryServiceImpl.getReceiveSummary("US", "77", "8", listOfReceiptNumbers, "99",
                null, "675", "987", "18", "0", null
                , null, "1988-12-12", "1990-11-11", listOfItemNumbers, listOfUpcNumbers).getData(), successMessage.getData().subList(0, 1));
    }

    @Test(expected = NotFoundException.class)
    public void getReceiveSummaryException() {
        receiveSummaryServiceImpl.getReceiveSummary(null, null, null, Mockito.anyList(),
                null, null, null, null, null, null,
                null, null, null, null, Mockito.anyList(), Mockito.anyList());
    }

    @Test(expected = BadRequestException.class)
    public void getReceiveSummaryNumbnerFormateException() {
        receiveSummaryServiceImpl.getReceiveSummary(null, null, null, null,
                null, null, "23qa", null, null, null,
                null, null, null, null, null, null);
    }

    @Test(expected = BadRequestException.class)
    public void getReceiveSummaryDateInvaidException() {
        receiveSummaryServiceImpl.getReceiveSummary(null, null, null, null,
                null, null, null, null, null, null,
                null, null, "asdasd", "adssa", null, null);
    }

    /**   Update service Unit tests **/

    @Test
    public void updateReceiveSummaryHappyPathTest() {

        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);

        ReceiveSummary receiveSummary = new ReceiveSummary("998|888|1|0", "888",
                6565, 18, 0, LocalDate.of(1995, 10, 16), LocalTime.of(18, 30, 00),
                0, 122663, 1111,
                0, 0, 'H', 0.0, 1.0,  'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.of(1995, 10, 16),
                LocalDate.of(1995, 10, 16), 9.0, 7, 0, 0, (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), 0,
                "999997", "yyyy", (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), "99"
                , 'K', "LLL",new Long(0));
        ReceivingSummaryRequest receivingSummaryRequest = new ReceivingSummaryRequest("888", "998", LocalDate.of(2018, 10, 10),
                1, "A", meta);
        String countryCode = "US";
        String id = "998|888|1|0";
        List<ReceivingSummaryRequest> responseList = new ArrayList<>();

        when(mongoTemplate.findById((Mockito.anyString()), Mockito.any(Class.class), Mockito.any())).thenReturn(receiveSummary);
        Mockito.when(receiveSummaryValidator.validateBusinessStatUpdateSummary(receivingSummaryRequest)).thenReturn(true);

        responseList.add(receivingSummaryRequest);

        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(responseList);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.of(2018, 10, 10, 0, 40, 0));

        Assert.assertEquals(receiveSummaryServiceImpl.updateReceiveSummary(receivingSummaryRequest, countryCode).getData(), successMessage.getData());
    }

    @Test
    public void updateReceiveSummaryElsePathTest() {

        ReceiveSummary receiveSummary = new ReceiveSummary("998|888|1|0", "888",
                6565, 18, 0, LocalDate.of(1995, 10, 16), LocalTime.of(18, 30, 00),
                0, 122663, 1111,
                0, 0, 'H', 0.0, 1.0,  'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.of(1995, 10, 16),
                LocalDate.of(1995, 10, 16), 9.0, 7, 0, 0, (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), 0,
                "999997", "yyyy", (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), "99"
                , 'K', "LLL",new Long(0));

        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(30);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("P");
        meta.setSorRoutingCtx(sorRoutingCtx);

        ReceivingSummaryRequest receivingSummaryRequest = new ReceivingSummaryRequest("888", "998", LocalDate.of(2018, 10, 10),
                1, "A", meta);

        Meta mockString = Mockito.mock(Meta.class);
        when(mockString.getUnitofWorkid()).thenReturn("11");

        List<ReceivingSummaryRequest> responseList = new ArrayList<>();

        String countryCode = "US";

        when(mongoTemplate.findById((Mockito.anyString()), Mockito.any(Class.class), Mockito.any())).thenReturn(receiveSummary);
        Mockito.when(receiveSummaryValidator.validateBusinessStatUpdateSummary(receivingSummaryRequest)).thenReturn(true);

        responseList.add(receivingSummaryRequest);

        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(responseList);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.of(2018, 10, 10, 0, 40, 0));

        Assert.assertEquals(receiveSummaryServiceImpl.updateReceiveSummary(receivingSummaryRequest, countryCode).getData(), successMessage.getData());

    }

    @Test(expected = InvalidValueException.class)
    public void updateReceiveSummaryNegativeBusinessStatusCodeTest() {

        ReceiveSummary receiveSummary = new ReceiveSummary("998|888|1|0", "888",
                6565, 18, 0, LocalDate.of(1995, 10, 16), LocalTime.of(18, 30, 00),
                0, 122663, 1111,
                0, 0, 'H', 0.0, 1.0,  'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.of(1995, 10, 16),
                LocalDate.of(1995, 10, 16), 9.0, 7, 0, 0, (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), 0,
                "999997", "yyyy", (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), "99"
                , 'K', "LLL",new Long(0));

        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(30);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("P");
        meta.setSorRoutingCtx(sorRoutingCtx);

        ReceivingSummaryRequest receivingSummaryRequest = new ReceivingSummaryRequest("888", "998", LocalDate.of(2018, 10, 10),
                1, "P", meta);

        Meta mockString = Mockito.mock(Meta.class);
        when(mockString.getUnitofWorkid()).thenReturn("11");

        when(mongoTemplate.findById((Mockito.anyString()), Mockito.any(Class.class), Mockito.any())).thenReturn(receiveSummary);

        Mockito.when(receiveSummaryValidator.validateBusinessStatUpdateSummary(receivingSummaryRequest)).thenReturn(false);
        receiveSummaryServiceImpl.updateReceiveSummary(receivingSummaryRequest, "US");

    }

    @Test(expected = ContentNotFoundException.class)
    public void updateReceiveSummaryContentNotFoundTest() {

        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(30);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("P");
        meta.setSorRoutingCtx(sorRoutingCtx);

        ReceivingSummaryRequest receivingSummaryRequest = new ReceivingSummaryRequest("888", "998", LocalDate.of(2018, 10, 10),
                1, "A", meta);

        String id = null;

        Meta mockString = Mockito.mock(Meta.class);
        when(mockString.getUnitofWorkid()).thenReturn("11");

        Mockito.when(receiveSummaryValidator.validateBusinessStatUpdateSummary(receivingSummaryRequest)).thenReturn(true);

        receiveSummaryServiceImpl.updateReceiveSummary(receivingSummaryRequest, "US");
    }

    @Test
    public void updateReceiveSummaryLineHappyPathTest() {

        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);

        String countryCode = "US";
        ReceiveSummary receiveSummary = new ReceiveSummary("9|8|1|0", "8",
                6565, 18, 0, LocalDate.of(1995, 10, 16), LocalTime.of(18, 30, 00),
                0, 122663, 1111,
                0, 0, 'H', 0.0, 1.0,  'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.of(1995, 10, 16),
                LocalDate.of(1995, 10, 16), 9.0, 7, 0, 0, (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), 0,
                "999997", "yyyy", (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), "9"
                , 'K', "LLL",new Long(0));

        ReceivingLine receivingLine = new ReceivingLine("9|8|1|0|1", "8",
                0, 3777, 94493, 0, 0.0, 0.0, "9",
                89, 12, "1122", 99, 8264, 18,
                LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 1,
                LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 0, LocalDate.now(),
                0, 1.9, "LL", 9, "OO", null,new Long(0));

        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                1, "9", meta);

        Mockito.when(receiveSummaryLineValidator.validateBusinessStatUpdateSummary(receivingSummaryLineRequest)).thenReturn(true);
        Mockito.when(receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest)).thenReturn(true);

        when(mongoTemplate.findById((Mockito.anyString()), Mockito.any(Class.class), Mockito.any())).thenReturn(receiveSummary, receivingLine);

        List<ReceivingSummaryLineRequest> responseList = new ArrayList<>();
        responseList.add(receivingSummaryLineRequest);

        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(responseList);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.of(2018, 10, 10, 0, 40, 0));

//        Assert.assertEquals(receiveSummaryServiceImpl.updateReceiveSummaryAndLine(receivingSummaryLineRequest, countryCode).getData(), successMessage.getData());
    }

    @Test(expected = ContentNotFoundException.class)
    public void updateReceiveSummaryLineContentNotFoundTest() {

        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(30);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("P");
        meta.setSorRoutingCtx(sorRoutingCtx);

        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                1, "9", meta);

        Meta mockString = Mockito.mock(Meta.class);
        when(mockString.getUnitofWorkid()).thenReturn("11");

        Mockito.when(receiveSummaryLineValidator.validateBusinessStatUpdateSummary(receivingSummaryLineRequest)).thenReturn(true);
        Mockito.when(receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest)).thenReturn(true);

        receiveSummaryServiceImpl.updateReceiveSummaryAndLine(receivingSummaryLineRequest, "US");

    }

    @Test
    public void updateReceiveSummaryLineElsePathTest() {

        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);

        String countryCode = "US";
        ReceiveSummary receiveSummary = new ReceiveSummary("9|8|1|0", "8",
                6565, 18, 0, LocalDate.of(1995, 10, 16), LocalTime.of(18, 30, 00),
                0, 122663, 1111,
                0, 0, 'H', 0.0, 1.0,  'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.of(1995, 10, 16),
                LocalDate.of(1995, 10, 16), 9.0, 7, 0, 0, (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), 0,
                "999997", "yyyy", (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), "9"
                , 'K', "LLL",new Long(0));

        ReceivingLine receivingLine = new ReceivingLine("9|8|1|0|1", "8",
                0, 3777, 94493, 0, 0.0, 0.0, "9",
                89, 12, "1122", 99, 8264, 18,
                LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 1,
                LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 0, LocalDate.now(),
                0, 1.9, "LL", 9, "OO", null,new Long(0));

        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                null, "9", meta);

        Meta mockString = Mockito.mock(Meta.class);
        when(mockString.getUnitofWorkid()).thenReturn("11");

        Mockito.when(receiveSummaryLineValidator.validateBusinessStatUpdateSummary(receivingSummaryLineRequest)).thenReturn(true);
        Mockito.when(receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest)).thenReturn(true);

        when(mongoTemplate.findById((Mockito.anyString()), Mockito.any(Class.class), Mockito.any())).thenReturn(receiveSummary, receivingLine);

        List<ReceivingSummaryLineRequest> responseList = new ArrayList<>();
        responseList.add(receivingSummaryLineRequest);

        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(responseList);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.of(2018, 10, 10, 0, 40, 0));

       // Assert.assertEquals(receiveSummaryServiceImpl.updateReceiveSummaryAndLine(receivingSummaryLineRequest, countryCode).getData(), successMessage.getData());
    }

    @Test(expected = InvalidValueException.class)
    public void updateReceiveSummaryLineNegativeBusinessStatCodeTest() {

        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);

        String countryCode = "US";
        ReceiveSummary receiveSummary = new ReceiveSummary("9|8|1|0", "8",
                6565, 18, 0, LocalDate.of(1995, 10, 16), LocalTime.of(18, 30, 00),
                0, 122663, 1111,
                0, 0, 'H', 0.0, 1.0,  'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.of(1995, 10, 16),
                LocalDate.of(1995, 10, 16), 9.0, 7, 0, 0, (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), 0,
                "999997", "yyyy", (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), "9"
                , 'K', "LLL",new Long(0));

        ReceivingLine receivingLine = new ReceivingLine("9|8|1|0|1", "8",
                0, 3777, 94493, 0, 0.0, 0.0, "9",
                89, 12, "1122", 99, 8264, 18,
                LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 1,
                LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 0, LocalDate.now(),
                0, 1.9, "LL", 10, "OO", null,new Long(0));

        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "P",
                null, "10", meta);

        Meta mockString = Mockito.mock(Meta.class);
        when(mockString.getUnitofWorkid()).thenReturn("11");

        Mockito.when(receiveSummaryLineValidator.validateBusinessStatUpdateSummary(receivingSummaryLineRequest)).thenReturn(false);
        Mockito.when(receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest)).thenReturn(false);

        when(mongoTemplate.findById((Mockito.anyString()), Mockito.any(Class.class), Mockito.any())).thenReturn(receiveSummary, receivingLine);

        List<ReceivingSummaryLineRequest> responseList = new ArrayList<>();
        responseList.add(receivingSummaryLineRequest);

        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(responseList);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.of(2018, 10, 10, 0, 40, 0));

        Mockito.when(receiveSummaryLineValidator.validateBusinessStatUpdateSummary(receivingSummaryLineRequest)).thenReturn(false);

        receiveSummaryServiceImpl.updateReceiveSummaryAndLine(receivingSummaryLineRequest, "US");

    }

    @Test(expected = InvalidValueException.class)
    public void updateReceiveSummaryLineNegativeInventoryMatchStatusTest() {

        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);

        String countryCode = "US";
        ReceiveSummary receiveSummary = new ReceiveSummary("9|8|1|0", "8",
                6565, 18, 0, LocalDate.of(1995, 10, 16), LocalTime.of(18, 30, 00),
                0, 122663, 1111,
                0, 0, 'H', 0.0, 1.0,  'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.of(1995, 10, 16),
                LocalDate.of(1995, 10, 16), 9.0, 7, 0, 0, (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), 0,
                "999997", "yyyy", (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), "9"
                , 'K', "LLL",new Long(0));

        ReceivingLine receivingLine = new ReceivingLine("9|8|1|0|1", "8",
                0, 3777, 94493, 0, 0.0, 0.0, "9",
                89, 12, "1122", 99, 8264, 18,
                LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 1,
                LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 0, LocalDate.now(),
                0, 1.9, "LL", 10, "OO", null,new Long(0));

        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                null, "10", meta);

        Meta mockString = Mockito.mock(Meta.class);
        when(mockString.getUnitofWorkid()).thenReturn("11");

        Mockito.when(receiveSummaryLineValidator.validateBusinessStatUpdateSummary(receivingSummaryLineRequest)).thenReturn(false);
        Mockito.when(receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest)).thenReturn(false);

        when(mongoTemplate.findById((Mockito.anyString()), Mockito.any(Class.class), Mockito.any())).thenReturn(receiveSummary, receivingLine);

        List<ReceivingSummaryLineRequest> responseList = new ArrayList<>();
        responseList.add(receivingSummaryLineRequest);

        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(responseList);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.of(2018, 10, 10, 0, 40, 0));

        Mockito.when(receiveSummaryLineValidator.validateBusinessStatUpdateSummary(receivingSummaryLineRequest)).thenReturn(true);
        Mockito.when(receiveSummaryLineValidator.validateInventoryMatchStatus(receivingSummaryLineRequest)).thenReturn(false);

        receiveSummaryServiceImpl.updateReceiveSummaryAndLine(receivingSummaryLineRequest, "US");

    }

}




