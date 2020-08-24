package com.walmart.finance.ap.fds.receiving.dao.queryCriteria;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummaryRequestParams;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestQueryParameters;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.Assert;

import java.util.*;

public class ReceivingSummaryCriteriaTest {

    @Test
    public void testCriteriaForSummary() {
        Map<String,String> mockMap = prepareMockMap();
        List<Criteria> criteriaList = ReceivingSummaryCriteria.getCriteriaForReceivingSummary(mockMap);
        Assert.notNull(criteriaList,"criteria for summary");
    }

    private Map<String, String> prepareMockMap() {

        Map mockMap = new HashMap();
        mockMap.put(ReceiveSummaryRequestParams.PURCHASEORDERNUMBER.getParameterName(), "999");
        mockMap.put(ReceiveSummaryRequestParams.CONTROLNUMBER.getParameterName(), "000");
        mockMap.put(ReceiveSummaryRequestParams.LOCATIONNUMBER.getParameterName(), "998");
        mockMap.put(ReceiveSummaryRequestParams.DEPARTMENTNUMBER.getParameterName(), "98");
        mockMap.put(ReceiveSummaryRequestParams.UPCNUMBERS.getParameterName(), "89776");
        mockMap.put(ReceiveSummaryRequestParams.VENDORNUMBER.getParameterName(), "0987");
        mockMap.put(ReceiveSummaryRequestParams.DIVISIONNUMBER.getParameterName(), "90");
        mockMap.put(ReceiveSummaryRequestParams.ITEMNUMBERS.getParameterName(), "9880");
        mockMap.put(ReceiveSummaryRequestParams.PURCHASEORDERID.getParameterName(), "456");
        mockMap.put(ReceiveSummaryRequestParams.RECEIPTNUMBERS.getParameterName(), "234");
        mockMap.put(ReceiveSummaryRequestParams.RECEIPTDATEEND.getParameterName(), "2017-12-12");
        mockMap.put(ReceiveSummaryRequestParams.RECEIPTDATESTART.getParameterName(), "2015-12-12");
        mockMap.put(ReceiveSummaryRequestParams.TRANSACTIONTYPE.getParameterName(), "0");
        mockMap.put(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam(),"W");
        return mockMap;
    }
}
