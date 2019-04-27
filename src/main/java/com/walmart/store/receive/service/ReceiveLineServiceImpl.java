
package com.walmart.store.receive.service;

import com.walmart.store.receive.Request.ReceivingSummaryRequest;
import com.walmart.store.receive.Response.ReceivingLineResponse;
import com.walmart.store.receive.Response.ReceivingSummaryResponse;
import com.walmart.store.receive.converter.ReceivingLineReqConverter;
import com.walmart.store.receive.converter.ReceivingLineResponseConverter;
import com.walmart.store.receive.converter.ReceivingSummaryReqConverter;
import com.walmart.store.receive.converter.ReceivingSummaryResponseConverter;
import com.walmart.store.receive.dao.ReceiveDataRepository;
import com.walmart.store.receive.dao.ReceiveLineDataRepository;
import com.walmart.store.receive.exception.ContentNotFoundException;
import com.walmart.store.receive.pojo.ReceiveSummary;
import com.walmart.store.receive.pojo.ReceivingLine;
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
    ReceivingLineReqConverter receivingLineReqConverter;



    public ReceivingLineResponse getLineSummary(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime, String sequenceNumber) {
        String id = receivingControlNumber + separator + poReceiveId + separator + storeNumber + separator + baseDivisionNumber + separator + transactionType + separator + finalDate + separator + finalTime ;
        Optional<ReceivingLine> receivingLine = receiveLineDataRepository.findById(id);

        if (receivingLine.isPresent()) {
            ReceivingLine savedReceiveLine = receivingLine.get();
            ReceivingLineResponse response = receivingLineResponseConverter.convert(savedReceiveLine);
            return response;

        } else {
            throw new ContentNotFoundException("No content found");

        }


    }
}



