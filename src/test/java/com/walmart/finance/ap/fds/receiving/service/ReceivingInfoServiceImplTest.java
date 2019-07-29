package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnIntegrationServiceImpl;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnResponseData;
import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoLineResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestCombinations;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestQueryParameters;
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
import java.util.*;

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
        // Financial Txn mocking
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(123, 999403403, "30006", 3669,
                495742, 1, 9.0, 7777, "99987", "USER",
                null, "USER", "1223", 1828926897, "000000004147570", "Memo",
                3669, null, "999403403", null);
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        when(financialTxnIntegrationService.getFinancialTxnDetails(Mockito.anyMap())).thenReturn(financialTxnResponseDataList);
        // MongoTemplate mocking
        ReceiveSummary receiveSummary = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21",
                "4665267", 3669, 18, 99,
                LocalDate.of(1996, 12, 12), LocalTime.of(18, 45, 21),
                0, 7688, 1111, 0, 0,
                "H", 0.0, 99.0, 'A',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56,
                22), LocalDate.of(2019, 03, 14),
                LocalDate.now(), 9.0, 7, 0,
                0, LocalDateTime.now(), 0, "0000030006", "yyyy",
                LocalDateTime.now(), "4665267"
                , 'K', "LLL", 0.0, 999403403,null,null,null);
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceiveSummary.class), Mockito.any())).thenReturn(
                new ArrayList<ReceiveSummary>() {
                    {
                        add(receiveSummary);
                    }
                });
        List<ReceivingLine> receivingLines = new ArrayList<ReceivingLine>() {
            {
                add(new ReceivingLine("999403403|0000030006|3669|2019-06-19|1", "0000030006",
                        10, 3777, 94493, 7, 30.0, 40.0, "9",
                        89, 12, "1122", 99, 3669, 18,
                        LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 22,
                        LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 6, LocalDate.now(),
                        0, 1.9, "LL", 0, "ww",
                        null, 1, "N", "NSW CRASH TRNF", 1, 999403403, "999403403|0000030006|3669|0",null,null,null));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceivingLine.class), Mockito.any())).thenReturn(receivingLines);
        List<FreightResponse> freightResponses = new ArrayList<FreightResponse>() {
            {
                add(new FreightResponse("3172122756", "ARFW", "972035"));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(FreightResponse.class), Mockito.any())).thenReturn(freightResponses);
// Receiving Info Response Creation
        List<ReceivingInfoResponse> receivingInfoResponses = new ArrayList<ReceivingInfoResponse>() {
            {
                add(new ReceivingInfoResponse("USER", null, "ARFW",
                        "4665267", 7777, 99, 1, new Long(1), 3669,
                        999403403, LocalDate.of(2019, 03, 14), new Long(30006),
                        "A", 0.0, 99.0, "972035", 495742, "Memo",
                        0.0, 0, "USER", "1223",
                        3669, null, "999403403", 1828926897,
                        "000000004147570", null));
            }
        };
// Testing method
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("scenario", ReceivingInfoRequestCombinations.INVOICEID.name());
        ReceivingResponse result = receivingInfoService.getInfoSeviceData(allRequestParams);
        compareResults(receivingInfoResponses, result.getData());
    }

    /**
     * Financial Txn + invoiceID =164680544 , invoiceNumber = "000544892029256 , LineNumberFlag = N +
     */

    @Test
    public void getSeviceWithLineResponse() {
        // Financial Txn mocking
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(123, 999403403, "30006", 3669,
                495742, 1, 9.0, 7777, "99987", "USER",
                null, "USER", "1223", 1828926897, "000000004147570", "Memo",
                3669, null, "999403403", null);
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        when(financialTxnIntegrationService.getFinancialTxnDetails(Mockito.anyMap())).thenReturn(financialTxnResponseDataList);
        // MongoTemplate mocking
        ReceiveSummary receiveSummary = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21",
                "4665267", 3669, 18, 99,
                LocalDate.of(1996, 12, 12), LocalTime.of(18, 45, 21),
                0, 7688, 1111, 0, 0,
                "H", 0.0, 99.0, 'A',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56,
                22), LocalDate.of(2019, 03, 14),
                LocalDate.now(), 9.0, 7, 0,
                0, LocalDateTime.now(), 0, "0000030006", "yyyy",
                LocalDateTime.now(), "4665267"
                , 'K', "LLL", 0.0, 999403403,null,null,null);
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceiveSummary.class), Mockito.any())).thenReturn(new ArrayList<ReceiveSummary>(){
            {
                add(receiveSummary);
            }
        });
        List<ReceivingLine> receivingLines = new ArrayList<ReceivingLine>() {
            {
                add(new ReceivingLine("999403403|0000030006|3669|2019-06-19|1", "0000030006",
                        10, 3777, 94493, 7, 30.0, 40.0, "9",
                        89, 12, "1122", 99, 3669, 18,
                        LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 22,
                        LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 6, LocalDate.now(),
                        0, 1.9, "LL", 0, "ww",
                        null, 1, "N", "NSW CRASH TRNF", 1, 999403403, "999403403|0000030006|3669|0",null,null,null));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceivingLine.class), Mockito.any())).thenReturn(receivingLines);
        List<FreightResponse> freightResponses = new ArrayList<FreightResponse>() {
            {
                add(new FreightResponse("3172122756", "ARFW", "972035"));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(FreightResponse.class), Mockito.any())).thenReturn(freightResponses);
        // Receiving Info Response Creation
        List<ReceivingInfoLineResponse> receivingInfoLineResponses = new ArrayList<ReceivingInfoLineResponse>() {
            {
                add(new ReceivingInfoLineResponse(new Long(30006), 1, 3777, 7, 30.0,
                        40.0, 7, 6,
                        "N", "1122",
                        "NSW CRASH TRNF", "LL", "ww", 1, "1.9", null
                ));
            }
        };
        List<ReceivingInfoResponse> receivingInfoResponses = new ArrayList<ReceivingInfoResponse>() {
            {
                add(new ReceivingInfoResponse("USER", null, "ARFW",
                        "4665267", 7777, 99, 1, new Long(1), 3669,
                        999403403, LocalDate.of(2019, 03, 14), new Long(30006),
                        "A", 0.0, 99.0, "972035", 495742, "Memo",
                        0.0, 0, "USER", "1223",
                        3669, null, "999403403", 1828926897,
                        "000000004147570", receivingInfoLineResponses));
            }
        };
        // Testing method
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("scenario", ReceivingInfoRequestCombinations.INVOICEID.name());
        allRequestParams.put(ReceivingInfoRequestQueryParameters.LINENUMBERFLAG.getQueryParam(), "Y");
        ReceivingResponse result = receivingInfoService.getInfoSeviceData(allRequestParams);
        compareResults(receivingInfoResponses, result.getData());
    }

    private void compareResults(List<ReceivingInfoResponse> receivingInfoResponses, List<ReceivingInfoResponse> result) {
        org.assertj.core.api.Assertions.assertThat(receivingInfoResponses.get(0)).isEqualToComparingFieldByFieldRecursively(result.get(0));
    }

    /**
     * No Financial Txn + Date format exception
     */

    @Test(expected = NotFoundException.class)
    public void getServiceNotFoundException() {
        // Testing method
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("scenario", ReceivingInfoRequestCombinations.INVOICEID.name());
        ReceivingResponse result = receivingInfoService.getInfoSeviceData(allRequestParams);
    }
}
