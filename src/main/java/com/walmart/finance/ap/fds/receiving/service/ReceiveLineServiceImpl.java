
package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineResponseConverter;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
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

    public ReceivingResponse getLineSummary(String purchaseOrderId, String receiptNumber, String transactionType, String controlNumber, String locationNumber, String divisionNumber) {
        try {
            Query query = searchCriteriaForGet(purchaseOrderId, receiptNumber, transactionType, controlNumber, locationNumber, divisionNumber);
            List<ReceivingLine> receiveLines = mongoTemplate.find(query.limit(1000), ReceivingLine.class, lineCollection);
            List<ReceivingLineResponse> responseList;
            if (CollectionUtils.isEmpty(receiveLines)) {
                throw new NotFoundException("Receiving line not found for given search criteria ", "please enter valid query parameters");
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
            throw new BadRequestException("Data Type is invalid for input values.", "Please enter valid query parameters");
        }
    }

    private Query searchCriteriaForGet(String purchaseOrderId, String receiptNumber, String transactionType, String controlNumber, String locationNumber, String divisionNumber) {
        Query dynamicQuery = new Query();
        if (StringUtils.isNotEmpty(purchaseOrderId)) {
            Criteria purchaseOrderIdCriteria = Criteria.where("purchaseOrderId").is(Long.valueOf(purchaseOrderId));
            dynamicQuery.addCriteria(purchaseOrderIdCriteria);
        }
        if (StringUtils.isNotEmpty(controlNumber)) {
            Criteria controlNumberCriteria = Criteria.where("receivingControlNumber").is(controlNumber);
            dynamicQuery.addCriteria(controlNumberCriteria);
        }
        if (StringUtils.isNotEmpty(receiptNumber)) {
            Criteria receiptNumberCriteria = Criteria.where("receiveId").is(receiptNumber);
            dynamicQuery.addCriteria(receiptNumberCriteria);
        }
        if (StringUtils.isNotEmpty(transactionType)) {
            Criteria transactionTypeCriteria = Criteria.where("transactionType").is(Integer.valueOf(transactionType));
            dynamicQuery.addCriteria(transactionTypeCriteria);
        }
        if (StringUtils.isNotEmpty(divisionNumber)) {
            Criteria divisionNumberCriteria = Criteria.where("baseDivisionNumber").is(Integer.valueOf(divisionNumber));
            dynamicQuery.addCriteria(divisionNumberCriteria);
        }
        if (StringUtils.isNotEmpty(locationNumber)) {
            Criteria locationNumberCriteria = Criteria.where("storeNumber").is(Integer.valueOf(locationNumber));
            dynamicQuery.addCriteria(locationNumberCriteria);
        }
        return dynamicQuery;
    }
}



