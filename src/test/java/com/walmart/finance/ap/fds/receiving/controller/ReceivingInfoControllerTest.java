package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoLineResponse;
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

        ReceivingInfoLineResponse receivingInfoLineResponse = new ReceivingInfoLineResponse( new Long(30006), 1, 3777,
                2, 33.0,
                33.84, 2, 0,
                "N", "0000047875883980",
                "NSW CRASH TRNF", "LL", "ww", 1, "1.9", null);
        List<ReceivingInfoLineResponse> lineResponses = new ArrayList<ReceivingInfoLineResponse>() {
            {
                add(receivingInfoLineResponse);
            }
        };
        ReceivingInfoResponse response = new ReceivingInfoResponse("USER", LocalDate.of(2019, 03, 14), null,
                "0", 1, 99, 1, new Long(1), 3669,
                999403403, LocalDate.of(2019, 03, 14), new Long(30006),
                " ", 0.0, 0.0, null, 495742, "Memo",
                0.0, 0, "USER", "1223",
                3669, null, "999403403", 411276735,
                "411276735", lineResponses);
        List<ReceivingInfoResponse> list = new ArrayList<ReceivingInfoResponse>() {
            {
                add(response);
            }
        };
        ReceivingResponse successMessage = new ReceivingResponse(true, LocalDateTime.of(2019, 05, 12, 15, 31, 16), list);
        when(receivingInfoService.getInfoSeviceData(Mockito.anyMap())).thenReturn(successMessage);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/US/receiving/info")
                .param("invoiceId", "411276735")
                .param("lineNumberFlag", "Y")
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(
                        " {" +
                                "\"success\": true,\n" +
                                "\"timestamp\": \"2019-05-12T15:31:16\",\n" +
                                " \"data\": \n" +
                                "[{" +
                                "\"authorizedBy\": \"USER\",\n" +
                                "\"authorizedDate\": \"2019-03-14\",\n" +
                                "\"carrierCode\": null,\n" +
                                "\"controlNumber\": \"0\",\n" +
                                "\"departmentNumber\": 1,\n" +
                                "\"transactionType\": 99,\n" +
                                "\"divisionNumber\": 1,\n" +
                                "\"lineCount\": 1,\n" +
                                "\"locationNumber\": 3669,\n" +
                                "\"purchaseOrderId\": 999403403,\n" +
                                "\"receiptDate\": \"2019-03-14\",\n" +
                                "\"receiptNumber\": 30006,\n" +
                                "\"receiptStatus\": \" \",\n" +
                                "\"totalCostAmount\": 0,\n" +
                                "\"totalRetailAmount\": 0,\n" +
                                "\"trailerNumber\": null,\n" +
                                "\"vendorNumber\": 495742,\n" +
                                "\"memo\": \"Memo\",\n" +
                                "\"bottleDepositAmount\": 0,\n" +
                                "\"controlSequenceNumber\": 0,\n" +
                                "\"vendorName\": \"USER\",\n" +
                                "\"parentReceivingNbr\": \"1223\",\n" +
                                "\"parentReceivingStoreNbr\": 3669,\n" +
                                "\"parentReceivingDate\": null,\n" +
                                "\"parentPurchaseOrderId\": \"999403403\",\n" +
                                "\"invoiceId\": 411276735,\n" +
                                "\"invoiceNumber\": \"411276735\",\n" +
                                "\"receivingLine\": [\n" +
                                "{\n" +
                                "\"receiptNumber\": 30006,\n" +
                                "\"receiptLineNumber\": 1,\n" +
                                "\"itemNumber\": 3777,\n" +
                                "\"quantity\": 2,\n" +
                                "\"eachCostAmount\": 33,\n" +
                                "\"eachRetailAmount\": 33.84,\n" +
                                "\"numberofCasesReceived\": 2,\n" +
                                "\"packQuantity\": 0,\n" +
                                "\"bottleDepositFlag\": \"N\",\n" +
                                "\"upc\": \"0000047875883980\",\n" +
                                "\"itemDescription\": \"NSW CRASH TRNF\",\n" +
                                "\"unitOfMeasure\": \"LL\",\n" +
                                "\"variableWeightInd\": \"ww\",\n" +
                                "\"costMultiple\": 1,\n" +
                                "\"receivedWeightQuantity\": \"1.9\",\n" +
                                "\"merchandises\": null\n" +
                                "}\n" +
                                "]" +
                                "}]}"
                ))
                .andReturn();
    }
}
