
package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineResponseConverter;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import com.walmart.finance.ap.fds.receiving.exception.ReceivingErrors;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLineParameters;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReceiveLineServiceImpl implements ReceiveLineService {

    public static final Logger log = LoggerFactory.getLogger(ReceiveLineServiceImpl.class);

    @Autowired
    ReceivingLineResponseConverter receivingLineResponseConverter;

    @Autowired
    MongoTemplate mongoTemplate;

    @Setter
    @Getter
    @Value("${azure.cosmosdb.collection.line}")
    private String lineCollection;

    public ReceivingResponse getLineSummary(Map<String, String> allRequestParams) {
        try {
            Query query = searchCriteriaForGet(allRequestParams);
            long startTime = System.currentTimeMillis();
            List<ReceivingLine> receiveLines = mongoTemplate.find(query.limit(1000), ReceivingLine.class, lineCollection);
            log.info(" getLineSummary :: queryTime :: " + (System.currentTimeMillis() - startTime));
            List<ReceivingLineResponse> responseList;
            if (CollectionUtils.isEmpty(receiveLines)) {
                throw new NotFoundException(ReceivingErrors.RECEIVELINENOTFOUND.getParameterName(), ReceivingErrors.INVALIDQUERYPARAMS.getParameterName());
            } else {
                responseList = receiveLines.stream().map(t -> receivingLineResponseConverter.convert(t)).collect(Collectors.toList());
                ReceivingResponse successMessage = new ReceivingResponse();
                successMessage.setTimestamp(LocalDateTime.now());
                successMessage.setData(responseList);
                successMessage.setSuccess(true);
                return successMessage;
            }
        } catch (NumberFormatException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new BadRequestException(ReceivingErrors.INVALIDDATATYPE.getParameterName(), ReceivingErrors.INVALIDQUERYPARAMS.getParameterName());
        }
    }

    private Query searchCriteriaForGet(Map<String, String> paramMap) {
        Query dynamicQuery = new Query();
        Criteria criteriaDefinition = new Criteria();

        if (StringUtils.isNotEmpty(paramMap.get(ReceivingLineParameters.PURCHASEORDERID.getParameterName()))) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.PURCHASEORDERID.getParameterName()).is(Integer.valueOf(paramMap.get(ReceivingConstants.PURCHASEORDERID.trim())));
            dynamicQuery.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.RECEIVINGCONTROLNUMBER))) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.RECEIVINGCONTROLNUMBER.getParameterName()).is(paramMap.get(ReceivingConstants.RECEIVINGCONTROLNUMBER));
            dynamicQuery.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.RECEIPTNUMBER))) {
            criteriaDefinition = criteriaDefinition.where(ReceivingLineParameters.RECEIVEID.getParameterName()).in(paramMap.get(ReceivingConstants.RECEIPTNUMBER).split(","));
            dynamicQuery.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.TRANSACTIONTYPE))) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.TRANSACTIONTYPE.getParameterName()).is(Integer.parseInt(paramMap.get(ReceivingConstants.TRANSACTIONTYPE)));
            dynamicQuery.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.DIVISIONNUMBER))) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.BASEDIVISIONNUMBER.getParameterName()).is(Integer.parseInt(paramMap.get(ReceivingConstants.DIVISIONNUMBER.trim())));
            dynamicQuery.addCriteria(criteriaDefinition);
        }
        if (StringUtils.isNotEmpty(paramMap.get(ReceivingConstants.LOCATIONNUMBER))) {
            criteriaDefinition = criteriaDefinition.and(ReceivingLineParameters.STORENUMBER.getParameterName()).is(Integer.parseInt(paramMap.get(ReceivingConstants.LOCATIONNUMBER.trim())));
            dynamicQuery.addCriteria(criteriaDefinition);
        }
        return dynamicQuery;
    }
}




