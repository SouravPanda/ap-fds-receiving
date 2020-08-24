package com.walmart.finance.ap.fds.receiving.dao;

import com.mongodb.client.result.UpdateResult;
import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.common.ReceivingUtils;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class ReceivingSummaryDaoImpl implements ReceivingSummaryDao {

    public static final Logger log = LoggerFactory.getLogger(ReceivingSummaryDaoImpl.class);

    @Autowired
    private MongoTemplateWithRetry mongoTemplateWithRetry;

    @Setter
    @Value("${azure.cosmosdb.collection.summary}")
    private String summaryCollection;

    @Override
    public ReceiveSummary updateReceiveSummary(Query query, Update update, FindAndModifyOptions options, Class<ReceiveSummary> receiveSummary, String summaryCollection) {

        return mongoTemplateWithRetry.findAndModify(query, update, options, ReceiveSummary.class, summaryCollection);

    }

    @Override
    public ReceivingLine updateReceiveSummaryAndLine(Query query, Update update, FindAndModifyOptions options, Class<ReceivingLine> receivingLine, String summaryLineCollection) {

        return mongoTemplateWithRetry.findAndModify(query, update, options, ReceivingLine.class, summaryLineCollection);


    }

    @Override
    public UpdateResult updateReceiveSummaryAndLines(Query query, Update update, Class<ReceivingLine> receivingLine, String summaryLineCollection) {
        return mongoTemplateWithRetry.updateMulti(query, update, ReceivingLine.class, summaryLineCollection);
    }

    @Override
    public List<ReceiveSummary> executeSummaryAggregation(List<Criteria> criteria) {

        Aggregation aggregation = ReceivingUtils.aggregateBuilder(criteria);

        long startTime = System.currentTimeMillis();
        log.info("Aggregation query : getSummaryData :: Query is " + aggregation);
        List<ReceiveSummary>  receiveSummaryList = new ArrayList<>(mongoTemplateWithRetry
                .aggregate(aggregation, summaryCollection, ReceiveSummary.class)
                .getMappedResults());
        log.info("Response Time : getSummaryData :: "+(System.currentTimeMillis()-startTime));

        return receiveSummaryList;
    }

    @Override
    public List<ReceiveSummary> executeQueryForReceiveSummary(Query query) {
        List<ReceiveSummary> receiveSummaries = new ArrayList<>();
        if (query != null) {
            long startTime = System.currentTimeMillis();
            receiveSummaries = mongoTemplateWithRetry.find(query.limit(1000), ReceiveSummary.class, summaryCollection);
            log.info("executeQueryForReceiveSummary :: queryTime :: " + (System.currentTimeMillis() - startTime));
        }
        return receiveSummaries;
    }
}
