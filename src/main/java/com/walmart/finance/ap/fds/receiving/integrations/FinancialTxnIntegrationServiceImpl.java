package com.walmart.finance.ap.fds.receiving.integrations;

import com.google.common.base.Enums;
import com.walmart.finance.ap.fds.receiving.exception.FinancialTransException;
import com.walmart.finance.ap.fds.receiving.mesh.FinancialTxnMeshHeadersGenerator;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestCombinations;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestQueryParameters;
import lombok.Data;
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
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Data
public class FinancialTxnIntegrationServiceImpl implements FinancialTxnIntegrationService {
    public static final Logger log = LoggerFactory.getLogger(FinancialTxnIntegrationServiceImpl.class);

    @Value("${financialTxn.base.url}")
    private String financialTxnBaseUrl;

    @Value("${financialTxn.base.endpoint}")
    private String financialTxnBaseEndpoint;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private FinancialTxnMeshHeadersGenerator meshHeadersGenerator;

    @Override
    public List<FinancialTxnResponseData> getFinancialTxnDetails(Map<String, String> allRequestParams) {
        HttpHeaders requestHeaders = meshHeadersGenerator.getRequestHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<>(requestHeaders);
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        String url = makeUrl(allRequestParams);
        log.info("Financial URL : " + url);
        ResponseEntity<FinancialTxnResponse> response;
        long startTime = System.currentTimeMillis();
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, FinancialTxnResponse.class);
            log.info("financialTransactionResponseTime :: "+(System.currentTimeMillis()-startTime));
        } catch (RestClientResponseException e) {
            log.info("financialTransactionResponseTime :: "+(System.currentTimeMillis()-startTime));
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
                        + "/origStoreNbr/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam());
                break;
            case VENDORNUMBER_LOCATIONNUMBER_INVOICENUMBER:
                url += "vendorNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam())
                        + "/invoiceNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam())
                        + "/origStoreNbr/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam());
                break;
            case VENDORNUMBER_LOCATIONNUMBER_RECEIPTNUMBERS:
                url += "vendorNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam())
                        + "/origStoreNbr/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam())
                        + "/receiptNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.RECEIPTNUMBERS.getQueryParam());
                break;
            case VENDORNUMBER_PURCHASEORDERID:
                url += "vendorNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam())
                        + "/purchaseOrderId/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.PURCHASEORDERID.getQueryParam());
                break;
            case LOCATIONNUMBER_PURCHASEORDERNUMBER_RECEIPTDATESTART_RECEIPTDATEEND:
                url += "origStoreNbr/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam())
                        + "/poNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam());
                break;
            case LOCATIONNUMBER_INVOICENUMBER_RECEIPTDATESTART_RECEIPTDATEEND:
                url += "origStoreNbr/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam())
                        + "/invoiceNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam());
                break;
            case LOCATIONNUMBER_VENDORNUMBER_RECEIPTDATESTART_RECEIPTDATEEND:
                url += "origStoreNbr/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam())
                        + "/vendorNumber/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam());
                break;
            case LOCATIONNUMBER_RECEIPTDATESTART_RECEIPTDATEEND:
                url += "/locationType/S/origStoreNbr/" + allRequestParamsClone.remove(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam());
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
