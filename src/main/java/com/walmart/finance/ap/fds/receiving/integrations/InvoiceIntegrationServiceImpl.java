package com.walmart.finance.ap.fds.receiving.integrations;

import com.google.common.base.Enums;
import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class InvoiceIntegrationServiceImpl implements InvoiceIntegrationService {

    public static final Logger log = LoggerFactory.getLogger(InvoiceIntegrationServiceImpl.class);

    @Getter
    @Setter
    @Value("${invoice.clientId}")
    private String clientId;

    @Getter
    @Setter
    @Value("${invoice.consumerId}")
    private String consumerId;

    @Getter
    @Setter
    @Value("${invoice.base.url}")
    private String invoicebaseUrl;

    @Getter
    @Setter
    @Value("${invoice.base.endpoint}")
    private String invoiceBaseEndpoint;

    @Resource
    private RestTemplate restTemplate;

    /**
     * Method makes an call to in Invoice Summary Api and return the array of InvoiceResponse.
     *
     * @param paramMap
     * @return
     */
    @Override
    public InvoiceResponse[] getInvoice(HashMap<String, String> paramMap) {
        log.info("Inside getInvoice method");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(ReceivingConstants.WM_CONSUMER, consumerId);
        requestHeaders.set(ReceivingConstants.WMAPIKEY, clientId);
        HttpEntity<String> entity = new HttpEntity<>(requestHeaders);

        InvoiceResponse[] invoiceResponseArray = null;
        String url = makeInvoiceURL(paramMap);
        ResponseEntity<InvoiceResponse[]> response = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, InvoiceResponse[].class);
        } catch (HttpStatusCodeException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            if (!paramMap.containsKey(ReceivingConstants.PURCHASEORDERNUMBER)) {
                throw new NotFoundException("Receiving summary not found for given search criteria.");
            }
        }

        if (response != null && response.getBody() != null && response.getBody().length > 0) {
            invoiceResponseArray = response.getBody();
        }
        return invoiceResponseArray;
    }

    // TODO Need to check country code.
    private String makeInvoiceURL(HashMap<String, String> paramMap) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(invoicebaseUrl + paramMap.get(ReceivingConstants.COUNTRYCODE) + invoiceBaseEndpoint);
        paramMap.entrySet()
                .stream()
                .filter((t) -> Enums.getIfPresent(InvoiceQueryParameters.class, t.getKey()).isPresent())
                .forEach(y -> builder.queryParam(InvoiceQueryParameters.valueOf(y.getKey()).toString(), y.getValue()));
        return builder.toUriString();
    }


}


