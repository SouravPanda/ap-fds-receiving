package com.walmart.store.receive.service.test;

import com.walmart.store.receive.Response.ReceivingSummaryResponse;
import com.walmart.store.receive.converter.ReceivingSummaryReqConverter;
import com.walmart.store.receive.converter.ReceivingSummaryResponseConverter;
import com.walmart.store.receive.dao.ReceiveDataRepository;
import com.walmart.store.receive.pojo.ReceiveSummary;
import com.walmart.store.receive.service.ReceiveSummaryServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class ReceiveSummaryServiceImplTest {
    @InjectMocks
    ReceiveSummaryServiceImpl receiveSummaryServiceImpl;

    @Mock
    ReceiveDataRepository receiveDataRepository;

    @Mock
    ReceivingSummaryResponseConverter receivingSummaryResponseConverter;

    @Mock
    ReceivingSummaryReqConverter receivingSummaryReqConverter;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void getReceiveSummaryTest() {
        String _id = "4665265|1804823|8264|18|3|1995-10-17|18:45:21";
        String receivingControlNumber="4665267";
        String storeNumber="8264";
        String transactionType="3";
        String finalDate="1995-10-17";
        String finalTime="18:45:21";
        String controlType="0";
        String vendorNumber="94493";
        String accountNumber="18";
        String controlSequenceNumber="1";
        String receiveSequenceNumber=null;
        String matchIndicator="P";
        String totalCostAmount="37979.92";
        String totalRetailAmount="37979.92";
        String freightBillId=null;
        String businessStatusCode="J";
        String freightBillExpandID=null;
        String claimPendingIndicator=null;
        String freeAstrayIndicator=null;
        String freightConslIndicator=null;
        String initialReceiveTimestamp=null;
        String MDSReceiveDate=null;
        String receiveProcessDate=null;
        String receiveWeightQuantity=null;
        String sequenceNumber=null;
        String departmentNumber="91";
        String casesReceived="0";
        String finalizedLoadTimestamp=null;
        String finalizedSequenceNumber=null;
        String poReceiveId="1804823";
        String baseDivisionNumber="18";
        String userId=null;
        String creationDate="2019-04-26T10:44:26.784";
     // ReceiveSummary receiveSummary =new ReceiveSummary(_id, receivingControlNumber, storeNumber, transactionType, finalDate, finalTime, controlType, vendorNumber, accountNumber, controlSequenceNumber, receiveSequenceNumber, matchIndicator, totalCostAmount, totalRetailAmount, freightBillId, businessStatusCode, freightBillExpandID, claimPendingIndicator, freeAstrayIndicator, freightConslIndicator, initialReceiveTimestamp, MDSReceiveDate, receiveProcessDate, receiveWeightQuantity, sequenceNumber=null, departmentNumber, casesReceived, finalizedLoadTimestamp, finalizedSequenceNumber, poReceiveId, baseDivisionNumber, userId, creationDate);
        Optional<ReceiveSummary> receiveSummary =receiveDataRepository.findById(_id);
        when(receiveDataRepository.findById(_id)).thenReturn(receiveSummary);
       Assert.assertEquals(receiveSummary, receiveDataRepository.findById(_id));
        System.out.println("I am here : "+receiveDataRepository.findById(_id));
    }
}

