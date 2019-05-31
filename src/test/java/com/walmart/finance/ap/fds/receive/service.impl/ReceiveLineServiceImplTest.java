/*package com.walmart.finance.ap.fds.receive.service.impl;

import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineReqConverter;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineResponseConverter;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.repository.ReceiveLineDataRepository;
import com.walmart.finance.ap.fds.receiving.request.ReceiveLineSearch;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest(ReceiveLineServiceImpl.class)
@RunWith(PowerMockRunner.class)
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
    String purchaseOrderReceiveID = "4665267";
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
    Integer purchaseOrderId = 1114;
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
    Integer upc = 117;
    String itemDescription = "9890";
    Integer purchaseReceiptNumber = 199;
    String unitOfMeasure = "lbs";
    LocalDateTime creationDate = null;
    Integer controlNumber = 118;
    Integer locationNumber = 112;
    Integer divisionNumber = 44;
    String receivingControlNumber = "112";
    Integer parentReceiptNumber = 0;
    Integer countryCode = 0;
    Character typeIndicator = 'A';
    String writeIndicator = "B";
    Integer bottleDepositAmount = 0;
    String purchaseOrderNumber = "888";
    LocalDate mdsReceiveDate = null;
    Integer receiveSequenceNumber = 0;
    Double receivedWeightQuantity = 9.0;
    String receivedQuantityUnitOfMeasureCode = "0";
    Sort.Direction order = Sort.DEFAULT_DIRECTION;
    int pageNbr = 1;
    int pageSize = 1;
    String orderBy = "creationDate";
    Query query = new Query();
    Query dynamicQuery = new Query();*/

   /* @Test
    public void getLineSummaryTest() {
        ReceivingLine receivingLine = new ReceivingLine(_id, purchaseOrderReceiveID, lineNumber, itemNumber, vendorNumber, receivedQuantity, costAmount, retailAmount, receivingControlNumber, purchaseReceiptNumber, purchaseOrderId, upcNumber, transactionType, storeNumber, baseDivisionNumber, finalDate, finalTime, sequenceNumber, creationDate, typeIndicator, writeIndicator, purchaseOrderNumber, quantity, mdsReceiveDate, receiveSequenceNumber, receivedWeightQuantity, receivedQuantityUnitOfMeasureCode);
        Optional<ReceivingLine> receiveLineAt = Optional.of(receivingLine);
        ReceivingLine savedReceivingLine = receiveLineAt.get();
        ReceivingLineResponse response = new ReceivingLineResponse(receiptNumber, receiptLineNumber, itemNumber, vendorNumber, quantity, eachCostAmount, eachRetailAmount, packQuantity, numberofCasesReceived,
                vendorStockNumber, bottleStockNumber, damaged, Integer.valueOf(purchaseOrderNumber), purchaseReceiptNumber, purchaseOrderId, upc, itemDescription, unitOfMeasure, variableWeightInd, receivedWeightQuantity.toString(), transactionType, controlNumber, locationNumber, divisionNumber);
        when(receivingLineResponseConverter.convert(savedReceivingLine)).thenReturn(response);
        when(receiveLineDataRepository.findById(_id)).thenReturn(receiveLineAt);
        //  Assert.assertEquals(response, receiveLineServiceImpl.getLineSummary(receivingControlNumber, poReceiveId.toString(), storeNumber.toString()
        //         , baseDivisionNumber.toString(), transactionType.toString(), finalDate.toString(), finalTime.toString(), sequenceNumber.toString(), pageNbr, pageSize, orderBy, Sort.Direction.DESC));
    }

    @Test
    public void getReceiveLineSearchTest() throws Exception {
        ReceiveLineSearch receiveLineSearch = new ReceiveLineSearch(Long.valueOf(purchaseOrderId), receiptNumber.longValue(), transactionType, controlNumber.toString(), locationNumber, divisionNumber);*/


