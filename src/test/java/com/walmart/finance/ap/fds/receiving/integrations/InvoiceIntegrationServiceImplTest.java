package com.walmart.finance.ap.fds.receiving.integrations;

import com.google.common.collect.Iterables;
import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.PUT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({InvoiceIntegrationServiceImpl.class})
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

        InvoiceResponse invoiceResponse = new InvoiceResponse("invoiceid","invoiceNumber","708588561","0708588561",null,"918","0","621680","90");
        InvoiceResponse[] invoiceResponseArray =  new InvoiceResponse[]{
                invoiceResponse
        };
        HashMap<String,String> paramMap = new HashMap<String,String>(){
            {
                put(ReceivingConstants.COUNTRYCODE, "US");
                put(ReceivingConstants.INVOICEID,"1234");
            }
        };
        String url = "https://api.qa.wal-mart.com/si/bofap/US/invoice/summary?invoiceId=1234";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(ReceivingConstants.WM_CONSUMER, invoiceIntegrationService.getConsumerId());
        requestHeaders.set(ReceivingConstants.WMAPIKEY, invoiceIntegrationService.getClientId());
        HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        ResponseEntity<InvoiceResponse[]> response = new ResponseEntity<>(invoiceResponseArray,HttpStatus.OK);

        when(restTemplate.exchange(url, HttpMethod.GET, entity, InvoiceResponse[].class)).thenReturn(response);
        assertArrayEquals(invoiceResponseArray,invoiceIntegrationService.getInvoice(paramMap));
    }
}