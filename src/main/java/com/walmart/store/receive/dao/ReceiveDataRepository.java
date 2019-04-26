package com.walmart.store.receive.dao;

import com.walmart.store.receive.pojo.ReceiveSummary;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReceiveDataRepository extends MongoRepository<ReceiveSummary,String> {
    List<ReceiveSummary> findByReceivingControlNumberAndPoReceiveIdAndStoreNumberAndBaseDivisionNumberAndTransactionType(Integer receConNo, Integer poReceId,
                                                                                                                         Integer storeNo, Integer baseDivisionNo, Integer transType);

}
