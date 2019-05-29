

/*package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineResponseConverter;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.request.ReceiveSummaryLineSearch;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
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
    private ReceiveLineServiceImpl receiveLineServiceImpl;

    @Mock
    MongoTemplate mongoTemplate;

    @Mock
    ReceivingLineResponseConverter receivingLineResponseConverter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void getReceiveLineTest() throws Exception {


        ReceivingLineResponse receivingLineResponse = new ReceivingLineResponse(0, 0, 0, 0, 0, 2.9,
                1.9, 0, 0, 0, 0, null, 0, 0,
                0, 0, null, null, null, null, 0, 0, 0,
                0);
        ReceivingLineResponse receivingLineResponseAt = new ReceivingLineResponse(1, 1, 1, 1,
                0, 2.9, 1.9, 0, 0, 0, 0,
                null, 0, 0, 0, 0, null, null, null,
                null, 0, 0, 0, 0);

        List listOfContent = new ArrayList<ReceivingLine>();
        ReceivingLine receivingLine = new ReceivingLine("112|1804823|8264|18|0|1995-10-17|1995-10-17T18:45:21|122", "4665267",
                0, 3777, 94493, 0, 0.0, 0.0, "9",
                89, 12, 1122, 99, 8264, 18,
                LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 22,
                LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 0, LocalDate.now(),
                0, 1.9, "LL");
        ReceivingLine receivingLineAt = new ReceivingLine("0|0|0|0|0|null|null|12", "6778", 0,
                0, 0, 0, 0.0, 0.0, "0", 0,
                0, 0, 0, 0, 0, null, null, 12,
                LocalDateTime.of(1985, 10, 17, 18, 45, 21), 'A', "BKP", "111",
                0, LocalDate.now(), 0, 1.9, "LL");
        listOfContent.add(receivingLine);
        listOfContent.add(receivingLineAt);

        List<ReceivingLineResponse> content = new ArrayList<>();
        content.add(receivingLineResponse);
        content.add(receivingLineResponseAt);

        PageRequest pageRequest = new PageRequest(1, 1, Sort.unsorted());
        PageImpl<ReceivingLineResponse> pageImplResponse = new PageImpl(content, pageRequest, 1);


        when(mongoTemplate.find(Mockito.any(Query.class), Mockito.any(Class.class), Mockito.anyString())).thenReturn(listOfContent);
        when(receivingLineResponseConverter.convert(Mockito.any(ReceivingLine.class))).thenReturn(receivingLineResponse);

        ReceiveLineServiceImpl receiveLineServiceImpl = Mockito.mock(ReceiveLineServiceImpl.class);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/US/receiving/line");
        Mockito.when(receiveLineServiceImpl.getLineSummary("1145", "1124", "11", "HHLL", "3580",
                "99", 1, 1, "creationDate", Sort.DEFAULT_DIRECTION).toString()).thenReturn(pageImplResponse.toString());
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "    \"receiptNumber\": \"80218\",\n" +
                        "    \"receiptLineNumber\": \"0\",\n" +
                        "    \"itemNumber\": \"null\",\n" +
                        "    \"vendorNumber\": \"467175\",\n" +
                        "    \"quantity\": 100,\n" +
                        "    \"eachCostAmount\": \"0\",\n" +
                        "    \"eachRetailAmount\": \"0\",\n" +
                        "    \"packQuantity\": 1,\n" +
                        "    \"numberofCasesReceived\": \"0\",\n" +
                        "    \"vendorStockNumber\": 0,\n" +
                        "    \"bottleDepositAmount\": 0,\n" +
                        "    \"damaged\": \n" +
                        "    \"purchaseOrderNumber\": 852180484,\n" +
                        "    \"parentReceiptNumber\":80218, \n" +
                        "    \"upc\":0, \n" +
                        "    \"unitOfMeasure\": lbs,\n" +
                        "    \"variableWeightInd\": \"\",\n" +
                        "    \"transactionType\": 99,\n" +
                        "    \"controlNumber\": 852180484,\n" +
                        "    \"locationNumber\": 3680,\n" +
                        "    \"divisionNumber\": 0,\n" +
                        "}"))
                .andReturn();
    }

}*/



