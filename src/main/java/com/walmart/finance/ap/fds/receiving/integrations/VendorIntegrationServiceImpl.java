package com.walmart.finance.ap.fds.receiving.integrations;


import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.response.FoundationSupplierWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Arrays;


@Service
public class VendorIntegrationServiceImpl implements VendorIntegrationService {

    @Resource
    private RestTemplate restTemplate;

    @Value("${vendor.base.url}")
    private String vendorBaseUrl;

    @Override
    public Integer getVendorBySupplierNumberAndCountryCode(Integer supplierNumber, String countryCode){

        HttpHeaders requestHeaders = new HttpHeaders();
        //requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
       // requestHeaders.set(ReceivingConstants.CORRELATION_ID_HEADER_KEY,"123");
        requestHeaders.set(ReceivingConstants.CLIENT_SECRET_ID_HEADER_KEY,"rK7xP5iW4uV0gI4mA0dM8yQ3dV8tW2hG6nB8uA5mY1kA4sQ8oB");
        requestHeaders.set(ReceivingConstants.CLIENT_ID_HEADER_KEY,"e684e483-dbba-41d5-a040-6043b48798f5");
        String url = vendorBaseUrl+"/supplierNumber/"+supplierNumber+"/countryCode/"+countryCode;
        HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        ResponseEntity<FoundationSupplierWrapper> response = restTemplate.exchange(url, HttpMethod.GET,entity,FoundationSupplierWrapper.class);
        return response.getBody().getFoundationSupplierList().get(0).getSupplierNumber();
    }
}
