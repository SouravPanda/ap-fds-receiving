package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.config.DefaultValuesConfigProperties;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingInfoLineResponseConverter;
import com.walmart.finance.ap.fds.receiving.dao.FreightDaoImpl;
import com.walmart.finance.ap.fds.receiving.dao.ReceivingLineDaoImpl;
import com.walmart.finance.ap.fds.receiving.dao.ReceivingSummaryDaoImpl;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnIntegrationServiceImpl;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnResponseData;
import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummaryRequestParams;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoLineResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoResponseV1;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.response.WHLinePOLineValue;
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
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.UOM_CODE_WH_EXCEPTION_RESOLUTION;
import static org.mockito.Mockito.when;

public class ReceivingInfoServiceTest {

    @Mock
    MongoTemplate mongoTemplate;

    @Mock
    FinancialTxnIntegrationServiceImpl financialTxnIntegrationService;

    @InjectMocks
    ReceivingInfoServiceImpl receivingInfoService;

    @Mock
    ReceivingLineDaoImpl receivingLineDao;

    @Mock
    ReceivingSummaryDaoImpl receivingSummaryDao;

    @Mock
    FreightDaoImpl freightDao;

    @Mock
    DefaultValuesConfigProperties defaultValuesConfigProperties;

    @Mock
    ReceivingInfoLineResponseConverter receivingInfoLineResponseConverter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        receivingInfoService.setMonthsPerShard(1);
        receivingInfoService.setMonthsToDisplay(12);
    }


    @Test
    public void testGetInfoServiceV1() {

        Map<String,String> mockMap = prepareMockMap();
        mockMap.put(ReceivingConstants.SCENARIO, ReceivingInfoRequestCombinations.TRANSACTIONID_TRANSACTIONSEQNBR.name());
        mockMap.put(ReceivingInfoRequestQueryParameters.INVOICEID.getQueryParam(),"InvoiceId1");
        when(financialTxnIntegrationService.getFinancialTxnDetails(Mockito.anyMap())).thenReturn(getFinancialTxnMock());
        when(receivingSummaryDao.executeSummaryAggregation(Mockito.anyList())).thenReturn(getReceivingSummaryMock());
        when(freightDao.executeQueryInFreight(Mockito.any(Query.class))).thenReturn(getFreightResponseList());
        when(receivingLineDao.executeLineAggregation(Mockito.anyList())).thenReturn(getReceivingLineList());
        ReceivingResponse receivingResponse  = receivingInfoService.getInfoServiceDataV1(mockMap);

        Assert.notNull(receivingResponse);

    }

    @Test
    public void getReceivingInfoWoFinTxn() {

        Map<String,String> mockMap = prepareMockMap();
        mockMap.put(ReceivingConstants.SCENARIO, ReceivingInfoRequestCombinations.TRANSACTIONID_TRANSACTIONSEQNBR.name());
        mockMap.put(ReceivingInfoRequestQueryParameters.INVOICEID.getQueryParam(),"InvoiceId1");
        when(financialTxnIntegrationService.getFinancialTxnDetails(Mockito.anyMap())).thenReturn(getFinancialTxnMock());
        when(receivingSummaryDao.executeSummaryAggregation(Mockito.anyList())).thenReturn(getReceivingSummaryMock());
        when(freightDao.executeQueryInFreight(Mockito.any(Query.class))).thenReturn(getFreightResponseList());
        when(receivingLineDao.executeLineAggregation(Mockito.anyList())).thenReturn(getReceivingLineList());
        List<ReceivingInfoResponseV1> receivingInfoResponseV1List =
                receivingInfoService.getReceivingInfoWoFinTxn(mockMap);

         Assert.notNull(receivingInfoResponseV1List);

    }

    @Test
    public void testAmalgamateReceivingInfoResponse() {

        Map<String,String> mockMap = prepareMockMap();
        List<ReceivingInfoResponseV1> receivingInfoResponseV1List = getReceivingInfoResponseMock();
        List<ReceivingInfoResponseV1> list = receivingInfoService.amalgamateReceivingInfoResponseV1(mockMap, receivingInfoResponseV1List, getFinancialTxnMock());

        Assert.notNull(list);
    }

    private List<ReceivingInfoResponseV1> getReceivingInfoResponseMock() {
        ReceivingInfoResponseV1 response = new ReceivingInfoResponseV1();
        response.setParentReceivingStoreNbr("3669");
        response.setPurchaseOrderId(Long.valueOf("999403403"));
        response.setInvoiceId(Long.valueOf(1828926897));
        response.setInvoiceNumber("000000004147570");
        response.setTransactionId(Long.valueOf("123"));
        response.setTxnCostAmt(9.0);
        response.setVendorDeptNbr(7777);
        response.setPoNbr("99987");
        response.setReceivingInfoLineResponses(Arrays.asList(new ReceivingInfoLineResponse()));

        return Arrays.asList(response);
    }

    private List<ReceivingLine> getReceivingLineList() {

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

        return receivingLines;
    }

    private List<FreightResponse> getFreightResponseList() {

        return Arrays.asList(new FreightResponse(new Long(31721227), "ARFW", "972035",123,
                "34567",20.00, LocalDate.now(),new Long(12),new Long(45),"4",
                0,"02","EA"));

    }

    private List<ReceiveSummary> getReceivingSummaryMock() {

        ReceiveSummary receiveSummary = new ReceiveSummary("999403403|0000030006|3669|0",
                "999403403", 3669, 18, 99,
                LocalDate.of(1996, 12, 12), LocalTime.of(18, 45, 21),
                0, 7688, 1111, 0, 0,
                "H", 0.0, 99.0, 'A',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56,
                22), LocalDate.of(2019, 03, 14),
                LocalDate.now(), 9.0, 7, "0",
                0, LocalDateTime.now(), 0, "0000030006", "yyyy",
                LocalDateTime.now(), "4665267"
                , 'K', "LLL", 0.0, new Long(999403403), null, null, null, LocalDateTime.of(2019, 03, 14, 8, 45, 21),
                null,null);

        return Arrays.asList(receiveSummary);
    }

    private List<FinancialTxnResponseData> getFinancialTxnMock() {

        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(123), 999403403L,
                "0000030006", 3669,
                495742, 1, 9.0, 7777, "99987", "USER",
                null, "USER", "1223", 1828926897L, "000000004147570", "Memo",
                "3669", null, "999403403", null
                , null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null);

        return Arrays.asList(financialTxnResponseData);
    }

    private Map<String, String> prepareMockMap() {

        Map mockMap = new HashMap();
        mockMap.put(ReceiveSummaryRequestParams.PURCHASEORDERNUMBER.getParameterName(), "999");
        mockMap.put(ReceiveSummaryRequestParams.CONTROLNUMBER.getParameterName(), "000");
        mockMap.put(ReceiveSummaryRequestParams.LOCATIONNUMBER.getParameterName(), "998");
        mockMap.put(ReceiveSummaryRequestParams.DEPARTMENTNUMBER.getParameterName(), "98");
        mockMap.put(ReceiveSummaryRequestParams.UPCNUMBERS.getParameterName(), "89776");
        mockMap.put(ReceiveSummaryRequestParams.VENDORNUMBER.getParameterName(), "0987");
        mockMap.put(ReceiveSummaryRequestParams.DIVISIONNUMBER.getParameterName(), "90");
        mockMap.put(ReceiveSummaryRequestParams.ITEMNUMBERS.getParameterName(), "9880");
        mockMap.put(ReceiveSummaryRequestParams.PURCHASEORDERID.getParameterName(), "456");
        mockMap.put(ReceiveSummaryRequestParams.RECEIPTNUMBERS.getParameterName(), "234");
        mockMap.put(ReceiveSummaryRequestParams.RECEIPTDATEEND.getParameterName(), "2017-12-12");
        mockMap.put(ReceiveSummaryRequestParams.RECEIPTDATESTART.getParameterName(), "2015-12-12");
        mockMap.put(ReceiveSummaryRequestParams.TRANSACTIONTYPE.getParameterName(), "0");
        mockMap.put(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam(),"W");
        return mockMap;
    }
}
