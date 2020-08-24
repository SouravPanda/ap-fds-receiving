package com.walmart.finance.ap.fds.receiving.dao.queryCriteria;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLineParameters;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestQueryParameters;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.LOCATION_TYPE_STORE;

public class ReceivingLineCriteria {

    public static  List<Criteria> getCriteriaForReceivingInfoLine(Map<String, String> allRequestParams, List<String> summaryReferences, List<String> receivingControlNumbers) {

        List<Criteria> criteriaList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(summaryReferences)) {
            criteriaList.add(Criteria.where(ReceivingLineParameters.SUMMARYREFERENCE.getParameterName()).in(summaryReferences));
        }
        if (StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam()))) {
            List<String> itemNumbers = Arrays.asList(allRequestParams.get(ReceivingInfoRequestQueryParameters.ITEMNUMBERS.getQueryParam()).split(","));
            criteriaList.add(Criteria.where(ReceivingLineParameters.ITEMNUMBER.getParameterName()).in(itemNumbers.stream().map(Long::parseLong).collect(Collectors.toList())));
        }
        if (allRequestParams.get(ReceivingInfoRequestQueryParameters.LOCATIONTYPE.getQueryParam()).equals(LOCATION_TYPE_STORE) && CollectionUtils.isNotEmpty(receivingControlNumbers)) {
            criteriaList.add(Criteria.where(ReceivingLineParameters.RECEIVINGCONTROLNUMBER.getParameterName()).in(receivingControlNumbers));
        }
        if (StringUtils.isNotEmpty(allRequestParams.get(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam()))) {
            List<String> upcNumberList =
                    Arrays.asList(allRequestParams.get(ReceivingInfoRequestQueryParameters.UPCNUMBERS.getQueryParam()).split(","));
            List<String> updatedUpcNumberList = new ArrayList<>();
            /*
             * Change 13 Digit UPC Number to 16 Digit GTIN Number while hitting line
             * Combination 1 : Add "00" to beginning and "0" to the end
             * Combination 2 : Add "000" to the beginning
             */
            for (String upcNumber : upcNumberList) {
                updatedUpcNumberList.add("00" + upcNumber + "0");
                updatedUpcNumberList.add("000" + upcNumber);
            }
            criteriaList.add(Criteria.where(ReceivingLineParameters.UPCNUMBER.getParameterName()).in(updatedUpcNumberList));
        }

        return criteriaList;
    }

    public static Criteria getCriteriaForReceivingLine(Map<String, String> paramMap) {

        Criteria criteriaDefinition = new Criteria();

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingLineParameters.PURCHASEORDERID.getParameterName()))) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.PURCHASEORDERID.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.PURCHASEORDERID.trim())));
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.RECEIVINGCONTROLNUMBER))) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(paramMap.get(ReceivingConstants.RECEIVINGCONTROLNUMBER));
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.RECEIPTNUMBER))) {
            criteriaDefinition = criteriaDefinition.where(ReceivingLineParameters.RECEIVEID.getParameterName()).in(paramMap.get(ReceivingConstants.RECEIPTNUMBER).split(","));
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.TRANSACTIONTYPE))) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.TRANSACTIONTYPE.getParameterName()).is(Integer.parseInt(paramMap.get(ReceivingConstants.TRANSACTIONTYPE)));
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.DIVISIONNUMBER))) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.BASEDIVISIONNUMBER.getParameterName()).is(Integer.parseInt(paramMap.get(ReceivingConstants.DIVISIONNUMBER.trim())));
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.LOCATIONNUMBER))) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.STORENUMBER.getParameterName()).is(Integer.parseInt(paramMap.get(ReceivingConstants.LOCATIONNUMBER.trim())));
        }
        return criteriaDefinition;
    }

}
