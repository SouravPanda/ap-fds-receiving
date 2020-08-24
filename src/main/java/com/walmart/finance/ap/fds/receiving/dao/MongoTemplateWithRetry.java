package com.walmart.finance.ap.fds.receiving.dao;

import com.mongodb.client.result.UpdateResult;
import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoTemplateWithRetry {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Retryable(value = Exception.class, maxAttempts = ReceivingConstants.RETRY_ATTEMPTS, backoff = @Backoff(delay =
            ReceivingConstants.RETRY_BACKOFF))
    public <T> List<T> find(Query query, Class<T> entityClass, String collectionName) {
        return mongoTemplate.find(query, entityClass, collectionName);
    }

    @Retryable(value = Exception.class, maxAttempts = ReceivingConstants.RETRY_ATTEMPTS, backoff = @Backoff(delay =
            ReceivingConstants.RETRY_BACKOFF))
    public <T> AggregationResults<T> aggregate(Aggregation aggregation, String collectionName, Class<T> entityClass) {
        return mongoTemplate.aggregate(aggregation, collectionName, entityClass);
    }

    @Retryable(value = Exception.class, maxAttempts = ReceivingConstants.RETRY_ATTEMPTS, backoff = @Backoff(delay =
            ReceivingConstants.RETRY_BACKOFF))
    public <T> T findById(Object id, Class<T> entityClass, String collectionName) {
        return mongoTemplate.findById(id, entityClass, collectionName);
    }

    @Retryable(value = Exception.class, maxAttempts = ReceivingConstants.RETRY_ATTEMPTS, backoff = @Backoff(delay =
            ReceivingConstants.RETRY_BACKOFF))
    public <T> T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> entityClass, String collectionName) {
        return mongoTemplate.findAndModify(query,update,options,entityClass,collectionName);

    }

    @Retryable(value = Exception.class, maxAttempts = ReceivingConstants.RETRY_ATTEMPTS, backoff = @Backoff(delay =
            ReceivingConstants.RETRY_BACKOFF))
    public UpdateResult updateMulti(Query queryForLine, Update updateLine, Class<?> entityClass, String collectionName) {
        return mongoTemplate.updateMulti(queryForLine,updateLine, entityClass,collectionName);
    }
}
