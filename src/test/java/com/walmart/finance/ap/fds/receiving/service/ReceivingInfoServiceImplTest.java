package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnIntegrationServiceImpl;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnResponseData;
import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.response.*;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestCombinations;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestQueryParameters;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.UOM_CODE_WH_EXCEPTION_RESOLUTION;
import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.UOM_CODE_WH_MATCHING;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@PrepareForTest(ReceivingInfoServiceImpl.class)
@RunWith(PowerMockRunner.class)
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
        receivingInfoService.setMonthsPerShard(1);
        receivingInfoService.setMonthsToDisplay(12);
    }

    /**
     * Financial Txn + purchaseOrderNumber =164680544 , purchaseOrderID = 164680544 , LineNumberFlag = N +
     */

    @Test
    public void getSevice() {
        // Financial Txn mocking
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(123), 999403403L,
                "0000030006", 3669,
                495742, 1, 9.0, 7777, "99987", "USER",
                null, "USER", "1223", 1828926897L, "000000004147570", "Memo",
                "3669", null, "999403403", null
                , null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null);
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
                LocalDate.now(), 9.0, 7, "0",
                0, LocalDateTime.now(), 0, "0000030006", "yyyy",
                LocalDateTime.now(), "4665267"
                , 'K', "LLL", 0.0, new Long(999403403), null, null, null, LocalDateTime.of(2019, 03, 14, 8, 45, 21));
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceiveSummary.class), Mockito.any())).thenReturn(
                new ArrayList<ReceiveSummary>() {
                    {
                        add(receiveSummary);
                    }
                });

        Map<String, WHLinePOLineValue> poLineValueMap = new HashMap<>();
        poLineValueMap.put(UOM_CODE_WH_EXCEPTION_RESOLUTION, new WHLinePOLineValue(UOM_CODE_WH_EXCEPTION_RESOLUTION,
                6, 30.0, 40.0));

        List<ReceivingLine> receivingLines = new ArrayList<ReceivingLine>() {
            {
                add(new ReceivingLine("999403403|0000030006|3669|2019-06-19|1", "0000030006",
                        10, 3777L, 94493, 7.0, 30.0, 40.0, "9",
                        89, 12, "1122", 99, 3669, 18,
                        LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 22,
                        LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 6, LocalDate.now(),
                        0, 1.9, "LL", 0, "ww",
                        null, poLineValueMap, 1, "N", "NSW CRASH TRNF", 1, new Long(999403403), "999403403|0000030006|3669|0",
                        null, null, null));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceivingLine.class), Mockito.any())).thenReturn(receivingLines);
        FreightResponse freightResponse = new FreightResponse(new Long(31721227), "ARFW", "972035");
        when(mongoTemplate.findById(Mockito.any(Long.class), eq(FreightResponse.class), Mockito.any())).thenReturn(freightResponse);

