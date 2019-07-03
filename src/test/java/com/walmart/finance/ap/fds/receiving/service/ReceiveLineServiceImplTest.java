package com.walmart.finance.ap.fds.receiving.service;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.internal.MongoBatchCursorAdapter;
import com.walmart.finance.ap.fds.receiving.config.MongoConfig;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineResponseConverter;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.when;

@PrepareForTest(ReceiveLineServiceImpl.class)
@RunWith(PowerMockRunner.class)
public class ReceiveLineServiceImplTest {

    @InjectMocks
    ReceiveLineServiceImpl receiveLineServiceImpl;

    @Mock
    ReceivingLineResponseConverter receivingLineResponseConverter;

    @Mock
    MongoTemplate mongoTemplate;

    @Mock
    MongoConfig mongoConfig;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getLineSummaryTest() {
        ReceivingLineResponse receivingLineResponse = new ReceivingLineResponse(0, 0, 0, 0, 0, 2.9,
                1.9, 0, 0, null, "0",
                null, null, null, 0, null, 0, 0, 10.0);
        List<ReceivingLineResponse> receivingInfoResponses = new ArrayList<>();
        receivingInfoResponses.add(receivingLineResponse);
        ReceivingResponse successMessage = new ReceivingResponse();
        successMessage.setData(receivingInfoResponses);
        when(receivingLineResponseConverter.convert(Mockito.any(ReceivingLine.class))).thenReturn(receivingLineResponse);
        mockMethod();
        ReceivingResponse result = receiveLineServiceImpl.getLineSummary("78887", "1", "1", "777", "87", "88");
        compareResults(receivingInfoResponses, result.getData());
    }

    private void mockMethod() {
        Document document = getDocument();
        FindIterable<Document> cursor = (FindIterable<Document>) Mockito.mock(FindIterable.class);
        MongoCursor<Document> iterator = (MongoBatchCursorAdapter<Document>) Mockito.mock(MongoBatchCursorAdapter.class);
        when(cursor.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true).thenReturn(false);
        when(iterator.next()).thenReturn(document);
        MongoClient mongoClient = Mockito.mock(MongoClient.class);
        MongoDatabase mongoDatabase = Mockito.mock(MongoDatabase.class);
        MongoCollection<Document> mongoCollection = Mockito.mock(MongoCollection.class);
        when(mongoConfig.mongoClient()).thenReturn(mongoClient);
        when(mongoConfig.getLineCollection()).thenReturn("vv");
        when(mongoConfig.getDatabaseName()).thenReturn("oo");
        when(mongoClient.getDatabase(Mockito.anyString())).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection(Mockito.anyString())).thenReturn(mongoCollection);
        when(mongoCollection.find(Mockito.any(Bson.class))).thenReturn(cursor);
    }

    private Document getDocument() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("_id", "852174048|80220|2240|0|103");
        hashMap.put("purchaseOrderReceiveID", "80220");
        hashMap.put("vendorNumber", 467175);
        hashMap.put("receivedQuantity", 25);
        hashMap.put("costAmount", 0.0);
        hashMap.put("retailAmount", 0.0);
        hashMap.put("receivingControlNumber", 852174048);
        hashMap.put("transactionType", 99);
        hashMap.put("storeNumber", 2240);
        hashMap.put("baseDivisionNumber", 0);
        hashMap.put("sequenceNumber", 103);
        hashMap.put("creationDate", null);
        hashMap.put("typeIndicator", "W");
        hashMap.put("writeIndicator", "DB2");
        hashMap.put("purchaseOrderNumber", "4856861482");
        hashMap.put("quantity", 1);
        hashMap.put("receivedWeightQuantity", 0.0);
        hashMap.put("receivedQuantityUnitOfMeasureCode", "02");
        hashMap.put("_class", "ReceivingLine");
        hashMap.put("inventoryMatchStatus", 0.0);
        return new Document(hashMap);
    }

    private void compareResults(List<ReceivingLineResponse> receivingInfoResponses, List<ReceivingInfoResponse> result) {
        org.assertj.core.api.Assertions.assertThat(receivingInfoResponses.get(0)).isEqualToComparingFieldByFieldRecursively(result.get(0));
    }

    @Test(expected = BadRequestException.class)
    public void getLineSummaryNumberFormatException() {
        receiveLineServiceImpl.getLineSummary(null, null, null, null,
                "null", null);
    }
}


