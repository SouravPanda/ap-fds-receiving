package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.response.*;
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
        ReceivingInfoLineResponse receivingInfoLineResponse = new ReceivingInfoLineResponse(new Long(30006), 1, 3777,
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
                new Long(999403403), LocalDate.of(2019, 03, 14), new Long(30006),
                " ", 0.0, 0.0, null, 495742, "Memo",
                0.0, 0, "USER", "1223",
                "3669", null, "999403403", 411276735,
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
                                "\"parentReceivingStoreNbr\": \"3669\",\n" +
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

    @Test
    public void getReceivingInfoV1() throws Exception {
        List<ReceiveMDSResponse> merchandises = new ArrayList<ReceiveMDSResponse>() {
            {
                add(new ReceiveMDSResponse(1, 350, "01"));
                add(new ReceiveMDSResponse(2, 400, "02"));
            }
        };
        ReceivingInfoLineResponse receivingInfoLineResponse = new ReceivingInfoLineResponse(new Long(110950), 2, 575486609,
                2, 30.0,
                30.09, 2, 0,
                "N", "0000047875883989",
                "NSW CRASH TRNF", null, "ww", 1, "0.0", merchandises);
        List<ReceivingInfoLineResponse> lineResponses = new ArrayList<ReceivingInfoLineResponse>() {
            {
                add(receivingInfoLineResponse);
            }
        };
        List<InvoiceFinTransProcessLogs> invoiceFinTransProcessLogs = new ArrayList<InvoiceFinTransProcessLogs>() {
            {
                add(new InvoiceFinTransProcessLogs(null, null, 10, LocalDate.of(2019, 05, 27), "ID123"));
            }
        };
        List<InvoiceFinTransAdjustLogs> invoiceFinTransAdjustLogs = new ArrayList<InvoiceFinTransAdjustLogs>() {
            {
                add(new InvoiceFinTransAdjustLogs(10, 10.05, LocalDate.of(2019, 05, 27), "Change123",
                        LocalDate.of(2018, 12, 23), 20.02, LocalDate.of(2018, 11, 23), LocalDate.of(2018, 11, 24)));
            }
        };
        List<InvoiceFinDelNoteChangeLogs> invoiceFinDelNoteChangeLogs = new ArrayList<InvoiceFinDelNoteChangeLogs>() {
            {
                add(new InvoiceFinDelNoteChangeLogs(LocalDate.of(2019, 05, 27), "User1234", "Del1234", "OrgDel123"));
            }
        };
        ReceivingInfoResponseV1 response = new ReceivingInfoResponseV1("ID123", LocalDate.of(2019, 05, 27), null,
                "0.0", 640, 99, 18, new Long(1), 6479,
                new Long(972515962), LocalDate.of(2019, 01, 01), new Long(110950),
                "A", 0.0, 0.0, null, 397646, null,
                0.0, 0, "PEPSI MIDAMERICA", "1223",
                null, null, null, 97166785,
                "1832721624", new Long(724201901), 0, null, "6854748957", "US",
                1, 0.0, -5743.12, 0.0, 0.0, 640
                , LocalDate.of(2018, 11, 23), LocalDate.of(2018, 12, 23), "6854748957", LocalDate.of(2018, 11, 24), 0, 538,
                0, 0, "0", "del123", 6479,
                7, 6479, 20, 64, 640, "N",
                null, 10,
                invoiceFinTransProcessLogs, invoiceFinTransAdjustLogs, invoiceFinDelNoteChangeLogs, lineResponses);
        List<ReceivingInfoResponseV1> list = new ArrayList<ReceivingInfoResponseV1>() {
            {
                add(response);
            }
        };
        ReceivingResponse successMessage = new ReceivingResponse(true, LocalDateTime.of(2019, 05, 12, 15, 31, 16), list);
        when(receivingInfoService.getInfoSeviceDataV1(Mockito.anyMap())).thenReturn(successMessage);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/US/receiving/info/v1")
                .param("invoiceId", "97166785")
                .param("lineNumberFlag", "Y")
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\n" +
                                "    \"success\": true,\n" +
                                "    \"timestamp\": \"2019-05-12T15:31:16\",\n" +
                                "    \"data\": [\n" +
                                "        {\n" +
                                "            \"authorizedBy\": \"ID123\",\n" +
                                "            \"authorizedDate\": \"2019-05-27\",\n" +
                                "            \"carrierCode\": null,\n" +
                                "            \"controlNumber\": \"0.0\",\n" +
                                "            \"departmentNumber\": 640,\n" +
                                "            \"transactionType\": 99,\n" +
                                "            \"divisionNumber\": 18,\n" +
                                "            \"lineCount\": 1,\n" +
                                "            \"locationNumber\": 6479,\n" +
                                "            \"purchaseOrderId\": 972515962,\n" +
                                "            \"receiptDate\": \"2019-01-01\",\n" +
                                "            \"receiptNumber\": 110950,\n" +
                                "            \"receiptStatus\": \"A\",\n" +
                                "            \"totalCostAmount\": 0.0,\n" +
                                "            \"totalRetailAmount\": 0.0,\n" +
                                "            \"trailerNumber\": null,\n" +
                                "            \"vendorNumber\": 397646,\n" +
                                "            \"memo\": null,\n" +
                                "            \"bottleDepositAmount\": 0.0,\n" +
                                "            \"controlSequenceNumber\": 0,\n" +
                                "            \"vendorName\": \"PEPSI MIDAMERICA\",\n" +
                                "            \"parentReceivingNbr\": \"1223\",\n" +
                                "            \"parentReceivingStoreNbr\": null,\n" +
                                "            \"parentReceivingDate\": null,\n" +
                                "            \"parentPurchaseOrderId\": null,\n" +
                                "            \"invoiceId\": 97166785,\n" +
                                "            \"invoiceNumber\": \"1832721624\",\n" +
                                "            \"transactionId\": 724201901,\n" +
                                "            \"txnSeqNbr\": 0,\n" +
                                "            \"f6ASeqNbr\": null,\n" +
                                "            \"transactionNbr\": \"6854748957\",\n" +
                                "            \"countryCode\": \"US\",\n" +
                                "            \"apCompanyId\": 1,\n" +
                                "            \"txnRetailAmt\": 0.0,\n" +
                                "            \"txnCostAmt\": -5743.12,\n" +
                                "            \"txnDiscountAmt\": 0.0,\n" +
                                "            \"txnAllowanceAmt\": 0.0,\n" +
                                "            \"vendorDeptNbr\": 640,\n" +
                                "            \"postDate\": \"2018-11-23\",\n" +
                                "            \"dueDate\": \"2018-12-23\",\n" +
                                "            \"poNbr\": \"6854748957\",\n" +
                                "            \"transactionDate\": \"2018-11-24\",\n" +
                                "            \"claimNbr\": 0,\n" +
                                "            \"accountNbr\": 538,\n" +
                                "            \"deductTypeCode\": 0,\n" +
                                "            \"txnBatchNbr\": 0,\n" +
                                "            \"txnControlNbr\": \"0\",\n" +
                                "            \"deliveryNoteId\": \"del123\",\n" +
                                "            \"origStoreNbr\": 6479,\n" +
                                "            \"origDivNbr\": 7,\n" +
                                "            \"poDcNbr\": 6479,\n" +
                                "            \"poTypeCode\": 20,\n" +
                                "            \"poDeptNbr\": 64,\n" +
                                "            \"offsetAccountNbr\": 640,\n" +
                                "            \"grocinvoiceInd\": \"N\",\n" +
                                "            \"matchDate\": null,\n" +
                                "            \"processStatusCode\": 10,\n" +
                                "            \"invoiceFinTransProcessLogs\": [\n" +
                                "                {\n" +
                                "                    \"actionIndicator\": null,\n" +
                                "                    \"memoComments\": null,\n" +
                                "                    \"processStatusCode\": 10,\n" +
                                "                    \"processStatusTimestamp\": \"2019-05-27\",\n" +
                                "                    \"statusUserId\": \"ID123\"\n" +
                                "                }\n" +
                                "            ],\n" +
                                "            \"invoiceFinTransAdjustLogs\": [\n" +
                                "                {\n" +
                                "                    \"adjustmentNbr\": 10,\n" +
                                "                    \"costAdjustAmt\": 10.05,\n" +
                                "                    \"createTs\": \"2019-05-27\",\n" +
                                "                    \"createUserId\": \"Change123\",\n" +
                                "                    \"dueDate\": \"2018-12-23\",\n" +
                                "                    \"origTxnCostAmt\": 20.02,\n" +
                                "                    \"postDate\": \"2018-11-23\",\n" +
                                "                    \"transactionDate\": \"2018-11-24\"\n" +
                                "                }\n" +
                                "            ],\n" +
                                "            \"invoiceFinDelNoteChangeLogs\": [\n" +
                                "                {\n" +
                                "                    \"changeTimestamp\": \"2019-05-27\",\n" +
                                "                    \"changeUserId\": \"User1234\",\n" +
                                "                    \"deliveryNoteId\": \"Del1234\",\n" +
                                "                    \"orgDelNoteId\": \"OrgDel123\"\n" +
                                "                }\n" +
                                "            ],\n" +
                                "            \"receivingLine\": [\n" +
                                "                {\n" +
                                "                    \"receiptNumber\": 110950,\n" +
                                "                    \"receiptLineNumber\": 2,\n" +
                                "                    \"itemNumber\": 575486609,\n" +
                                "                    \"quantity\": 2,\n" +
                                "                    \"eachCostAmount\": 30.0,\n" +
                                "                    \"eachRetailAmount\": 30.09,\n" +
                                "                    \"numberofCasesReceived\": 2,\n" +
                                "                    \"packQuantity\": 0,\n" +
                                "                    \"bottleDepositFlag\": \"N\",\n" +
                                "                    \"upc\": \"0000047875883989\",\n" +
                                "                    \"itemDescription\": \"NSW CRASH TRNF\",\n" +
                                "                    \"unitOfMeasure\": null,\n" +
                                "                    \"variableWeightInd\": \"ww\",\n" +
                                "                    \"costMultiple\": 1,\n" +
                                "                    \"receivedWeightQuantity\": \"0.0\",\n" +
                                "                    \"merchandises\": [\n" +
                                "                        {\n" +
                                "                            \"mdseConditionCode\": 1,\n" +
                                "                            \"mdseQuantity\": 350,\n" +
                                "                            \"mdseQuantityUnitOfMeasureCode\": \"01\"\n" +
                                "                        },\n" +
                                "                        {\n" +
                                "                            \"mdseConditionCode\": 2,\n" +
                                "                            \"mdseQuantity\": 400,\n" +
                                "                            \"mdseQuantityUnitOfMeasureCode\": \"02\"\n" +
                                "                        }\n" +
                                "                    ]\n" +
                                "                }\n" +
                                "            ]\n" +
                                "        }\n" +
                                "    ]\n" +
                                "}"
                ))
                .andReturn();
    }
}