/*        List<String> orderByproperties = new ArrayList<>();
        orderByproperties.add("creationDate");*//*


        List<ReceivingLine> listOfContent = new ArrayList<>();
        ReceivingLine receivingLine = new ReceivingLine("112|1804823|8264|18|0|1995-10-17|1995-10-17T18:45:21|122", "4665267", 0, 3777, 94493, 0, 0.0, 0.0, "9", 89, 12, 1122, 99, 8264, 18, LocalDate.of(1995, 10, 17), LocalDateTime.of(1995, 10, 17, 18, 45, 21), 22, LocalDateTime.of(1990, 10, 17, 18, 45, 21), 'A', "BKP", "111", 0, LocalDate.now(), 0, 1.9, "LL");
        ReceivingLine receivingLineAt = new ReceivingLine("0|0|0|0|0|null|null|12", "6778", 0, 0, 0, 0, 0.0, 0.0, "0", 0, 0, 0, 0, 0, 0, null, null, 12, LocalDateTime.of(1985, 10, 17, 18, 45, 21), 'A', "BKP", "111", 0, LocalDate.now(), 0, 1.9, "LL");
        listOfContent.add(receivingLine);
        listOfContent.add(receivingLineAt);

        Query query = new Query();
        ReceiveLineServiceImpl spy = PowerMockito.spy(new ReceiveLineServiceImpl());

        Criteria criteria = Criteria.where("receivingControlNumber").is(466567).and("purchaseOrderReceiveID").is(1).and("transactionType").is(0)
                .and("baseDivisionNumber").is(44).and("storeNumber").is(112);
        query.addCriteria(criteria);
        Pageable pageable = PageRequest.of(pageNbr, pageSize);
        query.with(pageable);

//        Query dynamicQueryAt=mock(Query.class);
//        whenNew(Query.class).withNoArguments().thenReturn(dynamicQueryAt);

        //ReceiveLineSearch receiveLineSearch=mock(ReceiveLineSearch.class);
        //  whenNew(ReceiveLineSearch.class).withAnyArguments().thenReturn(receiveLineSearch);

        // PowerMockito.doReturn(query).when(spy,"searchCriteria",receiveLineSearch, dynamicQueryAt);
        when(receivingLineResponseConverter.convert(receivingLine)).thenReturn(receivingLineResponse);

        ReceivingLineResponse receivingLineResponse = new ReceivingLineResponse(0, 0, 0, 0, 0, 2.9, 1.9, 0, 0, 0, 0, null, 0, 0, 0, 0, null, null, null, null, 0, 0, 0, 0);
        ReceivingLineResponse receivingLineResponseAt = new ReceivingLineResponse(1, 1, 1, 1, 0, 2.9, 1.9, 0, 0, 0, 0, null, 0, 0, 0, 0, null, null, null, null, 0, 0, 0, 0);
        List<ReceivingLineResponse> content = new ArrayList<>();
        content.add(receivingLineResponse);
        content.add(receivingLineResponseAt);
//        MongoTemplate template = spy(this.mongoTemplate);


        when(mongoTemplate.find(refEq(query), eq(ReceivingLine.class), eq("receive-line-new"))).thenReturn(listOfContent);

        PageRequest pageRequest = new PageRequest(1, 1, Sort.unsorted());
        PageImpl<ReceivingLineResponse> pageImplResponse = new PageImpl(content, pageRequest, 1);
        // PageImpl<ReceivingLine> pageImplRequest = new PageImpl<>(listOfContent,pageRequest,1);
        // when(PageableExecutionUtils.getPage(listOfContent,pageable,()->mongoTemplate.count(query,ReceivingLine.class))).thenReturn(pageImplResponse);
        when(mongoTemplate.count(query, ReceivingLine.class)).thenReturn(2L);


        // PowerMockito.doReturn(pageImplResponse).when(spy,"mapReceivingLineToResponse",pageImplRequest);


        Page<ReceivingLineResponse> receivingLineResponses = receiveLineServiceImpl.getReceiveLineSearch(receiveLineSearch, 1, 1, "creationDate");

        System.out.println("-------------->" + receivingLineResponses);


        Assert.assertEquals(receiveLineServiceImpl.getReceiveLineSearch(receiveLineSearch, 1, 1, "creationDate").toString(), pageImplResponse.toString());

    }

}

*/
