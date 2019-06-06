package com.walmart.finance.ap.fds.receiving.integrations;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class InvoiceIntegrationServiceImpl  implements  InvoiceIntegrationService{



   // @Value("${invoice.clientId}")
    private String clientId;

    //@Value("${invoice.consumerId}")
    private String consumerId;

   // @Value("${invoice.base.url}")
    private String invoicebaseUrl;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public InvoiceResponse getInvoiceByInvoiceId(Long invoiceId) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        requestHeaders.set(ReceivingConstants.WM_CONSUMER, consumerId);
        requestHeaders.set(ReceivingConstants.WMAPIKEY, clientId);
        HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        InvoiceResponse invoiceResponse =null;
        ResponseEntity<InvoiceResponse> response = restTemplate.exchange(invoicebaseUrl, HttpMethod.GET,entity,InvoiceResponse.class);

        if(response!=null && response.getBody()!=null){
            invoiceResponse =response.getBody();
        }
        return invoiceResponse;
    }

    @Override
    public List<InvoiceResponse> getInvoiceByinvoiceNbr(String invoiceNbr) {
        return null;
    }
}
