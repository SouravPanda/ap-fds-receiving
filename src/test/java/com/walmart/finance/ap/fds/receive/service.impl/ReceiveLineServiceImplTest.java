
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

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


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

        String _id = "112|1804823|8264|18|0|1995-10-17|1995-10-17T18:45:21|122";
        Integer purchaseOrderReceiveID = 4665267;
        Long purchaseOrderId = 466567L;
        Integer storeNumber = 8264;
        Integer itemNumber = 3777;
        LocalDate finalDate = LocalDate.of(1995, 10, 17);
        LocalDateTime finalTime=LocalDateTime.of(1995, 10, 17,18,45,21);
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
        Integer receivingControlNumber = 112;
        Integer countryCode=0;
        @Test
        public void getLineSummaryTest() {
             ReceivingLine receivingLine =new ReceivingLine(_id, purchaseOrderReceiveID, lineNumber, itemNumber, vendorNumber,receivedQuantity, costAmount, retailAmount, receivingControlNumber, purchaseReceiptNumber, purchasedOrderId, upcNumber, transactionType, storeNumber, baseDivisionNumber, finalDate, finalTime, sequenceNumber, creationDate);
              Optional<ReceivingLine> receiveLineAt =Optional.of(receivingLine);
              ReceivingLine savedReceivingLine = receiveLineAt.get();
             ReceivingLineResponse response= new ReceivingLineResponse(receiptNumber,receiptLineNumber,itemNumber,vendorNumber,quantity,eachCostAmount,eachRetailAmount,packQuantity,numberofCasesReceived,
                   vendorStockNumber,bottleStockNumber,damaged,purchaseOrderNumber,purchaseReceiptNumber,purchasedOrderId,upc,itemDescription,unitOfMeasure,variableWeightInd,receivedWeightQuantity,transactionType,controlNumber,locationNumber,divisionNumber);
              when(receivingLineResponseConverter.convert(savedReceivingLine)).thenReturn(response);
            when(receiveLineDataRepository.findById(_id)).thenReturn(receiveLineAt);
            Assert.assertEquals(response,receiveLineServiceImpl.getLineSummary(receivingControlNumber.toString(),poReceiveId.toString(),storeNumber.toString()
            ,baseDivisionNumber.toString(),transactionType.toString(),finalDate.toString(),finalTime.toString(),sequenceNumber.toString()));

        }

        @Test
        public void searchCriteriaTest(){
          Query dynamicQuery =new Query();
          ReceiveLineSearch receiveLineSearch = new ReceiveLineSearch(purchaseOrderId,receiptNumber.longValue(),transactionType,controlNumber.toString(), locationNumber, divisionNumber,countryCode);
            Criteria criteria=Criteria.where("receivingControlNumber").is(466567L).and("purchaseOrderReceiveID").is(1L).and("baseDivisionNumber").is(44).and("storeNumber").is(112);
            dynamicQuery.addCriteria(criteria);
            Assert.assertEquals(receiveLineServiceImpl.searchCriteria(receiveLineSearch,dynamicQuery).toString(),dynamicQuery.toString());
        }
}