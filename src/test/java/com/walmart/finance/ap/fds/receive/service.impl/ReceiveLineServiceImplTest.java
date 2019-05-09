
package com.walmart.finance.ap.fds.receive.service.impl;

import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineReqConverter;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineResponseConverter;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.repository.ReceiveLineDataRepository;
import com.walmart.finance.ap.fds.receiving.request.ReceiveLineSearch;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.mockito.Mockito.when;

public class ReceiveLineServiceImplTest {
    @InjectMocks
    ReceiveLineServiceImpl receiveLineServiceImpl;

    @Mock
    ReceiveLineDataRepository receiveLineDataRepository;

    @Mock
    ReceivingLineResponseConverter receivingLineResponseConverter;

    @Mock
    ReceivingLineReqConverter receivingLineReqConverter;

    @Mock
    MongoTemplate mongoTemplate;

    @Mock
    ReceivingLineResponse receivingLineResponse;



    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    String _id = "112|1804823|8264|18|0|1995-10-17|1995-10-17T18:45:21|122";
    Integer purchaseOrderReceiveID = 4665267;
    Integer purchaseOrderId = 466567;
    Integer storeNumber = 8264;
    Integer itemNumber = 3777;
    LocalDate finalDate = LocalDate.of(1995, 10, 17);
    LocalDateTime finalTime = LocalDateTime.of(1995, 10, 17, 18, 45, 21);
    Integer lineNumber = 0;
    Integer vendorNumber = 94493;
    Integer receiptLineNumber = 18;
    Integer receiptNumber = 1;
    Integer receivedQuantity = null;
    Integer quantity = 12;
    Integer purchasedOrderId = 1114;
    Double eachCostAmount = 2223.9;
    Double eachRetailAmount = 11222.8;
    Integer packQuantity = 34;
    String variableWeightInd = null;
    Double costAmount = 1118.9;
    Double retailAmount = 1445.9;
    Integer sequenceNumber = 122;
    Integer transactionType = 0;
    Integer upcNumber = 1114;
    Integer poReceiveId = 1804823;
    Integer baseDivisionNumber = 18;
    Integer numberofCasesReceived = 466;
    Integer vendorStockNumber = 1804823;
    Integer bottleStockNumber = 11333;
    String damaged = null;
    Integer purchaseOrderNumber = 7776;
    Integer upc = 117;
    String itemDescription = "PR";
    Integer purchaseReceiptNumber = 199;
    String unitOfMeasure = "lbs";
    LocalDateTime creationDate = null;
    String receivedWeightQuantity = "NETA";
    Integer controlNumber = 118;
    Integer locationNumber = 112;
    Integer divisionNumber = 44;
    String receivingControlNumber = "112";
    Integer countryCode = 0;
    Character typeIndicator='A';
    Character writeIndicator='B';
    Query query=new Query();
    Query dynamicQuery= new Query();

    @Test
    public void getLineSummaryTest() {
        ReceivingLine receivingLine = new ReceivingLine(_id, purchaseOrderReceiveID, lineNumber, itemNumber, vendorNumber, receivedQuantity, costAmount, retailAmount, receivingControlNumber, purchaseReceiptNumber, purchasedOrderId, upcNumber, transactionType, storeNumber, baseDivisionNumber, finalDate, finalTime, sequenceNumber, creationDate,typeIndicator,writeIndicator,quantity);
        Optional<ReceivingLine> receiveLineAt = Optional.of(receivingLine);
        ReceivingLine savedReceivingLine = receiveLineAt.get();
        ReceivingLineResponse response = new ReceivingLineResponse(receiptNumber, receiptLineNumber, itemNumber, vendorNumber, quantity, eachCostAmount, eachRetailAmount, packQuantity, numberofCasesReceived,
                vendorStockNumber, bottleStockNumber, damaged, purchaseOrderNumber, purchaseReceiptNumber, purchasedOrderId, upc, itemDescription, unitOfMeasure, variableWeightInd, receivedWeightQuantity, transactionType, controlNumber, locationNumber, divisionNumber);
        when(receivingLineResponseConverter.convert(savedReceivingLine)).thenReturn(response);
        when(receiveLineDataRepository.findById(_id)).thenReturn(receiveLineAt);
        Assert.assertEquals(response, receiveLineServiceImpl.getLineSummary(receivingControlNumber.toString(), poReceiveId.toString(), storeNumber.toString()
                , baseDivisionNumber.toString(), transactionType.toString(), finalDate.toString(), finalTime.toString(), sequenceNumber.toString()));

    }

