
package com.walmart.finance.ap.fds.receiving.service;


import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineReqConverter;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineResponseConverter;
import com.walmart.finance.ap.fds.receiving.exception.ContentNotFoundException;
import com.walmart.finance.ap.fds.receiving.request.ReceivingLineRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.repository.ReceiveLineDataRepository;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReceiveLineServiceImpl implements ReceiveLineService {


    private static final String separator = "|";

    @Autowired
    ReceiveLineDataRepository receiveLineDataRepository;

    @Autowired
    ReceivingLineResponseConverter receivingLineResponseConverter;

    @Autowired
    ReceivingLineReqConverter receivingLineRequestConverter;

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


    private String formulateId(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime, String sequenceNumber) {

        return receivingControlNumber + ReceivingConstants.PIPE_SEPARATOR + poReceiveId + ReceivingConstants.PIPE_SEPARATOR + storeNumber + ReceivingConstants.PIPE_SEPARATOR + baseDivisionNumber + ReceivingConstants.PIPE_SEPARATOR +transactionType + ReceivingConstants.PIPE_SEPARATOR + finalDate  + ReceivingConstants.PIPE_SEPARATOR + finalTime + ReceivingConstants.PIPE_SEPARATOR + sequenceNumber;
    }
}



