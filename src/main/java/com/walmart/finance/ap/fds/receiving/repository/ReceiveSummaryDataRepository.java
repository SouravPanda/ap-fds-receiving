package com.walmart.finance.ap.fds.receiving.repository;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiveSummaryDataRepository extends MongoRepository<ReceiveSummary,String> {
    //List<ReceiveSummary> findByReceivingControlNumberAndPoReceiveIdAndStoreNumberAndBaseDivisionNumberAndTransactionType(Integer receConNo, Integer poReceId,
                                                                                                                        // Integer storeNo, Integer baseDivisionNo, Integer transType);


}
