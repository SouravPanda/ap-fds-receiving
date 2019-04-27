

package com.walmart.store.receive.dao;

import com.walmart.store.receive.Response.ReceivingLineResponse;
import com.walmart.store.receive.pojo.ReceiveSummary;
import com.walmart.store.receive.pojo.ReceivingLine;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReceiveLineDataRepository extends MongoRepository<ReceivingLine,String> {
    public ReceivingLineResponse getLineSummary(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime, String sequenceNumber);

}


