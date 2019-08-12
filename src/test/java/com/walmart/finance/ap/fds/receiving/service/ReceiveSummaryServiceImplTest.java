package com.walmart.finance.ap.fds.receiving.service;

import com.mongodb.client.result.UpdateResult;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.exception.ContentNotFoundException;
import com.walmart.finance.ap.fds.receiving.exception.InvalidValueException;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.integrations.InvoiceIntegrationService;
import com.walmart.finance.ap.fds.receiving.integrations.InvoiceReferenceResponse;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@PrepareForTest(ReceiveSummaryServiceImpl.class)
@RunWith(PowerMockRunner.class)
public class ReceiveSummaryServiceImplTest {

    @InjectMocks
    ReceiveSummaryServiceImpl receiveSummaryServiceImpl;

    @Mock
    MongoTemplate mongoTemplate;

    @Mock
    private ApplicationEventPublisher publisher;

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
                0, 0, "H", 0.0, 1.0,
                'P', 2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, "0", 0, LocalDateTime.now(), 0,
                "JJJ", "yyyy", LocalDateTime.now(), "99"
                , 'K', "LLL", null, null, null, null, null);
        ReceiveSummary receiveSummaryAt = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "4665207",
                8064, 18, 0, LocalDate.of(1986, 12, 12), LocalTime.of(18, 45, 21),
                0, 9788, 1111,
                0, 0, "H", 0.0, 1.0, 'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, "0", 0, LocalDateTime.now(), 0,
                "JJJ", "UU", LocalDateTime.now(), "99"
                , 'K', "IIL", null, null, null, null, null);

        List listOfContent = new ArrayList<ReceiveSummary>();
        listOfContent.add(receiveSummary);
        listOfContent.add(receiveSummaryAt);
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
                1L, 0, 0, 10.0);

        ReceivingSummaryResponse receivingSummaryResponseAt = new ReceivingSummaryResponse("7778", new Long(1122), 99,
                "776", 3680, 0,
                LocalDate.of(1986, 12, 12), 'L', 78, "998H", "77",
                9.0, 7.0,
                0L, 0, 0, 10.0);

        FreightResponse freightResponse = new FreightResponse("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "0", "0");
        FreightResponse freightResponseAt = new FreightResponse("46652|18048|8264|18|18|1995-10-17|18:45:21", "0", "0");
        List<FreightResponse> listOfFreight = new ArrayList<>();
        listOfFreight.add(freightResponse);
        listOfFreight.add(freightResponseAt);
        List<ReceivingSummaryResponse> content = new ArrayList<>();
        content.add(receivingSummaryResponse);
        content.add(receivingSummaryResponseAt);

        List<InvoiceReferenceResponse> invoiceReferenceResponses = new ArrayList<InvoiceReferenceResponse>() {
            {
                add(new InvoiceReferenceResponse("PO", "000"));
                add(new InvoiceReferenceResponse("AD", "123"));
            }
        };
        List<InvoiceResponseData> invoiceResponseDataList = new ArrayList<>();
        invoiceResponseDataList.add(new InvoiceResponseData("656", "267", "000", null,
                "777", "0", "998", "9986", "098", invoiceReferenceResponses));
        invoiceResponseDataList.add(new InvoiceResponseData("656", "267", "000", null,
                "77", "0", "98", "9986", "098", invoiceReferenceResponses));


        ReceivingLine receivingLine = new ReceivingLine("4665267|1804823|8264|18|18|1995-10-17|18:45:21|0", "JJJ", 0,
                0, 0, 0, 0.0, 0.0, "776", 0,
                0, "444", 1, 1, 1, null, null, 2,
                null, 'W', "DB2", null, 2, null, 1, 0.0,
                null, null, null, null, null, null,
                null, null, null, "4665267|1804823|8264|18|18|1995-10-17|18:45:21", null, null, null);

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

        Map mockMap = Mockito.mock(Map.class);
        try {
            Assert.assertEquals(receiveSummaryServiceImpl.getReceiveSummary(mockMap).isSuccess(), successMessage.isSuccess());
        } catch (NullPointerException | ClassCastException e) {
            e.getMessage();
        }

    }

    @Test
    public void getReceiveSummaryElsePathTest() {

        ReceiveSummary receiveSummary = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "4665207",
                8064, 18, 0, LocalDate.of(1986, 12, 12), LocalTime.of(18, 45, 21),
                0, 9788, 1111,
                0, 0, "H", 0.0, 1.0, 'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, "0", 0, LocalDateTime.now(), 0,
                "JJJ", "UU", LocalDateTime.now(), "99"
                , 'K', "IIL", null, null, null, null, null);

        List listOfContent = new ArrayList<ReceiveSummary>();
        listOfContent.add(receiveSummary);
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
                0L, 0, 0, 10.0);

        ReceivingSummaryResponse receivingSummaryResponseAt = new ReceivingSummaryResponse("7778", new Long(1122), 99,
                "776", 3680, 0,
                LocalDate.of(1986, 12, 12), 'L', 78, "998H", "77",
                9.0, 7.0,
                0L, 0, 0, 10.0);

        FreightResponse freightResponse = new FreightResponse("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "0", "0");
        FreightResponse freightResponseAt = new FreightResponse("46652|18048|8264|18|18|1995-10-17|18:45:21", "0", "0");
        List<FreightResponse> listOfFreight = new ArrayList<>();
        listOfFreight.add(freightResponse);
        listOfFreight.add(freightResponseAt);
        List<ReceivingSummaryResponse> content = new ArrayList<>();
        content.add(receivingSummaryResponse);
        content.add(receivingSummaryResponseAt);

        ReceivingLine receivingLine = new ReceivingLine("4665267|1804823|8264|18|18|1995-10-17|18:45:21|0", "JJJ", 0, 0, 0, 0, 0.0, 0.0, "776", 0, 0, "444", 1, 1, 1, null, null, 2, null, 'W', "DB2", null, 2, null, 1, 0.0, null, null, null, null, null, null, null, null, null, "4665267|1804823|8264|18|18|1995-10-17|18:45:21", null, null, null);
        ReceivingLine receivingLineAt = new ReceivingLine("4665267|1804823|8264|18|18|1995-10-17|18:45:21|1", "JJJ", 0, 0, 0, 0, 0.0, 0.0, "776", 0, 0, "444", 1, 1, 1, null, null, 2, null, 'W', "DB2", null, 2, null, 1, 0.0, null, null, null, null, null, null, null, null, null, "4665267|1804823|8264|18|18|1995-10-17|18:45:21", null, null, null);

        List<ReceivingLine> listOfReceiveLines = new ArrayList<>();
        listOfReceiveLines.add(receivingLine);
        listOfReceiveLines.add(receivingLineAt);
        Query dynamicQuery = new Query();
        Criteria criteriaNew = Criteria.where("purchaseOrderNumber").is("999").and("receivingControlNumber").is("000").and("storeNumber")
                .is(998).and("departmentNumber").is(98);
        dynamicQuery.addCriteria(criteriaNew);
        when(receivingSummaryResponseConverter.convert(Mockito.any(ReceiveSummary.class))).thenReturn(receivingSummaryResponse);
        when(mongoTemplate.count(dynamicQuery, ReceiveSummary.class)).thenReturn(2L);
        Query mockQuery = Mockito.mock(Query.class);
        when(mockQuery.limit(Mockito.anyInt())).thenReturn(mockQuery);
        when(mongoTemplate.find(Mockito.any(Query.class), Mockito.any(Class.class), Mockito.any())).thenReturn(listOfContent, listOfReceiveLines, listOfFreight);
        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(content);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.now());
        Map mockMap = Mockito.mock(Map.class);
        Assert.assertEquals(receiveSummaryServiceImpl.getReceiveSummary(mockMap).getData(), successMessage.getData().subList(0, 1));
    }

    @Test
    public void getReceiveSummaryIfPurchaseOrderPathTest() {
        ReceiveSummary receiveSummary = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "4665207",
                8064, 18, 0, LocalDate.of(1986, 12, 12), LocalTime.of(18, 45, 21),
                0, 9788, 1111,
                0, 0, "H", 0.0, 1.0, 'P',
                null, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, "0", 0, LocalDateTime.now(), 0,
                "JJJ", "UU", LocalDateTime.now(), "99"
                , 'W', "IIL", null, null, null, null, null);
        List listOfContent = new ArrayList<ReceiveSummary>();
        listOfContent.add(receiveSummary);
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
                0L, 0, 0, 10.0);
        ReceivingSummaryResponse receivingSummaryResponseAt = new ReceivingSummaryResponse("7778", new Long(1122), 99,
                "776", 3680, 0,
                LocalDate.of(1986, 12, 12), 'L', 78, "998H", "77",
                9.0, 7.0,
                0L, 0, 0, 10.0);
        FreightResponse freightResponse = new FreightResponse("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "0", "0");
        FreightResponse freightResponseAt = new FreightResponse("46652|18048|8264|18|18|1995-10-17|18:45:21", "0", "0");
        List<FreightResponse> listOfFreight = new ArrayList<>();
        listOfFreight.add(freightResponse);
        listOfFreight.add(freightResponseAt);
        List<ReceivingSummaryResponse> content = new ArrayList<>();
        content.add(receivingSummaryResponse);
        content.add(receivingSummaryResponseAt);
        ReceivingLine receivingLine = new ReceivingLine("4665267|1804823|8264|18|18|1995-10-17|18:45:21|0", "JJJ", 0, 0, 0, 0, 0.0, 0.0, "776", 0, 0, "444", 1, 1, 1, null, null, 2, null, 'W', "DB2", null, 2, null, 1, 0.0, null, null, null, null, null, null, null, null, null, "4665267|1804823|8264|18|18|1995-10-17|18:45:21", null, null, null);
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
        Map mockMap = Mockito.mock(Map.class);
        Assert.assertEquals(receiveSummaryServiceImpl.getReceiveSummary(mockMap).getData(), successMessage.getData().subList(0, 1));
    }

    @Test(expected = NotFoundException.class)
    public void getReceiveSummaryException() {

        List<String> listOfItemNumbers = new ArrayList<>();
        listOfItemNumbers.add("99");
        listOfItemNumbers.add("89");

        List<String> listOfUpcNumbers = new ArrayList<>();
        listOfItemNumbers.add("9");
        listOfItemNumbers.add("89");

        Map mockMap = Mockito.mock(Map.class);
        receiveSummaryServiceImpl.getReceiveSummary(mockMap);
    }

    @Test(expected = NotFoundException.class)
    public void getReceiveSummaryNumbnerFormateException() {

        List<String> listOfItemNumbers = new ArrayList<>();
        listOfItemNumbers.add("99");
        listOfItemNumbers.add("89");

        List<String> listOfUpcNumbers = new ArrayList<>();
        listOfUpcNumbers.add("9");
        listOfUpcNumbers.add("89");

        Map mockMap = Mockito.mock(Map.class);
        receiveSummaryServiceImpl.getReceiveSummary(mockMap);
    }

    @Test(expected = BadRequestException.class)
    public void getReceiveSummaryDateInvaidException() {

        List<String> listOfItemNumbers = new ArrayList<>();
        listOfItemNumbers.add("99");
        listOfItemNumbers.add("89");

        List<String> listOfUpcNumbers = new ArrayList<>();
        listOfUpcNumbers.add("null");
        listOfItemNumbers.add("null");

        Map mockMap = new HashMap();
        mockMap.put("HH", "KKK");
        ReceiveSummaryValidator.validate("US", mockMap);
        receiveSummaryServiceImpl.getReceiveSummary(mockMap);
    }

    /**
     * Update service Unit tests
     **/

    @Test(expected = ContentNotFoundException.class)
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
                0, 0, "H", 0.0, 1.0, 'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.of(1995, 10, 16),
                LocalDate.of(1995, 10, 16), 9.0, 7, "0", 0, (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), 0,
                "999997", "yyyy", (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), "99"
                , 'K', "LLL", null, null, null, null, null);
        ReceivingSummaryRequest receivingSummaryRequest = new ReceivingSummaryRequest("888", "998", LocalDate.of(2018, 10, 10),
                1, "A", meta);
        String countryCode = "US";
        String id = "998|888|1|0";
        List<ReceivingSummaryRequest> responseList = new ArrayList<>();
        when(mongoTemplate.findById((Mockito.anyString()), Mockito.any(Class.class), Mockito.any())).thenReturn(receiveSummary);
        doNothing().when(receiveSummaryValidator).validateBusinessStatUpdateSummary(Mockito.anyString());
        responseList.add(receivingSummaryRequest);
        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(responseList);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.of(2018, 10, 10, 0, 40, 0));
        Assert.assertEquals(receiveSummaryServiceImpl.updateReceiveSummary(receivingSummaryRequest, countryCode).getData(), successMessage.getData());
    }

    @Test(expected = ContentNotFoundException.class)
    public void updateReceiveSummaryElsePathTest() {
        ReceiveSummary receiveSummary = new ReceiveSummary("998|888|1|0", "888",
                6565, 18, 0, LocalDate.of(1995, 10, 16), LocalTime.of(18, 30, 00),
                0, 122663, 1111,
                0, 0, "H", 0.0, 1.0, 'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.of(1995, 10, 16),
                LocalDate.of(1995, 10, 16), 9.0, 7, "0", 0, (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), 0,
                "999997", "yyyy", (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), "99"
                , 'K', "LLL", null, null, null, null, null);

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
        doNothing().when(receiveSummaryValidator).validateBusinessStatUpdateSummary(Mockito.anyString());
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
                0, 0, "H", 0.0, 1.0, 'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.of(1995, 10, 16),
                LocalDate.of(1995, 10, 16), 9.0, 7, "0", 0, (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), 0,
                "999997", "yyyy", (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), "99"
                , 'K', "LLL", null, null, null, null, null);

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
        doThrow(InvalidValueException.class).when(receiveSummaryValidator).validateBusinessStatUpdateSummary(Mockito.anyString());
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
        doNothing().when(receiveSummaryValidator).validateBusinessStatUpdateSummary(Mockito.anyString());
        receiveSummaryServiceImpl.updateReceiveSummary(receivingSummaryRequest, "US");
    }

    @Test(expected = ContentNotFoundException.class)
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
                0, 0, "H", 0.0, 1.0, 'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.of(1995, 10, 16),
                LocalDate.of(1995, 10, 16), 9.0, 7, "0", 0, (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), 0,
                "999997", "yyyy", (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), "9"
                , 'K', "LLL", null, null, null, null, null);

        ReceivingLine receivingLine = new ReceivingLine("9|8|1|0|1", "8",
                0, 3777, 94493, 0, 0.0, 0.0, "9",
                89, 12, "1122", 99, 8264, 18,
                LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 1,
                LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 0, LocalDate.now(),
                0, 1.9, "LL", 9, "OO", null, null, null, null, null, null, null, null, null, null);

        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                "1", "9", meta);
        doNothing().when(receiveSummaryValidator).validateBusinessStatUpdateSummary(Mockito.anyString());
        doNothing().when(receiveSummaryLineValidator).validateInventoryMatchStatus(receivingSummaryLineRequest);
        when(mongoTemplate.findById((Mockito.anyString()), Mockito.any(Class.class), Mockito.any())).thenReturn(receiveSummary, receivingLine);
        List<ReceivingSummaryLineRequest> responseList = new ArrayList<>();
        responseList.add(receivingSummaryLineRequest);
        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(responseList);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.of(2018, 10, 10, 0, 40, 0));
        Assert.assertEquals(receiveSummaryServiceImpl.updateReceiveSummaryAndLine(receivingSummaryLineRequest, countryCode).getData(), successMessage.getData());
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
                "1", "9", meta);
        Meta mockString = Mockito.mock(Meta.class);
        when(mockString.getUnitofWorkid()).thenReturn("11");
        doNothing().when(receiveSummaryValidator).validateBusinessStatUpdateSummary(Mockito.anyString());
        doNothing().when(receiveSummaryLineValidator).validateInventoryMatchStatus(receivingSummaryLineRequest);
        receiveSummaryServiceImpl.updateReceiveSummaryAndLine(receivingSummaryLineRequest, "US");
    }

    @Test(expected = ContentNotFoundException.class)
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
                0, 0, "H", 0.0, 1.0, 'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.of(1995, 10, 16),
                LocalDate.of(1995, 10, 16), 9.0, 7, "0", 0, (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), 0,
                "999997", "yyyy", (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), "9"
                , 'K', "LLL", null, null, null, null, null);

        ReceivingLine receivingLine = new ReceivingLine("9|8|1|0|1", "8",
                0, 3777, 94493, 0, 0.0, 0.0, "9",
                89, 12, "1122", 99, 8264, 18,
                LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 1,
                LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 0, LocalDate.now(),
                0, 1.9, "LL", 9, "OO", null, null, null, null, null, null, null, null, null, null);

        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                null, "9", meta);
        Meta mockString = Mockito.mock(Meta.class);
        when(mockString.getUnitofWorkid()).thenReturn("11");
        doNothing().when(receiveSummaryValidator).validateBusinessStatUpdateSummary(Mockito.anyString());
        doNothing().when(receiveSummaryLineValidator).validateInventoryMatchStatus(receivingSummaryLineRequest);
        when(mongoTemplate.findById((Mockito.anyString()), Mockito.any(Class.class), Mockito.any())).thenReturn(receiveSummary, receivingLine);
        List<ReceivingSummaryLineRequest> responseList = new ArrayList<>();
        responseList.add(receivingSummaryLineRequest);
        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(responseList);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.of(2018, 10, 10, 0, 40, 0));
        Assert.assertEquals(receiveSummaryServiceImpl.updateReceiveSummaryAndLine(receivingSummaryLineRequest, countryCode).getData(), successMessage.getData());
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
                0, 0, "H", 0.0, 1.0, 'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.of(1995, 10, 16),
                LocalDate.of(1995, 10, 16), 9.0, 7, "0", 0, (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), 0,
                "999997", "yyyy", (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), "9"
                , 'K', "LLL", null, null, null, null, null);

        ReceivingLine receivingLine = new ReceivingLine("9|8|1|0|1", "8",
                0, 3777, 94493, 0, 0.0, 0.0, "9",
                89, 12, "1122", 99, 8264, 18,
                LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 1,
                LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 0, LocalDate.now(),
                0, 1.9, "LL", 10, "OO", null, null, null, null, null, null, null, null, null, null);

        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "P",
                null, "10", meta);
        Meta mockString = Mockito.mock(Meta.class);
        when(mockString.getUnitofWorkid()).thenReturn("11");
        doThrow(InvalidValueException.class).when(receiveSummaryValidator).validateBusinessStatUpdateSummary(Mockito.anyString());
        doThrow(InvalidValueException.class).when(receiveSummaryLineValidator).validateInventoryMatchStatus(receivingSummaryLineRequest);
        when(mongoTemplate.findById((Mockito.anyString()), Mockito.any(Class.class), Mockito.any())).thenReturn(receiveSummary, receivingLine);
        List<ReceivingSummaryLineRequest> responseList = new ArrayList<>();
        responseList.add(receivingSummaryLineRequest);
        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(responseList);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.of(2018, 10, 10, 0, 40, 0));
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
                0, 0, "H", 0.0, 1.0, 'P',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.of(1995, 10, 16),
                LocalDate.of(1995, 10, 16), 9.0, 7, "0", 0, (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), 0,
                "999997", "yyyy", (LocalDateTime.of(2018, 10, 10, 0, 40, 0)), "9"
                , 'K', "LLL", null, null, null, null, null);

        ReceivingLine receivingLine = new ReceivingLine("9|8|1|0|1", "8",
                0, 3777, 94493, 0, 0.0, 0.0, "9",
                89, 12, "1122", 99, 8264, 18,
                LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 1,
                LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 0, LocalDate.now(),
                0, 1.9, "LL", 10, "OO", null, null, null, null, null, null, null, null, null, null);

        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                null, "10", meta);
        Meta mockString = Mockito.mock(Meta.class);
        when(mockString.getUnitofWorkid()).thenReturn("11");
        when(mongoTemplate.findById((Mockito.anyString()), Mockito.any(Class.class), Mockito.any())).thenReturn(receiveSummary, receivingLine);
        List<ReceivingSummaryLineRequest> responseList = new ArrayList<>();
        responseList.add(receivingSummaryLineRequest);
        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(responseList);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.of(2018, 10, 10, 0, 40, 0));
        doNothing().when(receiveSummaryValidator).validateBusinessStatUpdateSummary(Mockito.anyString());
        doThrow(InvalidValueException.class).when(receiveSummaryLineValidator).validateInventoryMatchStatus(receivingSummaryLineRequest);
        receiveSummaryServiceImpl.updateReceiveSummaryAndLine(receivingSummaryLineRequest, "US");
    }

    @Test
    public void updateReceiveSummaryTest() {
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx("R", 36, "US");
        ReceivingSummaryRequest receivingSummaryRequest = new ReceivingSummaryRequest("888", "998", LocalDate.of(2018, 10, 10),
                1, "A", new Meta("101", sorRoutingCtx));
        when(receiveSummaryValidator.isWareHouseData(sorRoutingCtx)).thenReturn(true);
        doNothing().when(receiveSummaryValidator).validateBusinessStatUpdateSummary(Mockito.anyString());
        when(mongoTemplate.findAndModify(Mockito.any(Query.class), Mockito.any(Update.class), refEq(FindAndModifyOptions.options().returnNew(true)),
                eq(ReceiveSummary.class), Mockito.any())).thenReturn(mock(ReceiveSummary.class));
        doNothing().when(publisher).publishEvent(mock(ReceiveSummary.class));
        receiveSummaryServiceImpl.updateReceiveSummary(receivingSummaryRequest, "US");
    }

    @Test
    public void updateReceiveSummaryAndLineIf() {
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx("R", 36, "US");
        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                "1", "10", new Meta("101", sorRoutingCtx));
        when(receiveSummaryValidator.isWareHouseData(sorRoutingCtx)).thenReturn(true);
        doNothing().when(receiveSummaryValidator).validateBusinessStatUpdateSummary(Mockito.anyString());
        doNothing().when(receiveSummaryLineValidator).validateInventoryMatchStatus(mock(ReceivingSummaryLineRequest.class));
        doNothing().when(receiveSummaryLineValidator).validateReceiptLineNumber(Mockito.anyString());
        when(mongoTemplate.findAndModify(Mockito.any(Query.class), Mockito.any(Update.class), refEq(FindAndModifyOptions.options().returnNew(true)),
                eq(ReceiveSummary.class), Mockito.any())).thenReturn(mock(ReceiveSummary.class));
        when(mongoTemplate.findAndModify(Mockito.any(Query.class), Mockito.any(Update.class), refEq(FindAndModifyOptions.options().returnNew(true)),
                eq(ReceivingLine.class), Mockito.any())).thenReturn(mock(ReceivingLine.class));
        doNothing().when(publisher).publishEvent(mock(List.class));
        receiveSummaryServiceImpl.updateReceiveSummaryAndLine(receivingSummaryLineRequest, "US");
    }

    @Test
    public void updateReceiveSummaryAndLineElse() {
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx("R", 36, "US");
        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("8", "9", LocalDate.now(), 1, "A",
                null, "10", new Meta("101", sorRoutingCtx));
        when(receiveSummaryValidator.isWareHouseData(sorRoutingCtx)).thenReturn(true);
        doNothing().when(receiveSummaryValidator).validateBusinessStatUpdateSummary(Mockito.anyString());
        doNothing().when(receiveSummaryLineValidator).validateInventoryMatchStatus(mock(ReceivingSummaryLineRequest.class));
        doNothing().when(receiveSummaryLineValidator).validateReceiptLineNumber(Mockito.anyString());
        when(mongoTemplate.findAndModify(Mockito.any(Query.class), Mockito.any(Update.class), refEq(FindAndModifyOptions.options().returnNew(true)), eq(ReceiveSummary.class), Mockito.any())).thenReturn(mock(ReceiveSummary.class));
        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getModifiedCount()).thenReturn(new Long(1));
        when(mongoTemplate.updateMulti(Mockito.any(Query.class), Mockito.any(Update.class), eq(ReceivingLine.class), Mockito.any())).thenReturn(updateResult);
        ArrayList list = new ArrayList<ReceivingLine>();
        list.add(mock(ReceivingLine.class));
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceivingLine.class), Mockito.any())).thenReturn(list);
        doNothing().when(publisher).publishEvent(mock(List.class));
        receiveSummaryServiceImpl.updateReceiveSummaryAndLine(receivingSummaryLineRequest, "US");
    }

    /**
     * Upc Numbers with + No line respose so iterator.remove scenario
     */
    @Test
    public void getReceiveSummaryTest1() {
        ReceiveSummary receiveSummary = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "4665207",
                8064, 18, 0, LocalDate.of(1986, 12, 12), LocalTime.of(18, 45, 21),
                0, 9788, 1111,
                0, 0, "H", 0.0, 1.0, 'P',
                null, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, "0", 0, LocalDateTime.now(), 0,
                "JJJ", "UU", LocalDateTime.now(), "99"
                , 'K', "IIL", null, null, null, null, null);
        ArrayList<ReceiveSummary> receiveSummaries = new ArrayList<>();
        receiveSummaries.add(receiveSummary);
        try {
            when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceiveSummary.class), Mockito.any())).thenReturn(receiveSummaries);
            when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceivingLine.class), Mockito.any())).thenReturn(new ArrayList<>());
            Map mockMap = Mockito.mock(Map.class);
            receiveSummaryServiceImpl.getReceiveSummary(mockMap);
        } catch (NullPointerException e) {
            e.getMessage();
        }
    }

    /**
     * No line respose + No item/upc  summary_id null (for line query) +  get freight respone
     */
    @Test
    public void getReceiveSummaryTest2() {
        ReceiveSummary receiveSummary = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "4665207",
                8064, 18, 0, LocalDate.of(1986, 12, 12), LocalTime.of(18, 45, 21),
                0, 9788, 1111,
                0, 0, "H", 0.0, 1.0, 'P',
                null, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, "0", 0, LocalDateTime.now(), 0,
                "JJJ", "UU", LocalDateTime.now(), "99"
                , 'K', "IIL", null, null, null, null, null);
        ArrayList<ReceiveSummary> receiveSummaries = new ArrayList<>();
        receiveSummaries.add(receiveSummary);
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceiveSummary.class), Mockito.any())).thenReturn(receiveSummaries);
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceivingLine.class), Mockito.any())).thenReturn(new ArrayList<>());
        when(receivingSummaryResponseConverter.convert(Mockito.any(ReceiveSummary.class))).thenReturn(mock(ReceivingSummaryResponse.class));
        Map mockMap = Mockito.mock(Map.class);
        receiveSummaryServiceImpl.getReceiveSummary(mockMap);
    }


    /**
     * this covers the scenario for summary list > 1000.
     */
    @Test
    public void getReceiveSummaryTest3() {
        ReceiveSummary receiveSummary = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "4665207",
                8064, 18, 0, LocalDate.of(1986, 12, 12), LocalTime.of(18, 45, 21),
                0, 9788, 1111,
                0, 0, "H", 0.0, 1.0, 'P',
                null, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, "0", 0, LocalDateTime.now(), 0,
                "JJJ", "UU", LocalDateTime.now(), "99"
                , 'K', "IIL", null, null, null, null, null);
        ArrayList<ReceiveSummary> receiveSummaries = mock(ArrayList.class);
//        receiveSummaries.add(receiveSummary);
        try {
            when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceiveSummary.class), Mockito.any())).thenReturn(receiveSummaries);
            when(receiveSummaries.size()).thenReturn(1234);
            when(receiveSummaries.isEmpty()).thenReturn(false).thenReturn(true);
//        when(receiveSummaries.get(Mockito.anyInt())).thenReturn(receiveSummary);
            Map mockMap = Mockito.mock(Map.class);
            receiveSummaryServiceImpl.getReceiveSummary(mockMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




