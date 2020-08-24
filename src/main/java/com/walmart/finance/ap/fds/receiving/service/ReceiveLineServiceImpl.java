
package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.common.ReceivingUtils;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineResponseConverter;
import com.walmart.finance.ap.fds.receiving.dao.ReceivingLineDao;
import com.walmart.finance.ap.fds.receiving.dao.queryCriteria.ReceivingLineCriteria;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import com.walmart.finance.ap.fds.receiving.exception.ReceivingErrors;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Setter
    @Value("${months.per.shard}")
    private Integer monthsPerShard;

    @Setter
    @Value("${months.to.display}")
    private Integer monthsToDisplay;

    @Autowired
    ReceivingLineDao receivingLineDao;


    public ReceivingResponse getLineSummary(Map<String, String> allRequestParams) {
        try {
            Criteria criteriaDefinition = ReceivingLineCriteria.getCriteriaForReceivingLine(allRequestParams);

            Query dynamicQuery = new Query();
            dynamicQuery.addCriteria(criteriaDefinition);

            if (StringUtils.isNotEmpty(allRequestParams.get(ReceivingConstants.LOCATIONNUMBER))) {
                ReceivingUtils.updateQueryForPartitionKey(null, allRequestParams,
                        Integer.parseInt(allRequestParams.get(ReceivingConstants.LOCATIONNUMBER.trim())), dynamicQuery,
                        monthsPerShard, monthsToDisplay);
            }

            long startTime = System.currentTimeMillis();
            List<ReceivingLine> receiveLines = receivingLineDao.executeQueryForReceiveLine(dynamicQuery);
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


}




