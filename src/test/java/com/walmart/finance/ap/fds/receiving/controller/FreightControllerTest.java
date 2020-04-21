
package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.service.FreightService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(FreightController.class)
public class FreightControllerTest {

    @Mock
    private FreightService freightService;

    @InjectMocks
    FreightController freightController;

    @Test
    public void updateFreightTest() {
        ReceivingResponse mockResponse = new ReceivingResponse();
        mockResponse.setData(null);
        mockResponse.setSuccess(true);
        mockResponse.setTimestamp(LocalDateTime.now());
        Mockito.when(freightService.getFreightById(any())).thenReturn(mockResponse);
        ReceivingResponse response = freightController.getFreightById("12345");
        Assert.assertNotNull(response);
    }

}



