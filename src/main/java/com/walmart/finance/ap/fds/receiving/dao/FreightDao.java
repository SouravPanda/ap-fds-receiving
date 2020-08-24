package com.walmart.finance.ap.fds.receiving.dao;
import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.model.Freight;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public interface FreightDao {

    Freight getFrightById(Long id );

    List<FreightResponse> executeQueryInFreight(Query query);
}
