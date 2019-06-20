package com.walmart.finance.ap.fds.receiving.integrations;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

public class FreightLineIntegrationServiceImplTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    FreightLineIntegrationServiceImpl freightLineIntegrationService;
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        freightLineIntegrationService.setClientId("ff0f3521-d398-4ab5-a8c4-67fe99da7eae");
        freightLineIntegrationService.setConsumerId("uH3jC2wQ6cD7lN0wY6kC0sY3qL1yL0qF7aN1tU1jB0dT6oI8fI");
        freightLineIntegrationService.setFreightUrl("https://api.qa.wal-mart.com/si/bofap/po/receiving/freight/billId/");
    }
    @Test
    public void getFreightLineAPIData() {
        ReceiveSummary receiveSummary = new ReceiveSummary();
        receiveSummary.set_id("0");
        receiveSummary.setFreightBillExpandID(new Long(1234));
        List<ReceiveSummary> receiveSummaries = new ArrayList<ReceiveSummary>(){
            {
                add(receiveSummary);
            }
        };
        FreightResponse freightResponse = new FreightResponse("1","RPS","91");
        String url = "https://api.qa.wal-mart.com/si/bofap/po/receiving/freight/billId/"+receiveSummaries.get(0).getFreightBillExpandID();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set(ReceivingConstants.WM_CONSUMER, freightLineIntegrationService.getConsumerId());
        requestHeaders.set(ReceivingConstants.WMAPIKEY, freightLineIntegrationService.getClientId());
        HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        ResponseEntity<FreightResponse> response = new ResponseEntity<>(freightResponse, HttpStatus.OK);

        when(restTemplate.exchange(url, HttpMethod.GET, entity, FreightResponse.class)).thenReturn(response);
        assertEquals(response.getBody().getCarrierCode(),freightLineIntegrationService.getFreightLineAPIData(receiveSummaries).get("0").getCarrierCode());
        assertEquals(response.getBody().getTrailerNbr(),freightLineIntegrationService.getFreightLineAPIData(receiveSummaries).get("0").getTrailerNbr());
    }
}