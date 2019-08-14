
package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ReceivingLineController.class)
public class ReceivingLineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReceiveLineServiceImpl receiveLineService;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getReceiveLine() throws Exception {


        ReceivingLineResponse response = new ReceivingLineResponse(new Long(999997), 0, null, 366404, 2000, 0.0, 0.0, 1, 0, null, "553683865", "lbs", " ",
                " ", 99, null, 6565, 0, "A",null);

        List<ReceivingLineResponse> responseList = new ArrayList<ReceivingLineResponse>() {
            {
                add(response);
            }
        };
        ReceivingResponse successMessage = new ReceivingResponse(true, LocalDateTime.of(2019, 05, 12, 15, 31, 16), responseList);

        when(receiveLineService.getLineSummary(Mockito.anyMap())).thenReturn(successMessage);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/US/receiving/line")
                .param("controlNumber", "553683865")
                .accept(MediaType.APPLICATION_JSON);


        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(

                        " {" +
                                "\"success\": true,\n" +
                                "\"timestamp\": \"2019-05-12T15:31:16\",\n" +
                                "    \"data\": [\n" +
                                "        {\n" +
                                "\"receiptNumber\": 999997,\n" +
                                "\"receiptLineNumber\": 0,\n" +
                                "\"itemNumber\": null,\n" +
                                "\"vendorNumber\": 366404,\n" +
                                "\"quantity\": 2000,\n" +
                                "\"eachCostAmount\": 0,\n" +
                                "\"eachRetailAmount\": 0,\n" +
                                "\"packQuantity\": 1,\n" +
                                "\"numberofCasesReceived\": 0,\n" +
                                "\"purchaseOrderId\": \"553683865\",\n" +
                                "\"unitOfMeasure\": \"lbs\",\n" +
                                "\"variableWeightInd\": \" \",\n" +
                                "\"receivedWeightQuantity\": \" \",\n" +
                                "\"transactionType\": 99,\n" +
                                "\"locationNumber\": 6565,\n" +
                                "\"divisionNumber\": 0,\n" +
                                "\"bottleDepositFlag\":\"A\" \n"+
                                "                }\n" +
                                "    ]\n" +
                                "}"))
                .andReturn();
    }
}



