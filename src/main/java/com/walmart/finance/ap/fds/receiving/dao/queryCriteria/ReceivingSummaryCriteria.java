package com.walmart.finance.ap.fds.receiving.dao.queryCriteria;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.common.ReceivingUtils;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummaryCosmosDBParameters;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestQueryParameters;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;
import java.util.*;

import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.LOCATION_TYPE_WAREHOUSE;

public class ReceivingSummaryCriteria {

    public static List<Criteria> getCriteriaForReceivingSummary(Map<String, String> paramMap) {


        List<Criteria> criteria = new ArrayList<>();

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.CONTROLNUMBER))) {
            List<String> controlNumList =
                    Arrays.asList(paramMap.get(ReceivingConstants.CONTROLNUMBER).split(","));
            Criteria controlNumberCriteria =
                    Criteria.where(ReceiveSummaryCosmosDBParameters.RECEIVINGCONTROLNUMBER.getParameterName()).in(controlNumList);
            criteria.add(controlNumberCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.PURCHASEORDERID))) {
            Criteria purchaseOrderIdCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.PURCHASEORDERID.getParameterName()).is(Long.valueOf(paramMap.get(ReceivingConstants.PURCHASEORDERID)));
            criteria.add(purchaseOrderIdCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.DIVISIONNUMBER))) {
            Criteria baseDivisionNumberCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.BASEDIVISIONNUMBER.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.DIVISIONNUMBER)));
            criteria.add(baseDivisionNumberCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.RECEIPTDATESTART)) && StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.RECEIPTDATEEND))) {
            LocalDateTime startDate = ReceivingUtils.getDate(paramMap.get(ReceivingConstants.RECEIPTDATESTART) + ReceivingConstants.TIMESTAMP_TIME_ZERO);
            LocalDateTime endDate = ReceivingUtils.getDate(paramMap.get(ReceivingConstants.RECEIPTDATEEND) + ReceivingConstants.TIMESTAMP_23_59_59);
            String applicableDateField = ReceiveSummaryCosmosDBParameters.DATERECEIVED.getParameterName();
            if (paramMap.containsKey(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam())
                    && paramMap.get(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam())
                    .equals(LOCATION_TYPE_WAREHOUSE)) {
                applicableDateField = ReceiveSummaryCosmosDBParameters.RECEIVEPROCESSDATE.getParameterName();
            }
            Criteria mdsReceiveDateCriteria = Criteria.where(applicableDateField).gte(startDate).lte(endDate);
            criteria.add(mdsReceiveDateCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.TRANSACTIONTYPE))) {
            Criteria transactionTypeCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.TRANSACTIONTYPE.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.TRANSACTIONTYPE)));
            criteria.add(transactionTypeCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.LOCATIONNUMBER))) {
            Criteria locationCriteria =
                    Criteria.where(ReceiveSummaryCosmosDBParameters.STORENUMBER.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.LOCATIONNUMBER)));
            criteria.add(locationCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.PURCHASEORDERNUMBER))) {
            Criteria purchaseOrderNumberCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.PURCHASEORDERNUMBER.getParameterName()).is(paramMap.get(ReceivingConstants.PURCHASEORDERNUMBER));
            criteria.add(purchaseOrderNumberCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.RECEIPTNUMBERS))) {
            Criteria poReceiveIdCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.RECEIVEID.getParameterName()).in(paramMap.get(ReceivingConstants.RECEIPTNUMBERS).split(","));
            criteria.add(poReceiveIdCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.DEPARTMENTNUMBER))) {
            Criteria departmentNumberCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.DEPARTMENTNUMBER.getParameterName()).is(paramMap.get(ReceivingConstants.DEPARTMENTNUMBER));
            criteria.add(departmentNumberCriteria);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.VENDORNUMBER))) {
            Criteria vendorNumberCriteria = Criteria.where(ReceiveSummaryCosmosDBParameters.VENDORNUMBER.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.VENDORNUMBER)));
            criteria.add(vendorNumberCriteria);
        }

        return criteria;
    }

}
