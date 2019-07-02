package com.walmart.finance.ap.fds.receiving.integrations;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.common.ReceivingInfoQueryParamName;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.when;

public class FinancialTxnIntegrationServiceImplTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    FinancialTxnIntegrationServiceImpl financialTxnIntegrationService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        financialTxnIntegrationService.setClientId("2c47c821-72b9-48cb-89b0-ea97d8fbb250");
        financialTxnIntegrationService.setConsumerId("lJ3hR4wL3nI7wH2qI4vI5cS2lF2bU3kH4dR4kI8yX0oL5jC2wW");
        financialTxnIntegrationService.setFinancialTxnBaseEndpoint("/invoice/financial/transaction/invoiceId/");
        financialTxnIntegrationService.setFinancialTxnBaseUrl("https://api.dev.wal-mart.com/bofap/dev/bofap/");
    }

    @Test
    public void getFinancialTxnDetails() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(123, 164680544, "10441", 6302, 2222, 0, 9.0, 0, "99987");
        Map<String, String> queryParamMap = new HashMap<String, String>() {
            {
                put(ReceivingInfoQueryParamName.COUNTRYCODE.getQueryParamName(), "US");
                put(ReceivingInfoQueryParamName.INVOICEID.getQueryParamName(), "639050495");
            }
        };
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        FinancialTxnResponse financialTxnResponse = new FinancialTxnResponse(financialTxnResponseDataList);
        String url = "https://api.dev.wal-mart.com/bofap/dev/bofap/US/invoice/financial/transaction/invoiceId/639050495";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(ReceivingConstants.WM_CONSUMER, financialTxnIntegrationService.getConsumerId());
        requestHeaders.set(ReceivingConstants.WMAPIKEY, financialTxnIntegrationService.getClientId());
        HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        ResponseEntity<FinancialTxnResponse> response = new ResponseEntity<>(financialTxnResponse, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, FinancialTxnResponse.class)).thenReturn(response);
        compareResults(financialTxnResponseDataList, financialTxnIntegrationService.getFinancialTxnDetails(queryParamMap));
    }

    @Test(expected = NotFoundException.class)
    public void getFinancialTxnDetailsNotFoundException() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(123, 164680544, "10441", 6302, 2222, 0, 9.0, 0, "99987");
        Map<String, String> queryParamMap = new HashMap<String, String>() {
            {
                put(ReceivingInfoQueryParamName.COUNTRYCODE.getQueryParamName(), "US");
                put(ReceivingInfoQueryParamName.INVOICEID.getQueryParamName(), "639050495");
            }
        };
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        String url = "https://api.dev.wal-mart.com/bofap/dev/bofap/US/invoice/financial/transaction/invoiceId/639050495";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(ReceivingConstants.WM_CONSUMER, financialTxnIntegrationService.getConsumerId());
        requestHeaders.set(ReceivingConstants.WMAPIKEY, financialTxnIntegrationService.getClientId());
        HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        HttpStatusCodeException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, FinancialTxnResponse.class)).thenThrow(exception);
        compareResults(financialTxnResponseDataList, financialTxnIntegrationService.getFinancialTxnDetails(queryParamMap));
    }

    @Test(expected = NotFoundException.class)
    public void getFinancialTxnDetailsResponseNullCheck() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(123, 164680544, "10441", 6302, 2222, 0, 9.0, 0, "99987");
        Map<String, String> queryParamMap = new HashMap<String, String>() {
            {
                put(ReceivingInfoQueryParamName.COUNTRYCODE.getQueryParamName(), "US");
                put(ReceivingInfoQueryParamName.INVOICEID.getQueryParamName(), "639050495");
            }
        };
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        String url = "https://api.dev.wal-mart.com/bofap/dev/bofap/US/invoice/financial/transaction/invoiceId/639050495";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(ReceivingConstants.WM_CONSUMER, financialTxnIntegrationService.getConsumerId());
        requestHeaders.set(ReceivingConstants.WMAPIKEY, financialTxnIntegrationService.getClientId());
        HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        ResponseEntity<FinancialTxnResponse> response = null;
        when(restTemplate.exchange(url, HttpMethod.GET, entity, FinancialTxnResponse.class)).thenReturn(response);
        compareResults(financialTxnResponseDataList, financialTxnIntegrationService.getFinancialTxnDetails(queryParamMap));
    }

    private void compareResults(List<FinancialTxnResponseData> receivingInfoResponses, List<FinancialTxnResponseData> result) {
        org.assertj.core.api.Assertions.assertThat(receivingInfoResponses.get(0)).isEqualToComparingFieldByFieldRecursively(result.get(0));
    }
}