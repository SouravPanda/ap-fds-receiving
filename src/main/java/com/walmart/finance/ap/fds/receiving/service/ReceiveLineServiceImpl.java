
package com.walmart.finance.ap.fds.receiving.service;


import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineReqConverter;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineResponseConverter;
import com.walmart.finance.ap.fds.receiving.exception.ContentNotFoundException;
import com.walmart.finance.ap.fds.receiving.request.ReceiveLineSearch;
import com.walmart.finance.ap.fds.receiving.request.ReceivingLineRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.repository.ReceiveLineDataRepository;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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

    public ReceivingLine saveReceiveLine(ReceivingLineRequest receivingLineRequest) {
        ReceivingLine receiveLine = receivingLineRequestConverter.convert(receivingLineRequest);
        return receiveLineDataRepository.save(receiveLine);

    }

    public ReceivingLineResponse getLineSummary(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime, String sequenceNumber) {
        String id = formulateId(receivingControlNumber, poReceiveId, storeNumber, baseDivisionNumber, transactionType, finalDate, finalTime, sequenceNumber);
        Optional<ReceivingLine> receivingLine = receiveLineDataRepository.findById(id);

        if (receivingLine.isPresent()) {
            ReceivingLine savedReceiveLine = receivingLine.get();
            ReceivingLineResponse response = receivingLineResponseConverter.convert(savedReceiveLine);
            return response;

        } else {
            throw new ContentNotFoundException("No content found");

        }


    }

    @Override
    public Page<ReceivingLineResponse> getReceiveLineSearch(ReceiveLineSearch receivingLineSearch, int pageNbr, int pageSize, String orderBy, Sort.Direction order) {
        Query query = searchCriteria(receivingLineSearch);
        Pageable pageable = PageRequest.of(pageNbr, pageSize);
        query.with(pageable);
        List<String> orderByproperties = new ArrayList<>();
        orderByproperties.add(orderBy);
        Sort sort = new Sort(order, orderByproperties);
       // query.with(sort);

        List<ReceivingLine> receiveLines = mongoTemplate.find(query, ReceivingLine.class, "receive-line");


        Page<ReceivingLine> receiveLinePage = PageableExecutionUtils.getPage(
                receiveLines,
                pageable,
                () -> mongoTemplate.count(query, ReceivingLine.class));

        return mapReceivingLineToResponse(receiveLinePage);

    }


    private Page<ReceivingLineResponse> mapReceivingLineToResponse(Page<ReceivingLine> receiveLinePage) {
        Page<ReceivingLineResponse> receivingLineResponsePage = receiveLinePage.map(new Function<ReceivingLine, ReceivingLineResponse>() {
            @Override
            public ReceivingLineResponse apply(ReceivingLine receiveLine) {
                return receivingLineResponseConverter.convert(receiveLine);
            }
        });
        return receivingLineResponsePage;
    }

    private Query searchCriteria(ReceiveLineSearch receivingLineSearch) {
        Query dynamicQuery = new Query();

        if (Optional.ofNullable(receivingLineSearch.getPurchaseOrderId()).orElse(0L) != 0L || (StringUtils.isNotEmpty(receivingLineSearch.getControlNumber()))) {
            if (Optional.ofNullable(receivingLineSearch.getPurchaseOrderId()).orElse(0L) != 0L) {
                Criteria purchaseOrderIdCriteria = Criteria.where("receivingControlNumber").is(receivingLineSearch.getPurchaseOrderId().toString());
                dynamicQuery.addCriteria(purchaseOrderIdCriteria);
            } else {
                Criteria controlNumberCriteria = Criteria.where("receivingControlNumber").is(receivingLineSearch.getControlNumber());
                dynamicQuery.addCriteria(controlNumberCriteria);
            }
        }
        if (Optional.ofNullable(receivingLineSearch.getReceiptNumber()).orElse(0L) != 0L) {
            Criteria receiptNumberCriteria = Criteria.where("purchaseOrderReceiveID").is(Integer.parseInt(receivingLineSearch.getReceiptNumber().toString()));
            dynamicQuery.addCriteria(receiptNumberCriteria);
        }
        if (Optional.ofNullable(receivingLineSearch.getTransactionType()).orElse(0) != 0) {
            Criteria transactionTypeCriteria = Criteria.where("transactionType").is(receivingLineSearch.getTransactionType());
            dynamicQuery.addCriteria(transactionTypeCriteria);
        }

        if (Optional.ofNullable(receivingLineSearch.getDivisionNumber()).orElse(0) != 0) {
            Criteria divisionNumberCriteria = Criteria.where("baseDivisionNumber").is(receivingLineSearch.getDivisionNumber());
            dynamicQuery.addCriteria(divisionNumberCriteria);
        }
        if (Optional.ofNullable(receivingLineSearch.getLocationNumber()).orElse(0) != 0) {
            Criteria locationNumberCriteria = Criteria.where("storeNumber").is(receivingLineSearch.getLocationNumber());
            dynamicQuery.addCriteria(locationNumberCriteria);
        }

        return dynamicQuery;
    }


    private String formulateId(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime, String sequenceNumber) {

        return receivingControlNumber + ReceivingConstants.PIPE_SEPARATOR + poReceiveId + ReceivingConstants.PIPE_SEPARATOR + storeNumber + ReceivingConstants.PIPE_SEPARATOR + baseDivisionNumber + ReceivingConstants.PIPE_SEPARATOR + transactionType + ReceivingConstants.PIPE_SEPARATOR + finalDate + ReceivingConstants.PIPE_SEPARATOR + finalTime + ReceivingConstants.PIPE_SEPARATOR + sequenceNumber;
    }
}



