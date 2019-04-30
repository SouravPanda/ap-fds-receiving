 package com.walmart.finance.ap.fds.receive.service.impl;

    import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
    import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
    import com.walmart.finance.ap.fds.receiving.repository.ReceiveDataRepository;
    import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
    import com.walmart.finance.ap.fds.receiving.service.ReceiveSummaryServiceImpl;
    import org.junit.Assert;
    import org.junit.Before;
    import org.junit.Test;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.MockitoAnnotations;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.LocalTime;
    import java.util.Optional;
    import static org.mockito.Mockito.when;


    public class ReceiveSummaryServiceImplTest {
        @InjectMocks
        ReceiveSummaryServiceImpl receiveSummaryServiceImpl;

        @Mock
        ReceiveDataRepository receiveDataRepository;

        @Mock
        ReceivingSummaryResponseConverter receivingSummaryResponseConverter;


        @Before
        public void setup() {
            MockitoAnnotations.initMocks(this);
        }


        @Test
        public void getReceiveSummaryTest() {
            String _id = "4665267|1804823|8264|18|18|1995-10-17|18:45:21";
            String receivingControlNumber="4665267";
            Integer storeNumber=8264;
            Integer transactionType=3;
            LocalDate finalDate=LocalDate.of(1995,10,17);
            LocalTime finalTime=LocalTime.of(18,45,21);
            Integer controlType=0;
            Integer vendorNumber=94493;
            Integer accountNumber=18;
            Integer controlSequenceNumber=1;
            Integer receiveSequenceNumber=null;
            char matchIndicator='P';
            Double totalCostAmount=37979.92;
            Double totalRetailAmount=37979.92;
            Integer freightBillId=null;
            Character businessStatusCode='J';
            Long freightBillExpandID=null;
            Character claimPendingIndicator=null;
            Character freeAstrayIndicator=null;
            Character freightConslIndicator=null;
            LocalDateTime initialReceiveTimestamp=null;
            LocalDate MDSReceiveDate=null;
            LocalDate receiveProcessDate=null;
            Double receiveWeightQuantity=null;
            String sequenceNumber=null;
            Integer departmentNumber=91;
            Integer casesReceived=0;
            LocalDateTime finalizedLoadTimestamp=null;
            Integer finalizedSequenceNumber=null;
            Integer poReceiveId=1804823;
            Integer baseDivisionNumber=18;
            String purchaseOrderId="4665267";
            Integer receiptNumber=1804823;
            String controlNumber="AB";
            String userId=null;
            Character receiptStatus='R';
            LocalDate receiptDate=null;
            Integer divisionNumber=176;
            Integer locationNumber=117;
            String carrierCode="PR";
            Integer trailerNumber=199;
            String associateName="DUM";
            LocalDateTime creationDate=null;
            String authorizedBy="NETA";
            Integer parentReceiptId=118;
            String parentReceiptNumber="EEL117";
            LocalDateTime authorizedDate=null;
            ReceiveSummary receiveSummary =new ReceiveSummary(_id, receivingControlNumber, storeNumber, transactionType, finalDate, finalTime, controlType, vendorNumber, accountNumber, controlSequenceNumber, receiveSequenceNumber, matchIndicator, totalCostAmount, totalRetailAmount, freightBillId, businessStatusCode, freightBillExpandID, claimPendingIndicator, freeAstrayIndicator, freightConslIndicator, initialReceiveTimestamp, MDSReceiveDate, receiveProcessDate, receiveWeightQuantity, sequenceNumber, departmentNumber, casesReceived, finalizedLoadTimestamp, finalizedSequenceNumber, poReceiveId, baseDivisionNumber, userId, creationDate);
            Optional<ReceiveSummary> receiveSummaryAt =Optional.of(receiveSummary);
            ReceiveSummary savedReceiveSummary = receiveSummaryAt.get();
            ReceivingSummaryResponse response= new ReceivingSummaryResponse(purchaseOrderId,receiptNumber,transactionType,controlNumber,locationNumber,divisionNumber,receiptDate,
            receiptStatus,vendorNumber,carrierCode,trailerNumber,associateName,authorizedBy,authorizedDate,totalCostAmount,totalRetailAmount,parentReceiptId,parentReceiptNumber,departmentNumber);
            when(receivingSummaryResponseConverter.convert(savedReceiveSummary)).thenReturn(response);
            when(receiveDataRepository.findById(_id)).thenReturn(receiveSummaryAt);
            Assert.assertEquals(response,receiveSummaryServiceImpl.getReceiveSummary(receivingControlNumber, poReceiveId.toString(), storeNumber.toString(),  baseDivisionNumber.toString(),  baseDivisionNumber.toString(),  finalDate.toString(),  finalTime.toString()));
        }
    }


