package com.walmart.finance.ap.fds.receiving.integrations;

import com.google.common.base.Enums;
import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.exception.FinancialTransException;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestCombinations;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestQueryParameters;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Getter
    @Setter
    @Value("${financialTxn.authorizationKey}")
    private String financialTxnAuthorizationKey;

    @Getter
    @Setter
    @Value("${financialTxn.authorizationValue}")
    private String financialTxnAuthorizationValue;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public List<FinancialTxnResponseData> getFinancialTxnDetails(Map<String, String> allRequestParams) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(ReceivingConstants.WM_CONSUMER, consumerId);
        requestHeaders.set(ReceivingConstants.WMAPIKEY, clientId);
        HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        HttpHeaders basicAuthHeader = new HttpHeaders() {{
            String auth = financialTxnAuthorizationKey + ":" + financialTxnAuthorizationValue;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
        HttpEntity<String> basicAuthEntity = new HttpEntity<>(basicAuthHeader);
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        String url = makeUrl(allRequestParams);
        ResponseEntity<FinancialTxnResponse> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, basicAuthEntity, FinancialTxnResponse.class);
        } catch (HttpStatusCodeException e) {
            log.error("Failed to get response from Financial Transaction.", e);
            throw new FinancialTransException("Failed to get response from Financial Transaction.");
        }
        if (response != null && response.getBody() != null && CollectionUtils.isNotEmpty(response.getBody().getFinancialTxnResponseDataList())) {
            financialTxnResponseDataList = response.getBody().getFinancialTxnResponseDataList();
        } else {
            log.error("Financial Transaction data not found for url " + url);
        }
        return financialTxnResponseDataList;
    }

    private String makeUrl(Map<String, String> allRequestParams) {
        Map<String, String> allRequestParamsClone = new HashMap<>(allRequestParams);
        //Removing 'Transaction Type' from params as it is not applicable for Financial Transactions
        allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.TRANSACTIONTYPE.getQueryParam());
        String url = financialTxnBaseUrl + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.COUNTRYCODE.getQueryParam()) + financialTxnBaseEndpoint;
        ReceivingInfoRequestCombinations combination = ReceivingInfoRequestCombinations.valueOf(allRequestParamsClone.remove("scenario"));
        switch (combination) {
            case INVOICEID:
                url += "invoiceId/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.INVOICEID.getQueryParam());
                break;
            case VENDORNUMBER_PURCHASEORDERNUMBER_INVOICENUMBER:
                url += "vendorNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam())
                        + "/poNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam())
                        + "/invoiceNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam());
                break;
            case VENDORNUMBER_PURCHASEORDERNUMBER_RECEIPTNUMBERS:
                url += "vendorNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam())
                        + "/poNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam())
                        + "/receiptNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.RECEIPTNUMBERS.getQueryParam());
                break;
            case VENDORNUMBER_PURCHASEORDERNUMBER_LOCATIONNUMBER:
                url += "vendorNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam())
                        + "/poNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam())
                        + "/storeNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam());
                break;
            case VENDORNUMBER_LOCATIONNUMBER_INVOICENUMBER:
                url += "vendorNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam())
                        + "/invoiceNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam())
                        + "/storeNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam());
                break;
            case VENDORNUMBER_LOCATIONNUMBER_RECEIPTNUMBERS:
                url += "vendorNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam())
                        + "/storeNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam())
                        + "/receiptNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.RECEIPTNUMBERS.getQueryParam());
                break;
            case VENDORNUMBER_PURCHASEORDERID:
                url += "vendorNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam())
                        + "/purchaseOrderId/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.PURCHASEORDERID.getQueryParam());
                break;
            case LOCATIONNUMBER_PURCHASEORDERNUMBER_RECEIPTDATESTART_RECEIPTDATEEND:
                url += "storeNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam())
                        + "/poNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam());
                break;
            case LOCATIONNUMBER_INVOICENUMBER_RECEIPTDATESTART_RECEIPTDATEEND:
                url += "storeNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam())
                        + "/invoiceNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam());
                break;
            case LOCATIONNUMBER_VENDORNUMBER_RECEIPTDATESTART_RECEIPTDATEEND:
                url += "storeNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam())
                        + "/vendorNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam());
                break;
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        allRequestParamsClone.entrySet()
                .stream()
                .filter(t -> Enums.getIfPresent(FinancialTxnRequestQueryParameters.class, t.getKey().toUpperCase()).isPresent())
                .forEach(y -> builder.queryParam(FinancialTxnRequestQueryParameters.valueOf(y.getKey().toUpperCase()).getFinTxnRequestQueryParam(), y.getValue()));
        return builder.toUriString();
    }
}
