package com.walmart.finance.ap.fds.receiving.dao;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.model.Freight;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FreightDaoImpl implements FreightDao {

    public static final Logger log = LoggerFactory.getLogger(FreightDaoImpl.class);

    @Autowired
    private MongoTemplateWithRetry mongoTemplateWithRetry;

    @Setter
    @Value("${azure.cosmosdb.collection.freight}")
    private String freightCollection;

    @Override
    public Freight getFrightById(Long id) {
        long startTime = System.currentTimeMillis();
        Freight freight = mongoTemplateWithRetry.findById(id, Freight.class, freightCollection);
        log.info(" executeQueryInFreight :: queryTime :: " + (System.currentTimeMillis() - startTime));
        return freight;

    }

    @Override
    public List<FreightResponse> executeQueryInFreight(Query query) {
        List<FreightResponse> receiveFreights = new ArrayList<>();
        if (query != null) {
            long startTime = System.currentTimeMillis();
            receiveFreights = mongoTemplateWithRetry.find(query.limit(1000), FreightResponse.class, freightCollection);
            log.info(" executeQueryInLine :: queryTime :: " + (System.currentTimeMillis() - startTime));
        }
        return receiveFreights;
    }

}



