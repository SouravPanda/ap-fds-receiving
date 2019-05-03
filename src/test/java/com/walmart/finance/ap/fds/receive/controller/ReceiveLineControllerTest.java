package com.walmart.finance.ap.fds.receive.controller;

import com.walmart.finance.ap.fds.receiving.controller.ReceivingLineController;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineService;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;

@RunWith(SpringRunner.class)
@WebMvcTest(ReceivingLineController.class)
public class ReceiveLineControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReceiveLineServiceImpl receiveLineServiceImpl;

    @Before public void setup(){
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void getReceiveLineTest(){
        ReceivingLineResponse receivingLineResponse = new ReceivingLineResponse(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,5,null,null,11);
        Mockito.when(receiveLineServiceImpl.getLineSummary("112223","1111","1118","676","99",String.valueOf(LocalDate.of(1995,10,17)),String.valueOf(LocalTime.of(18,45,21,11)),"00")).thenReturn(receivingLineResponse);
    }
}
