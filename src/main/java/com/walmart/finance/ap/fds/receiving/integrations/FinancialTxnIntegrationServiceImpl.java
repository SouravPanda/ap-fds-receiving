package com.walmart.finance.ap.fds.receiving.integrations;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.common.ReceivingInfoQueryParamName;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class FinancialTxnIntegrationServiceImpl implements FinancialTxnIntegrationService {
    public static final Logger log = LoggerFactory.getLogger(FinancialTxnIntegrationServiceImpl.class);

    @Getter
    @Setter
    @Value("${financialTxn.clientId}")
    private String clientId;

    @Getter
    @Setter
    @Value("${financialTxn.consumerId}")
    private String consumerId;

    @Getter
    @Setter
    @Value("${financialTxn.base.url}")
    private String financialTxnBaseUrl;

    @Getter
    @Setter
    @Value("${financialTxn.base.endpoint}")
    private String financialTxnBaseEndpoint;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public FinancialTxnResponse[] getFinancialTxnDetails(Map<String, String> queryParamMap) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(ReceivingConstants.WM_CONSUMER, consumerId);
        requestHeaders.set(ReceivingConstants.WMAPIKEY, clientId);
        HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        FinancialTxnResponse[] financialTxnResponseArray = null;
        String url = financialTxnBaseUrl + queryParamMap.get(ReceivingInfoQueryParamName.COUNTRYCODE.getQueryParamName()) + financialTxnBaseEndpoint + queryParamMap.get(ReceivingInfoQueryParamName.INVOICEID.getQueryParamName());
        ResponseEntity<FinancialTxnResponse[]> response = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, FinancialTxnResponse[].class);
        } catch (HttpStatusCodeException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new NotFoundException("Financial Transaction data not found for given search criteria.");
        }
        if (response != null && response.getBody() != null && response.getBody().length > 0) {
            financialTxnResponseArray = response.getBody();
        } else {
            log.error("Financial Transaction data not found for invoice ID " + queryParamMap.get(ReceivingInfoQueryParamName.INVOICEID.getQueryParamName()));
            throw new NotFoundException("Financial Transaction data not found for given search criteria.");
        }
        return financialTxnResponseArray;
    }
}
