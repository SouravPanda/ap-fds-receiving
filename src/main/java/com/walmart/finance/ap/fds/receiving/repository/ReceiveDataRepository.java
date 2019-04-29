package com.walmart.finance.ap.fds.receiving.repository;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReceiveDataRepository extends MongoRepository<ReceiveSummary,String> {
    List<ReceiveSummary> findByReceivingControlNumberAndPoReceiveIdAndStoreNumberAndBaseDivisionNumberAndTransactionType(Integer receConNo, Integer poReceId,
                                                                                                                         Integer storeNo, Integer baseDivisionNo, Integer transType);

}
