package com.walmart.finance.ap.fds.receiving.dao;

import com.mongodb.client.result.UpdateResult;
import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;


@Repository
public class ReceivingSummaryDaoImpl implements ReceivingSummaryDao {

    public static final Logger log = LoggerFactory.getLogger(ReceivingSummaryDaoImpl.class);

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    @Retryable(value = Exception.class, maxAttempts = ReceivingConstants.RETRY_ATTEMPTS, backoff = @Backoff(delay =
            ReceivingConstants.RETRY_BACKOFF))
    public ReceiveSummary updateReceiveSummary(Query query, Update update, FindAndModifyOptions options, Class<ReceiveSummary> receiveSummary, String summaryCollection) {

        return mongoTemplate.findAndModify(query, update, options, ReceiveSummary.class, summaryCollection);

    }

    @Override
    @Retryable(value = Exception.class, maxAttempts = ReceivingConstants.RETRY_ATTEMPTS, backoff = @Backoff(delay =
            ReceivingConstants.RETRY_BACKOFF))
    public ReceivingLine updateReceiveSummaryAndLine(Query query, Update update, FindAndModifyOptions options, Class<ReceivingLine> receivingLine, String summaryLineCollection) {

        return mongoTemplate.findAndModify(query, update, options, ReceivingLine.class, summaryLineCollection);


    }

    @Override
    @Retryable(value = Exception.class, maxAttempts = ReceivingConstants.RETRY_ATTEMPTS, backoff = @Backoff(delay =
            ReceivingConstants.RETRY_BACKOFF))
    public UpdateResult updateReceiveSummaryAndLines(Query query, Update update, Class<ReceivingLine> receivingLine, String summaryLineCollection) {
        return mongoTemplate.updateMulti(query, update, ReceivingLine.class, summaryLineCollection);
    }
}
