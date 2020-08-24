package com.walmart.finance.ap.fds.receiving.dao;

import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public interface ReceivingLineDao {
    List<ReceivingLine> executeQueryForReceiveLine(Query query);
    List<ReceivingLine> executeLineAggregation(List<Criteria> criteriaList);
}
