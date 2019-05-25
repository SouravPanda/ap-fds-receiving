package com.walmart.finance.ap.fds.receiving.integrations;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
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

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FreightLineIntegrationServiceImpl implements FreightLineIntegrationService {

    public static final Logger log = LoggerFactory.getLogger(FreightLineIntegrationServiceImpl.class);

    @Getter
    @Setter
    @Value("${freight.clientId}")
    private String clientId;

    @Getter
    @Setter
    @Value("${freight.consumerId}")
    private String consumerId;
    @Getter
    @Setter
    @Value("${freight.receive.url}")
    private String freightUrl;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public Map<String, FreightResponse> getFreightLineAPIData(List<ReceiveSummary> receiveSummaries) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(ReceivingConstants.WM_CONSUMER, consumerId);
        requestHeaders.set(ReceivingConstants.WMAPIKEY, clientId);
        HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        Map<String, FreightResponse> freightResponseMap = new HashMap<>();
        for (ReceiveSummary receiveSummary : receiveSummaries) {
            Integer buildId = receiveSummary.getFreightBillId();
            if (buildId == null) {
                continue;
            }
            String url = freightUrl + buildId.toString();
            try {
                ResponseEntity<FreightResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, FreightResponse.class);
                if (response != null && response.getBody() != null) {
                    freightResponseMap.put(receiveSummary.get_id(), response.getBody());
                }
            } catch (HttpStatusCodeException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return freightResponseMap;
    }
}
