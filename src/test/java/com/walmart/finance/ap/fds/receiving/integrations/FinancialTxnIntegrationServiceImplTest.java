package com.walmart.finance.ap.fds.receiving.integrations;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.exception.FinancialTransException;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestCombinations;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestQueryParameters;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

public class FinancialTxnIntegrationServiceImplTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    FinancialTxnIntegrationServiceImpl financialTxnIntegrationService;

    private String financialTxnHost;

    private HttpHeaders requestHeaders;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        financialTxnHost = "https://invc-fin-tran-d.dev01.gbs.ase.southcentralus.us.walmart.net/";
        requestHeaders = new HttpHeaders() {{
            String auth = "fdservices" + ":" + "fdservices";
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
        requestHeaders.set(ReceivingConstants.SM_WM_CONSUMER, financialTxnIntegrationService.getConsumerId());
        requestHeaders.set(ReceivingConstants.SM_WM_APP_NAME, financialTxnIntegrationService.getAppName());
        requestHeaders.set(ReceivingConstants.SM_WM_ENV, financialTxnIntegrationService.getAppEnv());
        financialTxnIntegrationService.setAppName("AP-FDS-INVOICE-FINANCIAL-TRANSACTION");
        financialTxnIntegrationService.setAppEnv("dev-us");
        financialTxnIntegrationService.setConsumerId("3fa1e5b2-6c55-4d0f-8ae5-634dbbb72865");
        financialTxnIntegrationService.setFinancialTxnBaseEndpoint("/invoice/financial/transaction/");
        financialTxnIntegrationService.setFinancialTxnBaseUrl("https://invc-fin-tran-d.dev01.gbs.ase.southcentralus.us.walmart.net/");
        financialTxnIntegrationService.setFinancialTxnAuthorizationKey("fdservices");
        financialTxnIntegrationService.setFinancialTxnAuthorizationValue("fdservices");
    }

    @Test
    public void getFinancialTxnDetails() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(123), 164680544, "10441", 6302,
                2222, 0, 9.0, 0, "99987",
                "USER", null, "VendorName",
                "1234", 1828926897, "1828926897", "Memo", "1223",
                null, "164680544", null
                , null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null);
        Map<String, String> queryParamMap = new HashMap<String, String>() {
            {
                put(ReceivingInfoRequestQueryParameters.COUNTRYCODE.getQueryParam(), "US");
                put(ReceivingInfoRequestQueryParameters.INVOICEID.getQueryParam(), "639050495");
                put("scenario", ReceivingInfoRequestCombinations.INVOICEID.name());
            }
        };
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        FinancialTxnResponse financialTxnResponse = new FinancialTxnResponse(financialTxnResponseDataList);
        String url = financialTxnHost + "US/invoice/financial/transaction/invoiceId/639050495";
        HttpHeaders requestHeaders = new HttpHeaders();

        HttpEntity<String> entity = new HttpEntity<>(this.requestHeaders);
        ResponseEntity<FinancialTxnResponse> response = new ResponseEntity<>(financialTxnResponse, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, FinancialTxnResponse.class)).thenReturn(response);
        compareResults(financialTxnResponseDataList, financialTxnIntegrationService.getFinancialTxnDetails(queryParamMap));
    }

    @Test
    public void getFinancialTxnDetailsNotFoundException() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(123), 164680544, "10441", 6302,
                2222, 0, 9.0, 0, "99987"
                , "USER", null, "VendorName", "1234",
                1828926897, "1828926897", "Memo", "1223", null,
                "164680544", null,
                null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null);
        Map<String, String> queryParamMap = new HashMap<String, String>() {
            {
                put(ReceivingInfoRequestQueryParameters.COUNTRYCODE.getQueryParam(), "2222");
                put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), "639050495");
                put(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam(), "99987");
                put(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam(), "1828926897");
                put("scenario", ReceivingInfoRequestCombinations.INVOICEID.name());
            }
        };
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        String url = financialTxnHost + "US/invoice/financial/transaction/vendorNumber/2222/poNumber/99987/invoiceNumber/1828926897";
        HttpEntity<String> entity = new HttpEntity<>(this.requestHeaders);
        HttpStatusCodeException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, FinancialTxnResponse.class)).thenThrow(exception);
        org.assertj.core.api.Assertions.assertThat(financialTxnIntegrationService.getFinancialTxnDetails(queryParamMap).isEmpty());
    }

    @Test
    public void getFinancialTxnDetailsResponseNullCheck() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(123), 164680544, "10441", 6302,
                2222, 0, 9.0, 0, "99987", "USER",
                null, "VendorName", "1234", 1828926897,
                "1828926897", "Memo", "1223", null, "164680544", null
                , null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null);
        Map<String, String> queryParamMap = new HashMap<String, String>() {
            {
                put(ReceivingInfoRequestQueryParameters.COUNTRYCODE.getQueryParam(), "US");
                put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), "639050495");
                put(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam(), "99987");
                put(ReceivingInfoRequestQueryParameters.RECEIPTNUMBERS.getQueryParam(), "6302");
                put(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam(), "1828926897");
                put("scenario", ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERNUMBER_RECEIPTNUMBERS.name());
            }
        };
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        String url = financialTxnHost + "US/invoice/financial/transaction/vendorNumber/2222/poNumber/99987/receiptNumber/6302?invoiceNumber=1828926897";
        HttpEntity<String> entity = new HttpEntity<>(this.requestHeaders);
        ResponseEntity<FinancialTxnResponse> response = null;
        when(restTemplate.exchange(url, HttpMethod.GET, entity, FinancialTxnResponse.class)).thenReturn(response);
        org.assertj.core.api.Assertions.assertThat(financialTxnIntegrationService.getFinancialTxnDetails(queryParamMap).isEmpty());
    }

    private void compareResults(List<FinancialTxnResponseData> receivingInfoResponses, List<FinancialTxnResponseData> result) {
        org.assertj.core.api.Assertions.assertThat(receivingInfoResponses.get(0)).isEqualToComparingFieldByFieldRecursively(result.get(0));
    }

    @Test(expected = FinancialTransException.class)
    public void httpStatusCodeException() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(123), 164680544, "10441", 6302,
                2222, 0, 9.0, 0, "99987", "USER",
                null, "VendorName", "1234", 1828926897,
                "1828926897", "Memo", "1223", null, "164680544", null
                , null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null);
        Map<String, String> queryParamMap = new HashMap<String, String>() {
            {
                put(ReceivingInfoRequestQueryParameters.COUNTRYCODE.getQueryParam(), "US");
                put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), "639050495");
                put(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam(), "99987");
                put(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam(), "6302");
                put(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam(), "1828926897");
                put("scenario", ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERNUMBER_LOCATIONNUMBER.name());
            }
        };
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        String url = financialTxnHost + "US/invoice/financial/transaction/vendorNumber/639050495/poNumber/99987/origStoreNbr/6302?invoiceNumber=1828926897";
        HttpEntity<String> entity = new HttpEntity<>(this.requestHeaders);
        HttpStatusCodeException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, FinancialTxnResponse.class)).thenThrow(exception);
        financialTxnIntegrationService.getFinancialTxnDetails(queryParamMap);
    }

    @Test
    public void geFinTxnVendorLocationInvoiceNum() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(123), 164680544, "10441", 6302,
                2222, 0, 9.0, 0, "99987",
                "USER", null, "VendorName",
                "1234", 1828926897, "1828926897", "Memo", "1223",
                null, "164680544", null
                , null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null);
        Map<String, String> queryParamMap = new HashMap<String, String>() {
            {
                put(ReceivingInfoRequestQueryParameters.COUNTRYCODE.getQueryParam(), "US");
                put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), "639050495");
                put(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam(), "6302");
                put(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam(), "1828926897");
                put("scenario", ReceivingInfoRequestCombinations.VENDORNUMBER_LOCATIONNUMBER_INVOICENUMBER.name());
            }
        };
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        FinancialTxnResponse financialTxnResponse = new FinancialTxnResponse(financialTxnResponseDataList);
        String url = financialTxnHost + "US/invoice/financial/transaction/vendorNumber/639050495/invoiceNumber/1828926897/origStoreNbr/6302";
        HttpEntity<String> entity = new HttpEntity<>(this.requestHeaders);
        ResponseEntity<FinancialTxnResponse> response = new ResponseEntity<>(financialTxnResponse, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, FinancialTxnResponse.class)).thenReturn(response);
        compareResults(financialTxnResponseDataList, financialTxnIntegrationService.getFinancialTxnDetails(queryParamMap));
    }

    @Test
    public void geFinTxnVendorLocationReceiptNum() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(123), 164680544, "10441", 6302,
                2222, 0, 9.0, 0, "99987",
                "USER", null, "VendorName",
                "1234", 1828926897, "1828926897", "Memo", "1223",
                null, "164680544", null
                , null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null);
        Map<String, String> queryParamMap = new HashMap<String, String>() {
            {
                put(ReceivingInfoRequestQueryParameters.COUNTRYCODE.getQueryParam(), "US");
                put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), "639050495");
                put(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam(), "6302");
                put(ReceivingInfoRequestQueryParameters.RECEIPTNUMBERS.getQueryParam(), "1828926897");
                put("scenario", ReceivingInfoRequestCombinations.VENDORNUMBER_LOCATIONNUMBER_RECEIPTNUMBERS.name());
            }
        };
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        FinancialTxnResponse financialTxnResponse = new FinancialTxnResponse(financialTxnResponseDataList);
        String url = financialTxnHost + "US/invoice/financial/transaction/vendorNumber/639050495/origStoreNbr/6302/receiptNumber/1828926897";
        HttpEntity<String> entity = new HttpEntity<>(this.requestHeaders);
        ResponseEntity<FinancialTxnResponse> response = new ResponseEntity<>(financialTxnResponse, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, FinancialTxnResponse.class)).thenReturn(response);
        compareResults(financialTxnResponseDataList, financialTxnIntegrationService.getFinancialTxnDetails(queryParamMap));
    }

    @Test
    public void geFinTxnVendorPurchaseOrderId() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(123), 164680544, "10441", 6302,
                2222, 0, 9.0, 0, "99987",
                "USER", null, "VendorName",
                "1234", 1828926897, "1828926897", "Memo", "1223",
                null, "164680544", null
                , null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null);
        Map<String, String> queryParamMap = new HashMap<String, String>() {
            {
                put(ReceivingInfoRequestQueryParameters.COUNTRYCODE.getQueryParam(), "US");
                put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), "639050495");
                put(ReceivingInfoRequestQueryParameters.PURCHASEORDERID.getQueryParam(), "1828926897");
                put("scenario", ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERID.name());
            }
        };
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        FinancialTxnResponse financialTxnResponse = new FinancialTxnResponse(financialTxnResponseDataList);
        String url = financialTxnHost + "US/invoice/financial/transaction/vendorNumber/639050495/purchaseOrderId/1828926897";
        HttpEntity<String> entity = new HttpEntity<>(this.requestHeaders);
        ResponseEntity<FinancialTxnResponse> response = new ResponseEntity<>(financialTxnResponse, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, FinancialTxnResponse.class)).thenReturn(response);
        compareResults(financialTxnResponseDataList, financialTxnIntegrationService.getFinancialTxnDetails(queryParamMap));
    }

    @Test
    public void geFinTxnLocationPurchaseOrderNum() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(123), 164680544, "10441", 6302,
                2222, 0, 9.0, 0, "99987",
                "USER", null, "VendorName",
                "1234", 1828926897, "1828926897", "Memo", "1223",
                null, "164680544", null
                , null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null);
        Map<String, String> queryParamMap = new HashMap<String, String>() {
            {
                put(ReceivingInfoRequestQueryParameters.COUNTRYCODE.getQueryParam(), "US");
                put(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam(), "6302");
                put(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam(), "1828926897");
                put("scenario", ReceivingInfoRequestCombinations.LOCATIONNUMBER_PURCHASEORDERNUMBER_RECEIPTDATESTART_RECEIPTDATEEND.name());
            }
        };
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        FinancialTxnResponse financialTxnResponse = new FinancialTxnResponse(financialTxnResponseDataList);
        String url = financialTxnHost + "US/invoice/financial/transaction/origStoreNbr/6302/poNumber/1828926897";
        HttpEntity<String> entity = new HttpEntity<>(this.requestHeaders);
        ResponseEntity<FinancialTxnResponse> response = new ResponseEntity<>(financialTxnResponse, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, FinancialTxnResponse.class)).thenReturn(response);
        compareResults(financialTxnResponseDataList, financialTxnIntegrationService.getFinancialTxnDetails(queryParamMap));
    }

    @Test
    public void geFinTxnLocationInvoiceNum() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(123), 164680544, "10441", 6302,
                2222, 0, 9.0, 0, "99987",
                "USER", null, "VendorName",
                "1234", 1828926897, "1828926897", "Memo", "1223",
                null, "164680544", null
                , null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null);
        Map<String, String> queryParamMap = new HashMap<String, String>() {
            {
                put(ReceivingInfoRequestQueryParameters.COUNTRYCODE.getQueryParam(), "US");
                put(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam(), "6302");
                put(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam(), "1828926897");
                put("scenario", ReceivingInfoRequestCombinations.LOCATIONNUMBER_INVOICENUMBER_RECEIPTDATESTART_RECEIPTDATEEND.name());
            }
        };
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        FinancialTxnResponse financialTxnResponse = new FinancialTxnResponse(financialTxnResponseDataList);
        String url = financialTxnHost + "US/invoice/financial/transaction/origStoreNbr/6302/invoiceNumber/1828926897";
        HttpEntity<String> entity = new HttpEntity<>(this.requestHeaders);
        ResponseEntity<FinancialTxnResponse> response = new ResponseEntity<>(financialTxnResponse, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, FinancialTxnResponse.class)).thenReturn(response);
        compareResults(financialTxnResponseDataList, financialTxnIntegrationService.getFinancialTxnDetails(queryParamMap));
    }

    @Test
    public void geFinTxnLocationVendorNum() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(123), 164680544, "10441", 6302,
                2222, 0, 9.0, 0, "99987",
                "USER", null, "VendorName",
                "1234", 1828926897, "1828926897", "Memo", "1223",
                null, "164680544", null
                , null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null);
        Map<String, String> queryParamMap = new HashMap<String, String>() {
            {
                put(ReceivingInfoRequestQueryParameters.COUNTRYCODE.getQueryParam(), "US");
                put(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam(), "6302");
                put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), "2222");
                put("scenario", ReceivingInfoRequestCombinations.LOCATIONNUMBER_VENDORNUMBER_RECEIPTDATESTART_RECEIPTDATEEND.name());
            }
        };
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        FinancialTxnResponse financialTxnResponse = new FinancialTxnResponse(financialTxnResponseDataList);
        String url = financialTxnHost + "US/invoice/financial/transaction/origStoreNbr/6302/vendorNumber/2222";
        HttpEntity<String> entity = new HttpEntity<>(this.requestHeaders);
        ResponseEntity<FinancialTxnResponse> response = new ResponseEntity<>(financialTxnResponse, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, FinancialTxnResponse.class)).thenReturn(response);
        compareResults(financialTxnResponseDataList, financialTxnIntegrationService.getFinancialTxnDetails(queryParamMap));
    }

    @Test
    public void geFinTxnVendorPurchaseOrderNumInvoiceNum() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(123), 164680544, "10441", 6302,
                2222, 0, 9.0, 0, "99987",
                "USER", null, "VendorName",
                "1234", 1828926897, "1828926897", "Memo", "1223",
                null, "164680544", null
                , null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null);
        Map<String, String> queryParamMap = new HashMap<String, String>() {
            {
                put(ReceivingInfoRequestQueryParameters.COUNTRYCODE.getQueryParam(), "US");
                put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), "2222");
                put(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam(), "164680544");
                put(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam(), "1828926897");
                put("scenario", ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERNUMBER_INVOICENUMBER.name());
            }
        };
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        FinancialTxnResponse financialTxnResponse = new FinancialTxnResponse(financialTxnResponseDataList);
        String url = financialTxnHost + "US/invoice/financial/transaction/vendorNumber/2222/poNumber/164680544/invoiceNumber/1828926897";
        HttpEntity<String> entity = new HttpEntity<>(this.requestHeaders);
        ResponseEntity<FinancialTxnResponse> response = new ResponseEntity<>(financialTxnResponse, HttpStatus.OK);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, FinancialTxnResponse.class)).thenReturn(response);
        compareResults(financialTxnResponseDataList, financialTxnIntegrationService.getFinancialTxnDetails(queryParamMap));
    }

    @Test
    public void httpStatusCodeExceptionTest2() {
        FinancialTxnResponseData financialTxnResponseData = new FinancialTxnResponseData(new Long(123), 164680544, "10441", 6302,
                2222, 0, 9.0, 0, "99987", "USER",
                null, "VendorName", "1234", 1828926897,
                "1828926897", "Memo", "1223", null, "164680544", null
                , null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null, null, null, null, null, null
                , null, null, null, null, null, null, null, null, null);
        Map<String, String> queryParamMap = new HashMap<String, String>() {
            {
                put(ReceivingInfoRequestQueryParameters.COUNTRYCODE.getQueryParam(), "US");
                put(ReceivingInfoRequestQueryParameters.VENDORNUMBER.getQueryParam(), "639050495");
                put(ReceivingInfoRequestQueryParameters.PURCHASEORDERNUMBER.getQueryParam(), "99987");
                put(ReceivingInfoRequestQueryParameters.LOCATIONNUMBER.getQueryParam(), "6302");
                put(ReceivingInfoRequestQueryParameters.INVOICENUMBER.getQueryParam(), "1828926897");
                put("scenario", ReceivingInfoRequestCombinations.VENDORNUMBER_PURCHASEORDERNUMBER_LOCATIONNUMBER.name());
            }
        };
        List<FinancialTxnResponseData> financialTxnResponseDataList = new ArrayList<>();
        financialTxnResponseDataList.add(financialTxnResponseData);
        String url = financialTxnHost + "US/invoice/financial/transaction/vendorNumber/639050495/poNumber/99987/origStoreNbr/6302?invoiceNumber=1828926897";
        HttpEntity<String> entity = new HttpEntity<>(this.requestHeaders);
        when(restTemplate.exchange(url, HttpMethod.GET, entity, FinancialTxnResponse.class)).thenReturn(null);
        financialTxnIntegrationService.getFinancialTxnDetails(queryParamMap);
    }
}
