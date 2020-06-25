package com.walmart.finance.ap.fds.receiving.dao;

import com.mongodb.client.result.UpdateResult;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


public interface ReceivingSummaryDao {
    public ReceiveSummary updateReceiveSummary(Query query, Update update, FindAndModifyOptions options, Class<ReceiveSummary> receiveSummary, String summaryCollection);

    public ReceivingLine updateReceiveSummaryAndLine(Query query, Update update, FindAndModifyOptions options, Class<ReceivingLine> receivingLine, String summaryLineCollection);

    public UpdateResult updateReceiveSummaryAndLines(Query query, Update update, Class<ReceivingLine> receivingLine, String summaryLineCollection);
}
