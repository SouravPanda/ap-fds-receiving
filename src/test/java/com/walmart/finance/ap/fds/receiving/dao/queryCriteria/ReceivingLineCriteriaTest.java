package com.walmart.finance.ap.fds.receiving.dao.queryCriteria;

import com.walmart.finance.ap.fds.receiving.model.ReceiveLineRequestParams;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestQueryParameters;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceivingLineCriteriaTest {

    @Test
    public void testPrepareCriteria() {

        Map<String,String> mockMap = prepareMockMap();
        List<String> summaryReferences = new ArrayList<>();
        summaryReferences.add("reference1");

        List<String> controlNumbers = new ArrayList<>();
        controlNumbers.add("1");
        List<Criteria> criteria = ReceivingLineCriteria.getCriteriaForReceivingInfoLine(mockMap,
                summaryReferences,controlNumbers);
        Assert.notNull(criteria);

    }

    private Map<String,String> prepareMockMap() {

        Map mockMap = new HashMap();
        mockMap.put(ReceiveLineRequestParams.PURCHASEORDERID.getParameterName(),"766");
        mockMap.put(ReceiveLineRequestParams.CONTROLNUMBER.getParameterName(),"899");
        mockMap.put(ReceiveLineRequestParams.RECEIPTNUMBER.getParameterName(),"877");
        mockMap.put(ReceiveLineRequestParams.TRANSACTIONTYPE.getParameterName(),"99");
        mockMap.put(ReceiveLineRequestParams.DIVISIONNUMBER.getParameterName(),"0");
        mockMap.put(ReceiveLineRequestParams.LOCATIONNUMBER.getParameterName(),"8990");
        mockMap.put(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam(),"S");
        mockMap.put(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam(),"102");
        mockMap.put(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam(),"102");

        return mockMap;
    }
}