// Receiving Info Response Creation
        List<ReceivingInfoResponse> receivingInfoResponses = new ArrayList<ReceivingInfoResponse>() {
            {
                add(new ReceivingInfoResponse("USER", null, "ARFW",
                        "4665267", 7777, 99, 1, new Long(1), 3669,
                        new Long(999403403), LocalDate.of(2019, 03, 14), "0000030006",
                        "A", 0.0, 99.0, "972035", 495742, "Memo",
                        0.0, 0, "USER", "1223",
                        "3669", null, "999403403", 1828926897L,
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
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(123), 999403403L,
                "0000030006", 3669,
                495742, 1, 9.0, 7777, "99987", "USER",
                null, "USER", "1223", 1828926897L, "000000004147570", "Memo",
                "3669", null, "999403403", null
                , null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, 0, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null);
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
                LocalDate.now(), 9.0, 7, "0",
                0, LocalDateTime.now(), 0, "0000030006", "yyyy",
                LocalDateTime.now(), "4665267"
                , 'K', "LLL", 0.0, new Long(999403403), null, null, null, LocalDateTime.of(2019, 03, 14, 8, 45, 21));
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceiveSummary.class), Mockito.any())).thenReturn(new ArrayList<ReceiveSummary>() {
            {
                add(receiveSummary);
            }
        });
        Map<String, WHLinePOLineValue> poLineValueMap = new HashMap<>();
        poLineValueMap.put(UOM_CODE_WH_EXCEPTION_RESOLUTION, new WHLinePOLineValue(UOM_CODE_WH_EXCEPTION_RESOLUTION,
                6, 30.0, 40.0));
        poLineValueMap.put(UOM_CODE_WH_MATCHING, new WHLinePOLineValue(UOM_CODE_WH_MATCHING,
                6, 30.0, 40.0));
        List<ReceivingLine> receivingLines = new ArrayList<ReceivingLine>() {
            {
                add(new ReceivingLine("999403403|0000030006|3669|2019-06-19|1", "0000030006",
                        10, 3777L, 94493, 7.0, 30.0, 40.0, "9",
                        89, 12, "1122", 99, 3669, 18,
                        LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 22,
                        LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 6, LocalDate.now(),
                        0, 1.9, "LL", 0, "ww",
                        null, poLineValueMap, 1, "N", "NSW CRASH TRNF", 1, new Long(999403403), "999403403|0000030006" +
                        "|3669|0", null,
                        null, null));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceivingLine.class), Mockito.any())).thenReturn(receivingLines);

        FreightResponse freightResponse = new FreightResponse(new Long(31721227), "ARFW", "972035");
        when(mongoTemplate.findById(Mockito.any(Long.class), eq(FreightResponse.class), Mockito.any())).thenReturn(freightResponse);
        // Receiving Info Response Creation
        List<ReceivingInfoLineResponse> receivingInfoLineResponses = new ArrayList<ReceivingInfoLineResponse>() {
            {
                add(new ReceivingInfoLineResponse("0000030006", 1, 3777L, 7, 30.0,
                        40.0, 7.0, 6,
                        "N", "1122",
                        "NSW CRASH TRNF", "LL", "ww", 1, "1.9", null, 30.0, 40.0, 6
                ));
            }
        };
        List<ReceivingInfoResponse> receivingInfoResponses = new ArrayList<ReceivingInfoResponse>() {
            {
                add(new ReceivingInfoResponse("USER", null, "ARFW",
                        "4665267", 7777, 99, 1, new Long(1), 3669,
                        new Long(999403403), LocalDate.of(2019, 03, 14), "0000030006",
                        "A", 0.0, 99.0, "972035", 495742, "Memo",
                        0.0, 0, "USER", "1223",
                        "3669", null, "999403403", 1828926897L,
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

    private void compareResultsV1(List<ReceivingInfoResponseV1> receivingInfoResponses, List<ReceivingInfoResponse> result) {
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

    @Test
    public void getInfoSeviceDataV1() {
        List<com.walmart.finance.ap.fds.receiving.integrations.InvoiceFinDelNoteChangeLogs> invoiceFinDelNoteChangeLogs =
                new ArrayList<com.walmart.finance.ap.fds.receiving.integrations.InvoiceFinDelNoteChangeLogs>() {

                    {
                        add(new com.walmart.finance.ap.fds.receiving.integrations.InvoiceFinDelNoteChangeLogs(
                                null, "User1234", "Del1234", "OrgDel123"));
                    }
                };
        List<com.walmart.finance.ap.fds.receiving.integrations.InvoiceFinTransProcessLogs> invoiceFinTransProcessLogs =
                new ArrayList<com.walmart.finance.ap.fds.receiving.integrations.InvoiceFinTransProcessLogs>() {
                    {
                        add(new com.walmart.finance.ap.fds.receiving.integrations.InvoiceFinTransProcessLogs(
                                null, null, 10, null, "ID123"));
                    }
                };
        List<com.walmart.finance.ap.fds.receiving.integrations.InvoiceFinTransAdjustLogs> invoiceFinTransAdjustLogs =
                new ArrayList<com.walmart.finance.ap.fds.receiving.integrations.InvoiceFinTransAdjustLogs>() {
                    {
                        add(new com.walmart.finance.ap.fds.receiving.integrations.InvoiceFinTransAdjustLogs(
                                10, 10.05, null, "Change123",
                                null, 20.02, null, null));
                    }
                };
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(724201901),
                972515962L, "110950", 6480,
                397646, 18, -5743.12, 640, "6854748957", "ID123",
                null, "PEPSI MIDAMERICA", "1223", 97166785L, "1832721624", null,
                null, null, null, null
                , 538, 1, 0, "US", null, null, 0, "del123",
                null, null, "N", invoiceFinDelNoteChangeLogs, invoiceFinTransAdjustLogs
                , invoiceFinTransProcessLogs, null, 640, 7, 6479, 6479, 64, 20, null,
                10, null, "SOE", null, "6854748957"
                , 0.0, 0, "0", 0.0, 0.0, 0, 1, "PO RECEIVINGS", null);
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        when(financialTxnIntegrationService.getFinancialTxnDetails(Mockito.anyMap())).thenReturn(financialTxnResponseDataList);
        // MongoTemplate mocking
        ReceiveSummary receiveSummary = new ReceiveSummary("972515962|110950|6479|2019-06-11",
                "0", 6479, 0, 99,
                LocalDate.of(1970, 01, 01), LocalTime.of(05, 30, 00),
                null, 50500, 0, 0, null,
                "", 0.0, 0.0, 'A',
                null, ' ', ' ',
                ' ', LocalDateTime.of(2019, 06, 02, 17, 30,
                00), LocalDate.of(2019, 01, 01),
                LocalDate.of(2019, 06, 02), 0.0, null, null,
                0, null, null, "110950", null,
                null, null
                , 'S', null, 0.0, new Long(972515962), null, null, null, LocalDateTime.now());
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceiveSummary.class), Mockito.any())).thenReturn(new ArrayList<ReceiveSummary>() {
            {
                add(receiveSummary);
            }
        });

        ReceiveMDSResponse receiveMDSResponse = new ReceiveMDSResponse(1,1,350L,"01");
        Map<String, ReceiveMDSResponse> receiveMDSResponseMap = new HashMap<>();
        receiveMDSResponseMap.put("1", receiveMDSResponse);


        List<ReceivingLine> receivingLines = new ArrayList<ReceivingLine>() {
            {
                add(new ReceivingLine("972515962|110950|6479|2019-06-11|2", "110950",
                        1, 575486609L, 495742, 2.0, 30.0, 30.09, null,
                        null, null, "0000047875883989", 0, 6479, 1,
                        LocalDate.of(2019, 06, 20), LocalDateTime.of(2019, 07, 14, 16, 50, 17), null,
                        null, 'W', "DB2", null, 0, LocalDate.of(2019, 06, 11),
                        null, 0.0, null, null, "",
                        receiveMDSResponseMap, null, 2, "N", "NSW CRASH TRNF", 1, new Long(972515962), "972515962" +
                        "|110950" +
                        "|6479|2019-06-11", null, null, null));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceivingLine.class), Mockito.any())).thenReturn(receivingLines);

        List<FreightResponse> freightResponses = new ArrayList<FreightResponse>() {
            {
                add(new FreightResponse(null, null, null));
            }
        };
        when(mongoTemplate.find(Mockito.any(Query.class), eq(FreightResponse.class), Mockito.any())).thenReturn(freightResponses);
        // Receiving Info Response Creation
        List<ReceiveMDSResponse> merchandises = new ArrayList<ReceiveMDSResponse>() {
            {
                add(new ReceiveMDSResponse(1,1, 350L, "01"));
                add(new ReceiveMDSResponse(1, 2, 400L, "02"));
            }
        };
        ReceivingInfoLineResponse receivingInfoLineResponse = new ReceivingInfoLineResponse("110950", 2, 575486609L,
                2, 30.0,
                30.09, 2.0, 0,
                "N", "0000047875883989",
                "NSW CRASH TRNF", null, "", 1, "0.0", merchandises, null, null, null);
        List<ReceivingInfoLineResponse> lineResponses = new ArrayList<ReceivingInfoLineResponse>() {
            {
                add(receivingInfoLineResponse);
            }
        };
        List<com.walmart.finance.ap.fds.receiving.response.InvoiceFinTransProcessLogs> invoiceFinTransProcessLogsResponse = new ArrayList<com.walmart.finance.ap.fds.receiving.response.InvoiceFinTransProcessLogs>() {
            {
                add(new com.walmart.finance.ap.fds.receiving.response.InvoiceFinTransProcessLogs(null, null, 10, null, "ID123"));
            }
        };
        List<com.walmart.finance.ap.fds.receiving.response.InvoiceFinTransAdjustLogs> invoiceFinTransAdjustLogsResponse = new ArrayList<com.walmart.finance.ap.fds.receiving.response.InvoiceFinTransAdjustLogs>() {
            {
                add(new com.walmart.finance.ap.fds.receiving.response.InvoiceFinTransAdjustLogs(10, 10.05, null, "Change123",
                        null, 20.02, null, null));
            }
        };
        List<com.walmart.finance.ap.fds.receiving.response.InvoiceFinDelNoteChangeLogs> invoiceFinDelNoteChangeLogsResponse = new ArrayList<com.walmart.finance.ap.fds.receiving.response.InvoiceFinDelNoteChangeLogs>() {
            {
                add(new com.walmart.finance.ap.fds.receiving.response.InvoiceFinDelNoteChangeLogs(null, "User1234", "Del1234", "OrgDel123"));
            }
        };
        ReceivingInfoResponseV1 response = new ReceivingInfoResponseV1("ID123", null, null,
                "0", 640, 99, 18, new Long(1), 6479,
                new Long(972515962), LocalDate.of(2019, 01, 01), "666666",
                "A", 0.0, 0.0, null, 397646, null,
                0.0, 0, "PEPSI MIDAMERICA", "1223",
                null, null, null, 97166785L,
                "1832721624", new Long(724201901), 0, null, "6854748957", "US",
                1, 0.0, -5743.12, 0.0, 0.0, 640
                , null, null, "6854748957", null, 0, 538,
                0, 0, "0", "del123", 6479,
                7, 6479, 20, 64, 640, "N",
                null, 10,
                invoiceFinTransProcessLogsResponse, invoiceFinTransAdjustLogsResponse, invoiceFinDelNoteChangeLogsResponse, lineResponses);
        List<ReceivingInfoResponseV1> list = new ArrayList<ReceivingInfoResponseV1>() {
            {
                add(response);
            }
        };
        // Testing method
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("scenario", ReceivingInfoRequestCombinations.INVOICEID.name());
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam(), "2019-01-01");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam(), "2019-01-01");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.LINENUMBERFLAG.getQueryParam(), "Y");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam(), "123");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam(), "Y");
        //ReceivingResponse result = receivingInfoService.getInfoSeviceDataV1(allRequestParams);
        //compareResultsV1(list, result.getData());
    }

    @Test(expected = BadRequestException.class)
    public void formulateIdReceiptStartDateException() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(724201901), null, null, null,
                397646, 18, -5743.12, 640, "6854748957", "ID123",
                null, "PEPSI MIDAMERICA", "1223", 97166785L, "1832721624", null,
                null, null, null, null
                , 538, 1, 0, "US", null, null, 0, "del123",
                null, null, "N", null, null
                , null, null, 640, 7, 6479, 6479, 64, 20, null,
                10, null, "SOE", null, "6854748957"
                , 0.0, 0, "0", 0.0, 0.0, 0, 1, "PO RECEIVINGS", null);
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        when(financialTxnIntegrationService.getFinancialTxnDetails(Mockito.anyMap())).thenReturn(financialTxnResponseDataList);
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceiveSummary.class), Mockito.any())).thenReturn(null);
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceivingLine.class), Mockito.any())).thenReturn(null);
        when(mongoTemplate.find(Mockito.any(Query.class), eq(FreightResponse.class), Mockito.any())).thenReturn(null);
        // Testing method
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("scenario", ReceivingInfoRequestCombinations.INVOICEID.name());
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam(), "123");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam(), "123");
        receivingInfoService.getInfoSeviceDataV1(allRequestParams);
    }

    @Test(expected = BadRequestException.class)
    public void formulateIdReceiptEndDateException() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(724201901), null, null, null,
                397646, 18, -5743.12, 640, "6854748957", "ID123",
                null, "PEPSI MIDAMERICA", "1223", 97166785L, "1832721624", null,
                null, null, null, null
                , 538, 1, 0, "US", null, null, 0, "del123",
                null, null, "N", null, null
                , null, null, 640, 7, 6479, 6479, 64, 20, null,
                10, null, "SOE", null, "6854748957"
                , 0.0, 0, "0", 0.0, 0.0, 0, 1, "PO RECEIVINGS", null);
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        when(financialTxnIntegrationService.getFinancialTxnDetails(Mockito.anyMap())).thenReturn(financialTxnResponseDataList);
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceiveSummary.class), Mockito.any())).thenReturn(null);
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceivingLine.class), Mockito.any())).thenReturn(null);
        when(mongoTemplate.find(Mockito.any(Query.class), eq(FreightResponse.class), Mockito.any())).thenReturn(null);
        // Testing method
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("scenario", ReceivingInfoRequestCombinations.INVOICEID.name());
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam(), "2019-01-01");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam(), "123");
        receivingInfoService.getInfoSeviceDataV1(allRequestParams);
    }

    @Test(expected = Exception.class)
    public void formulateIdReceiptEndDateBeforeStartDateException() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(724201901), null, null, null,
                397646, 18, -5743.12, 640, "6854748957", "ID123",
                null, "PEPSI MIDAMERICA", "1223", 97166785L, "1832721624", null,
                null, null, null, null
                , 538, 1, 0, "US", null, null, 0, "del123",
                null, null, "N", null, null
                , null, null, 640, 7, 6479, 6479, 64, 20, null,
                10, null, "SOE", null, "6854748957"
                , 0.0, 0, "0", 0.0, 0.0, 0, 1, "PO RECEIVINGS", null);
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        when(financialTxnIntegrationService.getFinancialTxnDetails(Mockito.anyMap())).thenReturn(financialTxnResponseDataList);
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceiveSummary.class), Mockito.any())).thenReturn(new ArrayList<>());
        when(mongoTemplate.find(Mockito.any(Query.class), eq(ReceivingLine.class), Mockito.any())).thenReturn(null);
        when(mongoTemplate.find(Mockito.any(Query.class), eq(FreightResponse.class), Mockito.any())).thenReturn(null);
        // Testing method
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("scenario", ReceivingInfoRequestCombinations.INVOICEID.name());
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam(), "2019-01-03");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam(), "2019-01-01");
        allRequestParams.put(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam(), "S");
        receivingInfoService.getInfoSeviceDataV1(allRequestParams);
    }

    @Test(expected = NotFoundException.class)
    public void getServiceNotFoundExceptionV1() {
        // Testing method
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put("scenario", ReceivingInfoRequestCombinations.INVOICEID.name());
        receivingInfoService.getInfoSeviceDataV1(allRequestParams);
    }
}
