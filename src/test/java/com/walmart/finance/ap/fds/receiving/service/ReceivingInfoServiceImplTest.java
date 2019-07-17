package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnIntegrationServiceImpl;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnResponse;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnResponseData;
import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoLineResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class ReceivingInfoServiceImplTest {

    @Mock
    MongoTemplate mongoTemplate;

    @Mock
    FinancialTxnIntegrationServiceImpl financialTxnIntegrationService;

    @InjectMocks
    ReceivingInfoServiceImpl receivingInfoService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Financial Txn + purchaseOrderNumber =164680544 , purchaseOrderID = 164680544 , LineNumberFlag = N +
     */
    @Test
    public void getSevice() {
        /* Financial Txn mocking */
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(123, 164680544, "10441", 6302, 2222, 0, 9.0, 7777, "99987");
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        when(financialTxnIntegrationService.getFinancialTxnDetails(Mockito.anyMap())).thenReturn(financialTxnResponseDataList);

        /* MongoTemplate mocking */
        List<ReceiveSummary> receiveSummaries = new ArrayList<ReceiveSummary>() {
            {
                add(new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21",
                        "4665267", 8264, 18, 99,
                        LocalDate.of(1996, 12, 12), LocalTime.of(18, 45, 21),
                        0, 7688, 1111, 0, 0,
                        'H', 0.0, 99.0, 'A',
                        2L, 'k', 'L',
                        'M', LocalDateTime.of(1990, 12, 12, 18, 56,
                        22), LocalDate.of(2019, 03, 14),
                        LocalDate.now(), 9.0, 7, 0,
                        0, LocalDateTime.now(), 0, "JJJ", "yyyy",
                        LocalDateTime.now(), "4665267"
                        , 'K', "LLL"));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceiveSummary.class), Mockito.any())).thenReturn(receiveSummaries);
        List<ReceivingLine> receivingLines = new ArrayList<ReceivingLine>() {
            {
                add(new ReceivingLine("112|1804823|8264|18|0|1995-10-17|1995-10-17T18:45:21|122", "4665267",
                        10, 3777, 94493, 7, 30.0, 40.0, "9",
                        89, 12, "1122", 99, 8264, 18,
                        LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 22,
                        LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 6, LocalDate.now(),
                        0, 1.9, "LL", 0, "ww", null));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceivingLine.class), Mockito.any())).thenReturn(receivingLines);
        List<FreightResponse> freightResponses = new ArrayList<FreightResponse>() {
            {
                add(new FreightResponse("3172122756", "ARFW", "972035"));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(FreightResponse.class), Mockito.any())).thenReturn(freightResponses);

        /* Receiving Info Response Creation */
        List<ReceivingInfoResponse> receivingInfoResponses = new ArrayList<ReceivingInfoResponse>() {
            {
                add(new ReceivingInfoResponse("99987", 10441,
                        99, "164680544", 6302, 0,
                        LocalDate.of(2019, 03, 14), 'A', 2222,
                        "ARFW", "972035", 9.0, 99.0,
                        new Long(1), 77, 0, null));
            }
        };

        /* Testing method */
        ReceivingResponse result = receivingInfoService.getSevice("US", null, null, "164680544", "164680544",
                null, null, null, null, null, null, null, null,
                null, null, null, "N");
        compareResults(receivingInfoResponses, result.getData());
    }

    /**
     * Financial Txn + invoiceID =164680544 , invoiceNumber = "000544892029256 , LineNumberFlag = N +
     */
    @Test
    public void getSeviceWithLineResponse() {

        /* Financial Txn mocking */
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(123, 164680544, "10441", 6302, 2222, 0, 9.0, null, "99987");
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        when(financialTxnIntegrationService.getFinancialTxnDetails(Mockito.anyMap())).thenReturn(financialTxnResponseDataList);

        /* MongoTemplate mocking */
        List<ReceiveSummary> receiveSummaries = new ArrayList<ReceiveSummary>() {
            {
                add(new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21",
                        "4665267", 8264, 18, 99,
                        LocalDate.of(1996, 12, 12), LocalTime.of(18, 45, 21),
                        0, 7688, 1111, 0, 0,
                        'H', 0.0, 99.0, 'A',
                        2L, 'k', 'L',
                        'M', LocalDateTime.of(1990, 12, 12, 18, 56,
                        22), LocalDate.of(2019, 03, 14),
                        LocalDate.now(), 9.0, 7, 0,
                        0, LocalDateTime.now(), 0, "JJJ", "yyyy",
                        LocalDateTime.now(), "4665267"
                        , 'K', "LLL"));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceiveSummary.class), Mockito.any())).thenReturn(receiveSummaries);
        List<ReceivingLine> receivingLines = new ArrayList<ReceivingLine>() {
            {
                add(new ReceivingLine("112|1804823|8264|18|0|1995-10-17|1995-10-17T18:45:21|122", "4665267",
                        10, 3777, 94493, 7, 30.0, 40.0, "9",
                        89, 12, "1122", 99, 8264, 18,
                        LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 22,
                        LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "89", 6, LocalDate.now(),
                        0, 1.9, "LL", 0, "ww", null));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceivingLine.class), Mockito.any())).thenReturn(receivingLines);
        List<FreightResponse> freightResponses = new ArrayList<FreightResponse>() {
            {
                add(new FreightResponse("3172122756", "ARFW", "972035"));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(FreightResponse.class), Mockito.any())).thenReturn(freightResponses);

        /* Receiving Info Response Creation */
        List<ReceivingInfoLineResponse> receivingInfoLineResponses = new ArrayList<ReceivingInfoLineResponse>() {
            {
                add(new ReceivingInfoLineResponse(
                        4665267, 10, 3777, 94493,
                        7, 30.0, 40.0, 6,
                        7, "12", "LL",
                        "ww", "1.9", 99,
                        8264, 18, "89", "9", 10.0,null
                ));
            }
        };
        List<ReceivingInfoResponse> receivingInfoResponses = new ArrayList<ReceivingInfoResponse>() {
            {
                add(new ReceivingInfoResponse("99987", 10441,
                        99, "164680544", 6302, 0,
                        LocalDate.of(2019, 03, 14), 'A', 2222,
                        "ARFW", "972035", 9.0, 99.0,
                        new Long(1), 0, 0, receivingInfoLineResponses));
            }
        };

        /* Testing method */
        ReceivingResponse result = receivingInfoService.getSevice("US", "1234", "000544892029256", null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, "Y");
        compareResults(receivingInfoResponses, result.getData());
    }

    private void compareResults(List<ReceivingInfoResponse> receivingInfoResponses, List<ReceivingInfoResponse> result) {
        org.assertj.core.api.Assertions.assertThat(receivingInfoResponses.get(0)).isEqualToComparingFieldByFieldRecursively(result.get(0));
    }

    /**
     * No Financial Txn + receiptNumbers = "10441","2345", LineNumberFlag = N
     */
    @Test
    public void getServiceNormalFlow() {

        /* MongoTemplate mocking */
        List<ReceiveSummary> receiveSummaries = new ArrayList<ReceiveSummary>() {
            {
                add(new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21",
                        "4665267", 8264, 18, 99,
                        LocalDate.of(1996, 12, 12), LocalTime.of(18, 45, 21),
                        0, 7688, 1111, 0, 0,
                        'H', 90.0, 99.0, 'A',
                        2L, 'k', 'L',
                        'M', LocalDateTime.of(1990, 12, 12, 18, 56,
                        22), LocalDate.of(2019, 03, 14),
                        LocalDate.now(), 9.0, 7, 0,
                        0, LocalDateTime.now(), 0, "10441", "yyyy",
                        LocalDateTime.now(), "4665267"
                        , 'K', "LLL"));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceiveSummary.class), Mockito.any())).thenReturn(receiveSummaries);
        List<ReceivingLine> receivingLines = new ArrayList<ReceivingLine>() {
            {
                add(new ReceivingLine("112|1804823|8264|18|0|1995-10-17|1995-10-17T18:45:21|122", "4665267",
                        10, 3777, 94493, 7, 30.0, 40.0, "9",
                        89, 12, "1122", 99, 8264, 18,
                        LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 22,
                        LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "89", 6, LocalDate.now(),
                        0, 1.9, "LL", 0, "ww", null));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceivingLine.class), Mockito.any())).thenReturn(receivingLines);
        List<FreightResponse> freightResponses = new ArrayList<FreightResponse>() {
            {
                add(new FreightResponse("3172122756", "ARFW", "972035"));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(FreightResponse.class), Mockito.any())).thenReturn(freightResponses);

        /* Receiving Info Response Creation */
        List<ReceivingInfoLineResponse> receivingInfoLineResponses = new ArrayList<ReceivingInfoLineResponse>() {
            {
                add(new ReceivingInfoLineResponse(
                        4665267, 10, 3777, 94493,
                        7, 30.0, 40.0, 6,
                        7, "12", "LL",
                        "ww", "1.9", 99,
                        8264, 18, "89", "9", 10.0, null
                ));
            }
        };
        List<ReceivingInfoResponse> receivingInfoResponses = new ArrayList<ReceivingInfoResponse>() {
            {
                add(new ReceivingInfoResponse("4665267", 10441,
                        99, "4665267", 8264, 18,
                        LocalDate.of(2019, 03, 14), 'A', 7688,
                        "ARFW", "972035", 90.0, 99.0,
                        new Long(1), 0, 0, null));
            }
        };

        /* Testing method */
        ReceivingResponse result = receivingInfoService.getSevice("US", null, null, null, null,
                new ArrayList<String>() {{
                    add("10441");
                    add("2345");
                }}, null, null, null, "18", "7688", null,
                new ArrayList<String>() {{
                    add("3777");
                }},
                null, "2019-03-12", "2019-03-15", "N");
        compareResults(receivingInfoResponses, result.getData());
    }

    /**
     * No Financial Txn + controlNumber = ,  LineNumberFlag = Y
     */
    @Test
    public void getServiceNormalFlowWithLine() {

        /* MongoTemplate mocking */
        List<ReceiveSummary> receiveSummaries = new ArrayList<ReceiveSummary>() {
            {
                add(new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21",
                        "4665267", 8264, 18, 99,
                        LocalDate.of(1996, 12, 12), LocalTime.of(18, 45, 21),
                        0, 7688, 1111, 0, 0,
                        'H', 90.0, 99.0, 'A',
                        null, 'k', 'L',
                        'M', LocalDateTime.of(1990, 12, 12, 18, 56,
                        22), LocalDate.of(2019, 03, 14),
                        LocalDate.now(), 9.0, 7, 0,
                        0, LocalDateTime.now(), 0, "10441", "yyyy",
                        LocalDateTime.now(), "4665267"
                        , 'K', "LLL"));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceiveSummary.class), Mockito.any())).thenReturn(receiveSummaries);
        List<ReceivingLine> receivingLines = new ArrayList<ReceivingLine>() {
            {
                add(new ReceivingLine("112|1804823|8264|18|0|1995-10-17|1995-10-17T18:45:21|122", "4665267",
                        10, 3777, 94493, 7, 30.0, 40.0, "9",
                        89, 12, "1122", 99, 8264, 18,
                        LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 22,
                        LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "89", 6, LocalDate.now(),
                        0, 1.9, "LL", 0, "ww", null));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceivingLine.class), Mockito.any())).thenReturn(receivingLines);
        // Build expand id is null so Freight response is not required.

        /* Receiving Info Response Creation */
        List<ReceivingInfoLineResponse> receivingInfoLineResponses = new ArrayList<ReceivingInfoLineResponse>() {
            {
                add(new ReceivingInfoLineResponse(
                        4665267, 10, 3777, 94493,
                        7, 30.0, 40.0, 6,
                        7, "12", "LL",
                        "ww", "1.9", 99,
                        8264, 18, "89", "9", 10.0, null
                ));
            }
        };
        List<ReceivingInfoResponse> receivingInfoResponses = new ArrayList<ReceivingInfoResponse>() {
            {
                add(new ReceivingInfoResponse("4665267", 10441,
                        99, "4665267", 8264, 18,
                        LocalDate.of(2019, 03, 14), 'A', 7688,
                        null, null, 90.0, 99.0,
                        new Long(1), 0, 0, receivingInfoLineResponses));
            }
        };

        /* Testing method */
        ReceivingResponse result = receivingInfoService.getSevice("US", null, null, null, null,
                null, "99", "4665267", "8264", null, null, "0", null,
                new ArrayList<String>() {{
                    add("3777");
                }}, null, null, "Y");
        compareResults(receivingInfoResponses, result.getData());
    }

    /**
     * No Financial Txn + Date format exception
     */
    @Test(expected = BadRequestException.class)
    public void getServiceDateFormatException() {
        /* Testing method */
        ReceivingResponse result = receivingInfoService.getSevice("US", null, null, null, null,
                new ArrayList<String>() {{
                    add("10441");
                    add("2345");
                }}, null, null, null, "18", "7688", null,
                new ArrayList<String>() {{
                    add("3777");
                }},
                null, "2019-03-44", "2019-03-15", "N");
    }

    /**
     * No Financial Txn + Date format exception
     */
    @Test(expected = NotFoundException.class)
    public void getServiceNotFoundException() {
        /* Testing method */
        ReceivingResponse result = receivingInfoService.getSevice("US", null, null, null, null,
                new ArrayList<String>() {{
                    add("10441");
                    add("2345");
                }}, null, null, null, "18", "7688", null,
                new ArrayList<String>() {{
                    add("3777");
                }},
                null, "2019-03-14", "2019-03-15", "N");
//        compareResults(receivingInfoResponses, result);
    }
}