    @Test
    public void getReceiveLineSearchTest() {
        ReceiveLineSearch receiveLineSearch = new ReceiveLineSearch(Long.valueOf(purchaseOrderId), receiptNumber.longValue(), transactionType, controlNumber.toString(), locationNumber, divisionNumber, countryCode);
        query = searchCriteria(receiveLineSearch);
        Pageable pageable = PageRequest.of(1, 1);
        query.with(pageable);
        List<String> orderByproperties = new ArrayList<>();
        orderByproperties.add("creationDate");
        List<ReceivingLine> listOfContent=new ArrayList<>();
        ReceivingLine receivingLine = new ReceivingLine("112|1804823|8264|18|0|1995-10-17|1995-10-17T18:45:21|122", 4665267, 0, 3777, 94493, 0, 0.0, 0.0, "9", 89, 12, 1122, 99, 8264, 18, LocalDate.of(1995, 10, 17),LocalDateTime.of(1995, 10, 17, 18, 45, 21), 22, LocalDateTime.of(1990, 10, 17, 18, 45, 21),'A','B',0);
        ReceivingLine receivingLineAt=new ReceivingLine("0|0|0|0|0|null|null|12", 0, 0, 0, 0, 0, 0.0, 0.0, "0", 0, 0, 0, 0, 0, 0, null, null, 12, LocalDateTime.of(1985, 10, 17, 18, 45, 21),'A','B',0);
        listOfContent.add(receivingLine);
        listOfContent.add(receivingLineAt);
        Sort sort = new Sort(Sort.Direction.DESC, orderByproperties);
        query.with(sort);

     when(mongoTemplate.find(query, ReceivingLine.class, "receive-line")).thenReturn(listOfContent);
        Page<ReceivingLine> receiveLinePage = PageableExecutionUtils.getPage(
                listOfContent,
                pageable,
                () -> mongoTemplate.count(query, ReceivingLine.class));
        System.out.println("hh "+receiveLineServiceImpl.getReceiveLineSearch(receiveLineSearch,1,1,"creationDate",Sort.Direction.DESC).toString());
       // System.out.println("yy "+mapReceivingSummaryToResponse(receiveLinePage).toString());
        when(receivingLineResponseConverter.convert(receivingLine)).thenReturn(receivingLineResponse);
       // when(receiveLineServiceImpl.mapReceivingSummaryToResponse(Page<ReceivingLine>receiveLinePage))
       // Assert.assertEquals(receiveLineServiceImpl.getReceiveLineSearch(receiveLineSearch,1,1,"creationDate",Sort.Direction.DESC).toString(),mapReceivingSummaryToResponse(receiveLinePage).toString());

    }

    private Query searchCriteria(ReceiveLineSearch receiveLineSearch) {
        Criteria criteria = Criteria.where("receivingControlNumber").is(466567L).and("purchaseOrderReceiveID").is(1L).and("baseDivisionNumber").is(44).and("storeNumber").is(112);
        dynamicQuery.addCriteria(criteria);
        return dynamicQuery;
    }
   /* private Page<ReceivingLineResponse> mapReceivingSummaryToResponse(Page<ReceivingLine> receiveLinePage) {
        Page<ReceivingLineResponse> receivingLineResponsePage = receiveLinePage.map(new Function<ReceivingLine, ReceivingLineResponse>() {
            @Override
            public ReceivingLineResponse apply(ReceivingLine receiveLine) {
                ReceivingLineResponse receivingLineResponse = new ReceivingLineResponse(_id, purchaseOrderReceiveID, lineNumber, itemNumber, vendorNumber, receivedQuantity, costAmount, retailAmount, receivingControlNumber, purchaseReceiptNumber, purchasedOrderId, upcNumber, transactionType, storeNumber, baseDivisionNumber, finalDate, finalTime, sequenceNumber, creationDate);
                return when(receivingLineResponseConverter.convert(receiveLine)).thenReturn(receivingLineResponse);
            }
        });
        return receivingLineResponsePage;
    }*/

}
