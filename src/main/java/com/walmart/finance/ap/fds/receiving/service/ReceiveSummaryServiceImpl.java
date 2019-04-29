package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.exception.ContentNotFoundException;
import com.walmart.finance.ap.fds.receiving.exception.InvalidValueException;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.repository.ReceiveDataRepository;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryReqConverter;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;


@Service
public class ReceiveSummaryServiceImpl implements ReceiveSummaryService {


    private static final String separator = "|";

    @Autowired
    ReceiveDataRepository receiveDataRepository;

    @Autowired
    ReceivingSummaryResponseConverter receivingSummaryResponseConverter;

    @Autowired
    ReceivingSummaryReqConverter receivingSummaryReqConverter;


    // TODO validation for incoming against MDM needs to be added later

    public ReceiveSummary saveReceiveSummary(ReceivingSummaryRequest receivingSummaryRequest) {
        ReceiveSummary receiveSummary = receivingSummaryReqConverter.convert(receivingSummaryRequest);
        return receiveDataRepository.save(receiveSummary);

    }


    public ReceivingSummaryResponse getReceiveSummary(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime) {
        String id = formulateId(receivingControlNumber, poReceiveId, storeNumber, baseDivisionNumber, transactionType, finalDate, finalTime);
        Optional<ReceiveSummary> receiveSummary = receiveDataRepository.findById(id);
        if (receiveSummary.isPresent()) {
            ReceiveSummary savedReceiveSummary = receiveSummary.get();
            ReceivingSummaryResponse response = receivingSummaryResponseConverter.convert(savedReceiveSummary);
            return response;

        } else {
            throw new ContentNotFoundException("No content found");

        }
    }


    private String formulateId(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime) {
        return receivingControlNumber + ReceivingConstants.PIPE_SEPARATOR + poReceiveId + ReceivingConstants.PIPE_SEPARATOR + storeNumber + ReceivingConstants.PIPE_SEPARATOR +  baseDivisionNumber + ReceivingConstants.PIPE_SEPARATOR + transactionType + ReceivingConstants.PIPE_SEPARATOR +  finalDate + ReceivingConstants.PIPE_SEPARATOR +  finalTime;


    }
}


