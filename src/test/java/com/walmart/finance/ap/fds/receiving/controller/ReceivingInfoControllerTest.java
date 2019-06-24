package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceivingInfoServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ReceivingInfoController.class)
public class ReceivingInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ReceivingInfoServiceImpl receivingInfoService;

    @Test
    public void getReceivingInfo() throws Exception {
        ReceivingInfoResponse response = new ReceivingInfoResponse("99987", 10441, 99,
                "164680544", 6302, 0, LocalDate.of(2019, 03, 14),
                'A', 2222, null, null, 9.0, 99.0,
                new Long(0), 0, null);
        List<ReceivingInfoResponse> list = new ArrayList<ReceivingInfoResponse>() {
            {
                add(response);
            }
        };
        ReceivingResponse successMessage = new ReceivingResponse(true, LocalDateTime.of(2019, 05, 12, 15, 31, 16), list);
        when(receivingInfoService.getSevice(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(successMessage);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/US/receiving/info")
                .param("controlNumber", "164680544")
                .accept(MediaType.APPLICATION_JSON);

/*        [
        {
            "purchaseOrderId": "99987",
                "receiptNumber": 10441,
                "transactionType": 99,
                "controlNumber": "164680544",
                "locationNumber": 6302,
                "divisionNumber": 0,
                "receiptDate": "2019-03-14",
                "receiptStatus": "A",
                "vendorNumber": 2222,
                "carrierCode": null,
                "trailerNumber": null,
                "totalCostAmount": 9,
                "totalRetailAmount": 99,
                "lineCount": 0,
                "departmentNumber": 0,
                "receivingInfoLineResponses": null
        }
]*/
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(
                        " {" +
                                "\"message\": true,\n" +
                                "\"timestamp\": \"2019-05-12T15:31:16\",\n" +
                                " \"data\": \n" +
                                "[{" +
                                "\"purchaseOrderId\": \"99987\", \n " +
                                "\"receiptNumber\": 10441, \n" +
                                "\"transactionType\": 99, \n" +
                                "\"controlNumber\": \"164680544\",\n" +
                                "\"locationNumber\": 6302,\n" +
                                "\"divisionNumber\": 0,\n" +
                                "\"receiptDate\": \"2019-03-14\",\n" +
                                "\"receiptStatus\": \"A\",\n" +
                                "\"vendorNumber\": 2222,\n" +
                                "\"carrierCode\": null,\n" +
                                "\"trailerNumber\": null,\n" +
                                "\"totalCostAmount\": 9,\n" +
                                "\"totalRetailAmount\": 99,\n" +
                                "\"lineCount\": 0,\n" +
                                "\"departmentNumber\": 0,\n" +
                                "\"receivingLine\": null\n" +
                                "}]}"
                ))
                .andReturn();
    }
}