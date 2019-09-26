
package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.config.DefaultValuesConfigProperties;
import com.walmart.finance.ap.fds.receiving.request.Meta;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.request.SorRoutingCtx;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveSummaryServiceImpl;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ReceivingSummaryController.class)
public class ReceivingSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReceiveSummaryServiceImpl receiveSummaryService;

    @MockBean
    DefaultValuesConfigProperties defaultValuesConfigProperties;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeClass
    public static void setSystemProperty() {
        Properties properties = System.getProperties();
        properties.setProperty("spring.profiles.active", "dev-us");
    }

    @AfterClass
    public static void removeSystemProperty() {
        System.clearProperty("spring.profiles.active");
    }

    @Test
    public void updateSummaryTest() {
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);
        ReceivingSummaryRequest receivingSummaryRequest = new ReceivingSummaryRequest("2", "2", LocalDate.now(), 2, "A", meta);
        List<ReceivingSummaryRequest> responseList = new ArrayList<>();
        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setSuccess(true);
        responseList.add(receivingSummaryRequest);
        successMessage.setData(responseList);
        successMessage.setTimestamp(LocalDateTime.now());
        Mockito.when(receiveSummaryService.updateReceiveSummary(receivingSummaryRequest, "US")).thenReturn(successMessage);
        String body = "{\n" +
                "\"receiveId\" : \"645099\",\n" +
                "\"purchaseOrderId\": \"1022497259\",\n" +
                "\"receiveDate\": \"1970-01-01\",\n" +
                "\"locationNumber\":8231,\n" +
                " \"businessStatusCode\": \"M\",\n" +
                " \"meta\": {\n" +
                "   \"unitOfWorkId\": \"12122\",\n" +
                "   \"sorRoutingCtx\": {\n" +
                "     \"replnTypCd\": \"R\",\n" +
                "     \"invProcAreaCode\": 30,\n" +
                "     \"locationCountryCd\": \"US\"\n" +
                "   }\n" +
                " }\n" +
                "}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/US/receiving/summary")
                .accept(MediaType.APPLICATION_JSON)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            mockMvc.perform(requestBuilder)
                    .andExpect(content().json(body))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\n" +
                            "    \"success\": true,\n" +
                            "    \"timestamp\": \"2019-07-29T00:35:10.17\",\n" +
                            "    \"data\": [\n" +
                            "        {\n" +
                            "            \"receiveId\": \"645099\",\n" +
                            "            \"purchaseOrderId\": \"1022497259\",\n" +
                            "            \"receiveDate\": \"1970-01-01\",\n" +
                            "            \"locationNumber\": 8231,\n" +
                            "            \"businessStatusCode\": \"M\",\n" +
                            "            \"meta\": {\n" +
                            "                \"unitOfWorkId\": \"12122\",\n" +
                            "                \"sorRoutingCtx\": {\n" +
                            "                    \"replnTypCd\": \"R\",\n" +
                            "                    \"invProcAreaCode\": 30,\n" +
                            "                    \"locationCountryCd\": \"US\"\n" +
                            "                }\n" +
                            "            }\n" +
                            "        }\n" +
                            "    ]\n" +
                            "}"))
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateSummaryAndLineTest() {
        Meta meta = new Meta();
        SorRoutingCtx sorRoutingCtx = new SorRoutingCtx();
        sorRoutingCtx.setInvProcAreaCode(36);
        sorRoutingCtx.setLocationCountryCd("US");
        sorRoutingCtx.setReplnTypCd("R");
        meta.setSorRoutingCtx(sorRoutingCtx);
        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("2", "2", LocalDate.now(), 2,
                "A", "1", "9", meta);
        ReceivingResponse successMessage = new ReceivingResponse();
        List<ReceivingSummaryLineRequest> responseList = new ArrayList<>();
        successMessage.setSuccess(true);
        responseList.add(receivingSummaryLineRequest);
        successMessage.setData(responseList);
        successMessage.setTimestamp(LocalDateTime.now());
        Mockito.when(receiveSummaryService.updateReceiveSummaryAndLine(receivingSummaryLineRequest, "US")).thenReturn(successMessage);
        String body = "{\n" +
                "\"receiveId\" : \"999997\",\n" +
                "\"purchaseOrderId\": \"553683865\",\n" +
                "\"receiveDate\": \"2019-05-12\",\n" +
                "\"locationNumber\":\"6565\",\n" +
                "\"businessStatusCode\": \"A\",\n" +
                "\"inventoryMatchStatus\":\"0\",\n" +
                "\"lineSequenceNumber\": \"4\",\n" +
                "\"meta\": {\n" +
                "    \"unitOfWorkId\": \"12122\",\n" +
                "    \"sorRoutingCtx\": {\n" +
                "        \"replnTypCd\": \"R\",\n" +
                "        \"invProcAreaCode\": 30,\n" +
                "        \"locationCountryCd\": \"US\"\n" +
                "                     }\n" +
                "       }\n" +
                "}";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/US/receiving/summary/line")
                .accept(MediaType.APPLICATION_JSON)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            mockMvc.perform(requestBuilder)
                    .andExpect(content().json(body))
                    .andExpect(status().isAccepted())
                    .andExpect(content().json("{\n" +
                            "    \"success\": true,\n" +
                            "    \"timestamp\": \"2019-07-12T12:54:52.604\",\n" +
                            "    \"data\": [\n" +
                            "        {\n" +
                            "            \"receiveId\": \"999997\",\n" +
                            "            \"purchaseOrderId\": \"553683865\",\n" +
                            "            \"receiveDate\": \"2019-05-12\",\n" +
                            "            \"locationNumber\": 6565,\n" +
                            "            \"businessStatusCode\": \"A\",\n" +
                            "            \"lineSequenceNumber\": \"4\",\n" +
                            "            \"inventoryMatchStatus\": \"0\",\n" +
                            "            \"meta\": {\n" +
                            "                \"unitOfWorkId\": 12122,\n" +
                            "                \"sorRoutingCtx\": {\n" +
                            "                    \"replnTypCd\": \"R\",\n" +
                            "                    \"invProcAreaCode\": 30,\n" +
                            "                    \"locationCountryCd\": \"US\"\n" +
                            "                }\n" +
                            "            }\n" +
                            "        }\n" +
                            "    ]\n" +
                            "}"))
                    .andReturn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getReceiveSummary() throws Exception {
        ReceivingSummaryResponse response = new ReceivingSummaryResponse("984003673", new Long(10022), 0, "984003673", 3680,
                28, LocalDate.of(2019, 01, 03), 'M', 762214, null, "0", 0.0, 0.0,
                null, 96, 0, 10.0
        );
        List<ReceivingSummaryResponse> responseList = new ArrayList<ReceivingSummaryResponse>() {
            {
                add(response);
            }
        };
        ReceivingResponse successMessage = new ReceivingResponse(true, LocalDateTime.of(2019, 05, 12, 15, 31, 16), responseList);
        Map<String, String> mockMap = new LinkedHashMap<>();
        mockMap.put("controlNumber", "984003673");
        when(receiveSummaryService.getReceiveSummary(Mockito.anyMap())).thenReturn(successMessage);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/US/receiving/summary")
                .param("controlNumber", "984003673")
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(
                        " {" +
                                "\"success\": true,\n" +
                                "\"timestamp\": \"2019-05-12T15:31:16\",\n" +
                                "\"data\": [{ \n" +
                                "\"purchaseOrderId\": \"984003673\",\n" +
                                "\"receiptNumber\": 10022,\n" +
                                "\"transactionType\": 0,\n" +
                                "\"controlNumber\": \"984003673\",\n" +
                                "\"locationNumber\": 3680,\n" +
                                "\"divisionNumber\": 28,\n" +
                                "\"receiptDate\": \"2019-01-03\",\n" +
                                "\"vendorNumber\": 762214,\n" +
                                "\"carrierCode\": null,\n" +
                                "\"trailerNumber\": \"0\",\n" +
                                "\"totalCostAmount\": 0,\n" +
                                "\"totalRetailAmount\": 0,\n" +
                                "\"lineCount\": null,\n" +
                                "\"departmentNumber\": 96,\n" +
                                "\"controlSequenceNumber\":0, \n" +
                                "\"bottleDepositAmount\" : 10.0 \n" +
                                "}] " +
                                "}"
                ))
                .andReturn();
    }
}



