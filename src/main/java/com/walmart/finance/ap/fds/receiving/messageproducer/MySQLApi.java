package com.walmart.finance.ap.fds.receiving.messageproducer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.walmart.finance.ap.fds.receiving.common.Details;
import com.walmart.finance.ap.fds.receiving.common.Error;
import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.response.MySQLApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class MySQLApi {

    @Autowired
    RestTemplate restTemplate;

    @Value("${mesh.consumerId}")
    private String consumerId;

    @Value("${mysql.service.name}")
    private String serviceName;

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${mysql.url}")
    private String mysqlUrl;

    public static final Logger log = LoggerFactory.getLogger(Producer.class);

    public void saveFailureRecordTOMysql(ObjectNode message) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        //headers.add("Content-Type", "application/json");
        headers.add(ReceivingConstants.SM_WM_APP_NAME, serviceName);
        headers.add(ReceivingConstants.SM_WM_ENV, profile);
        headers.add(ReceivingConstants.SM_WM_CONSUMER, consumerId);
        ObjectNode valueTree=builfailureMessage(message);
        try {
            URI uri = new URI(mysqlUrl);
            HttpEntity<ObjectNode> request = new HttpEntity<>(valueTree, headers);
            ResponseEntity<MySQLApiResponse> response = restTemplate.postForEntity(uri, request, MySQLApiResponse.class);
            log.info("Successfully updated the MySQL failure table with the failure record " + response);
        } catch (RestClientException exe) {
            log.error("exception while calling Audit API to save the failure record to MySQL failure table  " + exe);
        } catch (URISyntaxException exe) {
            log.error("exception while forming URI to make the rest call to MySQL Audit API " + exe);
        }
    }

    private ObjectNode builfailureMessage(ObjectNode failureMessage) {
        Details details = new Details();
        details.setDescription(ReceivingConstants.EVENT_HUB_NETWORK_ERROR);
        List<Details> detailList = new ArrayList<>();
        detailList.add(details);
        Error error = new Error();
        error.setMessage(ReceivingConstants.EVENT_HUB_NETWORK_ERROR);
        error.setErrorCode(ReceivingConstants.ERROR_CODE);
        error.setDetails(detailList);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String value = mapper.writeValueAsString(error);
            ObjectNode valueTree = (ObjectNode) mapper.readTree(value);
            failureMessage.put(ReceivingConstants.SERVICE, ReceivingConstants.SERVICE_NAME);
            failureMessage.set(ReceivingConstants.ERROR, valueTree);
        } catch (IOException exe) {
            log.error("exception while forming error JSON structure for the failure message " + exe);
        }
        return failureMessage;
    }
}
