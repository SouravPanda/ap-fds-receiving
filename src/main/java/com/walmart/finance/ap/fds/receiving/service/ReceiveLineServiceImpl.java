
package com.walmart.finance.ap.fds.receiving.service;


import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineReqConverter;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineResponseConverter;
import com.walmart.finance.ap.fds.receiving.exception.ContentNotFoundException;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.repository.ReceiveLineDataRepository;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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


    @Autowired
    ReceiveLineDataRepository receiveLineDataRepository;

    @Autowired
    ReceivingLineResponseConverter receivingLineResponseConverter;

    @Autowired
    ReceivingLineReqConverter receivingLineRequestConverter;

    @Autowired
    MongoTemplate mongoTemplate;

    @Setter
    @Getter
    @Value("${azure.cosmosdb.collection.line}")
    private String lineCollection;

    public ReceivingResponse getLineSummary(String purchaseOrderId, String receiptNumber, String transactionType, String controlNumber, String locationNumber, String divisionNumber) {

        Query query = searchCriteriaForGet(purchaseOrderId, receiptNumber, transactionType, controlNumber, locationNumber, divisionNumber);
        List<ReceivingLine> receiveLines = mongoTemplate.find(query.limit(1000), ReceivingLine.class, lineCollection);
        List<ReceivingLineResponse> responseList;
        if (CollectionUtils.isEmpty(receiveLines)) {
            throw new ContentNotFoundException("Receiving line not found for given search criteria ","please enter valid query parameters");
        } else {
            responseList = receiveLines.stream().map((t) -> receivingLineResponseConverter.convert(t)).collect(Collectors.toList());
            ReceivingResponse successMessage = new ReceivingResponse();
            successMessage.setTimestamp(LocalDateTime.now());
            successMessage.setData(responseList);
            successMessage.setMessage(true);
            return successMessage;
        }
    }

    private Query searchCriteriaForGet(String purchaseOrderId, String receiptNumber, String transactionType, String controlNumber, String locationNumber, String divisionNumber) {

        Query dynamicQuery = new Query();
        if (StringUtils.isNotEmpty(purchaseOrderId) || (StringUtils.isNotEmpty(controlNumber))) {
            if (StringUtils.isNotEmpty(purchaseOrderId)) {
                Criteria purchaseOrderIdCriteria = Criteria.where("receivingControlNumber").is(purchaseOrderId);
                dynamicQuery.addCriteria(purchaseOrderIdCriteria);
            } else {
                Criteria controlNumberCriteria = Criteria.where("receivingControlNumber").is(controlNumber);
                dynamicQuery.addCriteria(controlNumberCriteria);
            }
        }
        if (StringUtils.isNotEmpty(receiptNumber)) {
            Criteria receiptNumberCriteria = Criteria.where("purchaseOrderReceiveID").is(receiptNumber);
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



