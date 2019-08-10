package com.walmart.finance.ap.fds.receiving.integrations;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
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

import static org.junit.Assert.assertNull;
import static org.powermock.api.mockito.PowerMockito.when;

public class InvoiceIntegrationServiceImplTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    InvoiceIntegrationServiceImpl invoiceIntegrationService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        invoiceIntegrationService.setClientId("ce40d438-985f-4468-83c4-42552d77edd3");
        invoiceIntegrationService.setConsumerId("D0mO7gL6rO7dI3iS8dL8lJ7iM3hX7kU1jQ8cH5sQ2iW8tK2dC1");
        invoiceIntegrationService.setInvoiceBaseEndpoint("/invoice/summary?");
        invoiceIntegrationService.setInvoicebaseUrl("https://api.qa.wal-mart.com/si/bofap/");
    }

    @Test
    public void getInvoice() {
        InvoiceResponseData invoiceResponseData = new InvoiceResponseData("invoiceid", "invoiceNumber", "708588561", "0708588561", null, "918", "0", "621680", "90",null);
        List<InvoiceResponseData> invoiceResponseDataList = new ArrayList<>();
        invoiceResponseDataList.add(invoiceResponseData);
        InvoiceResponse invoiceResponse = new InvoiceResponse(invoiceResponseDataList);
        HashMap<String, String> paramMap = new HashMap<String, String>() {
            {
                put(ReceivingConstants.COUNTRYCODE, "US");
                put(ReceivingConstants.INVOICEID, "1234");
            }
        };
        String url = "https://api.qa.wal-mart.com/si/bofap/US/invoice/summary?invoiceId=1234";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(ReceivingConstants.WM_CONSUMER, invoiceIntegrationService.getConsumerId());
        requestHeaders.set(ReceivingConstants.WMAPIKEY, invoiceIntegrationService.getClientId());
        HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        ResponseEntity<InvoiceResponse> response = new ResponseEntity<>(invoiceResponse, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, InvoiceResponse.class)).thenReturn(response);
        List<InvoiceResponseData> result = invoiceIntegrationService.getInvoice(paramMap);
        compareResults(invoiceResponseDataList, result);
    }

    private void compareResults(List<InvoiceResponseData> receivingInfoResponses, List<InvoiceResponseData> result) {
        org.assertj.core.api.Assertions.assertThat(receivingInfoResponses.get(0)).isEqualToComparingFieldByFieldRecursively(result.get(0));
    }

    @Test(expected = NotFoundException.class)
    public void getInvoiceException() {
        InvoiceResponseData invoiceResponseData = new InvoiceResponseData("invoiceid", "invoiceNumber", "708588561", "0708588561", null, "918", "0", "621680", "90",null);
        List<InvoiceResponseData> invoiceResponseDataList = new ArrayList<>();
        invoiceResponseDataList.add(invoiceResponseData);
        InvoiceResponse invoiceResponse = new InvoiceResponse(invoiceResponseDataList);
        HashMap<String, String> paramMap = new HashMap<String, String>() {
            {
                put(ReceivingConstants.COUNTRYCODE, "US");
                put(ReceivingConstants.INVOICEID, "1234");
            }
        };
        String url = "https://api.qa.wal-mart.com/si/bofap/US/invoice/summary?invoiceId=1234";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(ReceivingConstants.WM_CONSUMER, invoiceIntegrationService.getConsumerId());
        requestHeaders.set(ReceivingConstants.WMAPIKEY, invoiceIntegrationService.getClientId());
        HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        ResponseEntity<InvoiceResponse> response = new ResponseEntity<>(invoiceResponse, HttpStatus.OK);
        HttpStatusCodeException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, InvoiceResponse.class)).thenThrow(exception);
        invoiceIntegrationService.getInvoice(paramMap);
    }

    @Test
    public void getInvoiceExceptionNot() {
        InvoiceResponseData invoiceResponseData = new InvoiceResponseData("invoiceid", "invoiceNumber", "708588561", "0708588561", null, "918", "0", "621680", "90",null);
        List<InvoiceResponseData> invoiceResponseDataList = new ArrayList<>();
        invoiceResponseDataList.add(invoiceResponseData);
        InvoiceResponse invoiceResponse = new InvoiceResponse(invoiceResponseDataList);
        HashMap<String, String> paramMap = new HashMap<String, String>() {
            {
                put(ReceivingConstants.COUNTRYCODE, "US");
                put(ReceivingConstants.INVOICEID, "1234");
                put(ReceivingConstants.PURCHASEORDERNUMBER,"123");
            }
        };
        String url = "https://api.qa.wal-mart.com/si/bofap/US/invoice/summary?invoiceId=1234";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(ReceivingConstants.WM_CONSUMER, invoiceIntegrationService.getConsumerId());
        requestHeaders.set(ReceivingConstants.WMAPIKEY, invoiceIntegrationService.getClientId());
        HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        ResponseEntity<InvoiceResponse> response = new ResponseEntity<>(invoiceResponse, HttpStatus.OK);
        HttpStatusCodeException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, InvoiceResponse.class)).thenThrow(exception);
        assertNull(invoiceIntegrationService.getInvoice(paramMap));
    }
}