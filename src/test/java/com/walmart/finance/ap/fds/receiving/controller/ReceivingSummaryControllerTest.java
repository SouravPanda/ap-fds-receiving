
package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.request.Meta;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.request.SorRoutingCtx;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.response.SuccessMessage;
import com.walmart.finance.ap.fds.receiving.service.ReceiveSummaryService;
import com.walmart.finance.ap.fds.receiving.service.ReceiveSummaryServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ReceivingSummaryController.class)
public class ReceivingSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReceiveSummaryService receiveSummaryService;

    @Mock
    private ReceiveSummaryServiceImpl receiveSummaryServiceImpl;

    @Mock
    MongoTemplate mongoTemplate;

    @Mock
    ReceivingSummaryResponseConverter receivingSummaryResponseConverter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void getReceiveSummaryTest() throws Exception {


        ReceiveSummary receiveSummary = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "4665267",
                8264, 18, 0, LocalDate.of(1996, 12, 12), LocalTime.of(18, 45, 21), 0, 7688, 1111,
                0, 0, 'H', 0.0, 1.0, 1, 'P', 2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, 0, 0, LocalDateTime.now(), 0, "JJJ", "yyyy", LocalDateTime.now(), "99"
                , 'K', "LLL");
        ReceiveSummary receiveSummaryAt = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21", "4665207",
                8064, 18, 0, LocalDate.of(1986, 12, 12), LocalTime.of(18, 45, 21), 0, 9788, 1111,
                0, 0, 'H', 0.0, 1.0, 1, 'P', 2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56, 22), LocalDate.now(),
                LocalDate.now(), 9.0, 7, 0, 0, LocalDateTime.now(), 0, "JJJ", "UU", LocalDateTime.now(), "99"
                , 'K', "IIL");

        List listOfContent = new ArrayList<ReceiveSummary>();
        listOfContent.add(receiveSummary);
        listOfContent.add(receiveSummaryAt);

        ReceivingSummaryResponse receivingSummaryResponse = new ReceivingSummaryResponse("7778", 1122, 99, "776", 3680, 0,
                LocalDate.of(1986, 12, 12), 'L', 78, "hjhj", "77", 9.0, 7.0,
                0L, 0);

        ReceivingSummaryResponse receivingSummaryResponseAt = new ReceivingSummaryResponse("999778", 10022, 99, "776", 3680, 0,
                LocalDate.of(1986, 12, 12), 'L', 78, "hjhj", "77", 9.0, 7.0,
                0L, 0);

        List<ReceivingSummaryResponse> content = new ArrayList<>();
        content.add(receivingSummaryResponse);
        content.add(receivingSummaryResponseAt);

        List<String> listOfReceiptNumbers = new ArrayList<>();
        listOfReceiptNumbers.add("99");
        listOfReceiptNumbers.add("89");

        List<String> listOfItemNumbers = new ArrayList<>();
        listOfItemNumbers.add("99");
        listOfItemNumbers.add("89");

        List<String> listOfUpcNumbers = new ArrayList<>();
        listOfItemNumbers.add("9");
        listOfItemNumbers.add("89");

        when(mongoTemplate.find(Mockito.any(Query.class), Mockito.any(Class.class), Mockito.anyString())).thenReturn(listOfContent);
        when(receivingSummaryResponseConverter.convert(Mockito.any(ReceiveSummary.class))).thenReturn(receivingSummaryResponse);

        SuccessMessage successMessage = new SuccessMessage();
        successMessage.setTimestamp(LocalDateTime.now());
        successMessage.setMessage(true);
        successMessage.setData(content);

        Mockito.when(receiveSummaryServiceImpl.getReceiveSummary("US", "77", "8", listOfReceiptNumbers, "66",
                "99", "675", "987", "18", "WW8", "776"
                , "1980", "1988-12-12", "1990-12-12", listOfItemNumbers, listOfUpcNumbers)).thenReturn(successMessage);

        Assert.assertEquals(receiveSummaryServiceImpl.getReceiveSummary("US", "77", "8", listOfReceiptNumbers, "66",
                "99", "675", "987", "18", "WW8", "776"
                , "1980", "1988-12-12", "1990-12-12", listOfItemNumbers, listOfUpcNumbers).getData(), successMessage.getData());

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
        SuccessMessage successMessage = new SuccessMessage();
        successMessage.setMessage(true);
        responseList.add(receivingSummaryRequest);
        successMessage.setData(responseList);
        successMessage.setTimestamp(LocalDateTime.now());
        Mockito.when(receiveSummaryServiceImpl.updateReceiveSummary(receivingSummaryRequest, "US")).thenReturn(successMessage);
        String body = new String("{\n" +
                "            \"receiptNumber\": \"2\",\n" +
                "            \"controlNumber\": \"2\",\n" +
                "            \"receiptDate\": \"2019-04-19\",\n" +
                "            \"locationNumber\": 2,\n" +
                "            \"businessStatusCode\": \"A\",\n" +
                "            \"meta\": {\n" +
                "                \"unitofWorkid\": null,\n" +
                "                \"sorRoutingCtx\": {\n" +
                "                    \"replnTypCd\": null,\n" +
                "                    \"invProcAreaCode\": 36,\n" +
                "                    \"locationCountryCd\": \"US\"\n" +
                "                }\n" +
                "             }\n" +
                "}");
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/US/receiving/summary")
                .header("X-FDS-FOUNDATION-API-KEY", "razorbacks")
                .header("Content-Type", "application/json")
                .header("X-IBM-Client-Secret", "rK7xP5iW4uV0gI4mA0dM8yQ3dV8tW2hG6nB8uA5mY1kA4sQ8oB")
                .header("X-IBM-Client-Id", "e684e483-dbba-41d5-a040-6043b48798f5")
                .accept(MediaType.APPLICATION_JSON)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            mockMvc.perform(requestBuilder)
                    .andExpect(content().json(body))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\n" +
                            "    \"message\": true,\n" +
                            "    \"timestamp\": \"2019-06-19T14:29:17.812\",\n" +
                            "    \"data\": [\n" +
                            "        {\n" +
                            "            \"receiptNumber\": \"2\",\n" +
                            "            \"controlNumber\": \"2\",\n" +
                            "            \"receiptDate\": \"2019-04-19\",\n" +
                            "            \"locationNumber\": 2,\n" +
                            "            \"businessStatusCode\": \"A\",\n" +
                            "            \"meta\": {\n" +
                            "                \"unitofWorkid\": null,\n" +
                            "                \"sorRoutingCtx\": {\n" +
                            "                    \"replnTypCd\": null,\n" +
                            "                    \"invProcAreaCode\": 36,\n" +
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
        ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest("2", "2", LocalDate.now(), 2, "A", 1, "0", null);
        SuccessMessage successMessage = new SuccessMessage();
        List<ReceivingSummaryLineRequest> responseList = new ArrayList<>();
        successMessage.setMessage(true);
        responseList.add(receivingSummaryLineRequest);
        successMessage.setData(responseList);
        successMessage.setTimestamp(LocalDateTime.now());
        Mockito.when(receiveSummaryServiceImpl.updateReceiveSummaryAndLine(receivingSummaryLineRequest, "US")).thenReturn(successMessage);
        String body = new String("{\n" +
                "            \"receiptNumber\": \"2\",\n" +
                "            \"controlNumber\": \"2\",\n" +
                "            \"receiptDate\": \"2019-04-19\",\n" +
                "            \"locationNumber\": 2,\n" +
                "            \"businessStatusCode\": \"A\",\n" +
                "            \"inventoryMatchStatus\": \"9\",\n" +
                "            \"meta\": {\n" +
                "                \"unitofWorkid\": null,\n" +
                "                \"sorRoutingCtx\": {\n" +
                "                    \"replnTypCd\": null,\n" +
                "                    \"invProcAreaCode\": 36,\n" +
                "                    \"locationCountryCd\": \"US\"\n" +
                "                }\n" +
                "             }\n" +
                "}");
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/US/receiving/summary")
                .header("X-FDS-FOUNDATION-API-KEY", "razorbacks")
                .header("Content-Type", "application/json")
                .header("X-IBM-Client-Secret", "rK7xP5iW4uV0gI4mA0dM8yQ3dV8tW2hG6nB8uA5mY1kA4sQ8oB")
                .header("X-IBM-Client-Id", "e684e483-dbba-41d5-a040-6043b48798f5")
                .accept(MediaType.APPLICATION_JSON)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            mockMvc.perform(requestBuilder)
                    .andExpect(content().json(body))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\n" +
                            "    \"message\": true,\n" +
                            "    \"timestamp\": \"2019-06-19T14:29:17.812\",\n" +
                            "    \"data\": [\n" +
                            "        {\n" +
                            "            \"receiptNumber\": \"2\",\n" +
                            "            \"controlNumber\": \"2\",\n" +
                            "            \"receiptDate\": \"2019-04-19\",\n" +
                            "            \"locationNumber\": 2,\n" +
                            "            \"businessStatusCode\": \"A\",\n" +
                            "            \"inventoryMatchStatus\": \"9\",\n" +
                            "            \"sequenceNumber\": \"null\",\n" +
                            "            \"meta\": {\n" +
                            "                \"unitofWorkid\": null,\n" +
                            "                \"sorRoutingCtx\": {\n" +
                            "                    \"replnTypCd\": null,\n" +
                            "                    \"invProcAreaCode\": 36,\n" +
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
}



