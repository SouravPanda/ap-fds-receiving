package com.walmart.finance.ap.fds.receiving.dao;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.model.Freight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

@Repository
public class FreightDaoImpl implements FreightDao {

    public static final Logger log = LoggerFactory.getLogger(FreightDaoImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;


    @Value("${azure.cosmosdb.collection.freight}")
    private String freightCollection;

    @Override
    @Retryable(value = Exception.class, maxAttempts = ReceivingConstants.RETRY_ATTEMPTS, backoff = @Backoff(delay =
            ReceivingConstants.RETRY_BACKOFF))
    public Freight getFrightById(Long id) {
        long startTime = System.currentTimeMillis();
        Freight freight = mongoTemplate.findById(id, Freight.class, freightCollection);
        log.info(" executeQueryInFreight :: queryTime :: " + (System.currentTimeMillis() - startTime));
        return freight;

    }

}



