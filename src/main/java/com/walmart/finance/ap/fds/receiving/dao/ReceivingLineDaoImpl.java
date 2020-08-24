package com.walmart.finance.ap.fds.receiving.dao;

import com.walmart.finance.ap.fds.receiving.common.ReceivingUtils;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
@Repository
public class ReceivingLineDaoImpl implements ReceivingLineDao {

    public static final Logger log = LoggerFactory.getLogger(com.walmart.finance.ap.fds.receiving.dao.ReceivingLineDaoImpl.class);

    @Autowired
    private MongoTemplateWithRetry mongoTemplateWithRetry;

    @Setter
    @Value("${azure.cosmosdb.collection.line}")
    private String lineCollection;

    @Override
    public List<ReceivingLine> executeLineAggregation(List<Criteria> criteriaList) {

        Aggregation aggregation = ReceivingUtils.aggregateBuilder(criteriaList);

        long startTime = System.currentTimeMillis();
        log.info("Aggregation query :getLineData :: Query is " + aggregation);
        List<ReceivingLine>  receivingLineList = new ArrayList<>(mongoTemplateWithRetry
                .aggregate(aggregation, lineCollection, ReceivingLine.class)
                .getMappedResults());
        log.info("Response Time : getLineData :: "+(System.currentTimeMillis()-startTime));

        return receivingLineList;

    }

    @Override
    public List<ReceivingLine> executeQueryForReceiveLine(Query query) {
        List<ReceivingLine> receivingLines = new ArrayList<>();
        if (query != null) {
            long startTime = System.currentTimeMillis();
            receivingLines = mongoTemplateWithRetry.find(query.limit(1000), ReceivingLine.class, lineCollection);
            log.info("executeQueryForReceiveLine :: queryTime :: " + (System.currentTimeMillis() - startTime));
        }
        return receivingLines;
    }

}
