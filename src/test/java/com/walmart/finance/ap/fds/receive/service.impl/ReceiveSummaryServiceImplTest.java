
 package com.walmart.finance.ap.fds.receive.service.impl;

    import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
    import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
    import com.walmart.finance.ap.fds.receiving.repository.ReceiveSummaryDataRepository;
    import com.walmart.finance.ap.fds.receiving.request.ReceivingSummarySearch;
    import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
    import com.walmart.finance.ap.fds.receiving.service.ReceiveSummaryServiceImpl;
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
    import java.time.LocalTime;
    import java.util.Optional;
    import static org.mockito.Mockito.when;


    public class ReceiveSummaryServiceImplTest {
        @InjectMocks
        ReceiveSummaryServiceImpl receiveSummaryServiceImpl;

        @Mock
        ReceiveSummaryDataRepository receiveDataRepository;

        @Mock
        ReceivingSummaryResponseConverter receivingSummaryResponseConverter;


        @Before
        public void setup() {
            MockitoAnnotations.initMocks(this);
        }

            String _id = "4665267|1804823|8264|18|18|1995-10-17|18:45:21";
            String receivingControlNumber = "4665267";
            Integer storeNumber = 8264;
            Integer transactionType = 3;
            LocalDate finalDate = LocalDate.of(1995, 10, 17);
            LocalTime finalTime = LocalTime.of(18, 45, 21);
            Integer controlType = 0;
            Integer vendorNumber = 94493;
            Integer accountNumber = 18;
            Integer controlSequenceNumber = 1;
            Integer receiveSequenceNumber = null;
            char matchIndicator = 'P';
            Double totalCostAmount = 37979.92;
            Double totalRetailAmount = 37979.92;
            Integer freightBillId = null;
            Character businessStatusCode = 'J';
            Long freightBillExpandID = null;
            Character claimPendingIndicator = null;
            Character freeAstrayIndicator = null;
            Character freightConslIndicator = null;
            LocalDateTime initialReceiveTimestamp = null;
            LocalDate MDSReceiveDate = null;
            LocalDate receiveProcessDate = null;
            Double receiveWeightQuantity = null;
            String sequenceNumber = null;
            Integer departmentNumber = 91;
            Integer casesReceived = 0;
            LocalDateTime finalizedLoadTimestamp = null;
            Integer finalizedSequenceNumber = null;
            Integer poReceiveId = 1804823;
            Integer baseDivisionNumber = 18;
            String purchaseOrderId = "4665267";
            Integer receiptNumber = 1804823;
            String controlNumber = "AB";
            String userId = null;
            Character receiptStatus = 'R';
            LocalDate receiptDate = null;
            Integer divisionNumber = 176;
            Integer locationNumber = 117;
            String carrierCode = "PR";
            Integer trailerNumber = 199;
            String associateName = "DUM";
            LocalDateTime creationDate = null;
            String authorizedBy = "NETA";
            Integer parentReceiptId = 118;
            String parentReceiptNumber = "EEL117";
            LocalDateTime authorizedDate = null;
            Long purchaseOrderNumber = 0L;
            LocalDateTime receiptDateStart=null;
            LocalDateTime receiptDateEnd=null;
            Long invoiceId=9L;
            String invoiceNumber="AA";
            Integer countryCode=0;
            Character typeIndicator='J';
            Character writeIndicator='L';
            Query query = new Query();

            @Test
            public void getReceiveSummaryTest() {
            ReceiveSummary receiveSummary = new ReceiveSummary(_id, receivingControlNumber, storeNumber, transactionType, finalDate, finalTime, controlType, vendorNumber, accountNumber, controlSequenceNumber, receiveSequenceNumber, matchIndicator, totalCostAmount, totalRetailAmount, freightBillId, businessStatusCode, freightBillExpandID, claimPendingIndicator, freeAstrayIndicator, freightConslIndicator, initialReceiveTimestamp, MDSReceiveDate, receiveProcessDate, receiveWeightQuantity, sequenceNumber, departmentNumber, casesReceived, finalizedLoadTimestamp, finalizedSequenceNumber, poReceiveId, baseDivisionNumber, userId, creationDate, purchaseOrderNumber.toString(),writeIndicator,typeIndicator);
            Optional<ReceiveSummary> receiveSummaryAt = Optional.of(receiveSummary);
            ReceiveSummary savedReceiveSummary = receiveSummaryAt.get();
            ReceivingSummaryResponse response = new ReceivingSummaryResponse(purchaseOrderId, receiptNumber, transactionType, controlNumber, locationNumber, divisionNumber, receiptDate,
                    receiptStatus, vendorNumber, carrierCode, trailerNumber, associateName, authorizedBy, authorizedDate, totalCostAmount, totalRetailAmount, parentReceiptId, parentReceiptNumber, departmentNumber);
            when(receivingSummaryResponseConverter.convert(savedReceiveSummary)).thenReturn(response);
            when(receiveDataRepository.findById(_id)).thenReturn(receiveSummaryAt);

            Assert.assertEquals(response, receiveSummaryServiceImpl.getReceiveSummary(receivingControlNumber, poReceiveId.toString(), storeNumber.toString(), baseDivisionNumber.toString(), baseDivisionNumber.toString(), finalDate.toString(), finalTime.toString()));
        }

        @Test
        public void getReceiveSummarySearchTest() {

            query= new Query();
            ReceivingSummarySearch receivingSummarySearch = new ReceivingSummarySearch(purchaseOrderNumber, Long.valueOf(purchaseOrderId), receiptNumber.toString()
                    , transactionType, controlNumber, locationNumber, divisionNumber, vendorNumber, departmentNumber, invoiceId, invoiceNumber, countryCode, receiptDateStart
                    , receiptDateEnd);
            query = searchCriteria(receivingSummarySearch);

           // Assert.assertEquals(receiveSummaryServiceImpl.searchCriteria(receivingSummarySearch,dynamicQuery).toString(),dynamicQuery.toString());
        }
        private Query searchCriteria(ReceivingSummarySearch receivingSummarySearch){
            Query dynamicQuery= new Query();
            Criteria criteria=Criteria.where("receivingControlNumber").is(4665267).and("baseDivisionNumber").is(18).and("mdsReceiveDate").is(null)
                    .and("transactionType").is(3).and("storeNumber").is(8264).and("purchaseOrderNumber").is("TTTY").and("poReceiveId").is(1804823).and("departmentNumber")
                    .is(91).and("vendorNumber").is(94493);
            dynamicQuery.addCriteria(criteria);
            return dynamicQuery;
        }
    }



