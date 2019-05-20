

package com.walmart.finance.ap.fds.receiving.repository;

import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReceiveLineDataRepository extends MongoRepository<ReceivingLine, String> {

}


