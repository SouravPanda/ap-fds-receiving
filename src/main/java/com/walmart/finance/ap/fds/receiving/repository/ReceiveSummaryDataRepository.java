package com.walmart.finance.ap.fds.receiving.repository;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiveSummaryDataRepository extends MongoRepository<ReceiveSummary,String> {
}
