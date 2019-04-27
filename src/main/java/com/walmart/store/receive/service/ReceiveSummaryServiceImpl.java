package com.walmart.store.receive.service;

import com.walmart.store.receive.Request.ReceivingSummaryRequest;
import com.walmart.store.receive.Response.ReceivingSummaryResponse;
import com.walmart.store.receive.converter.ReceivingSummaryReqConverter;
import com.walmart.store.receive.converter.ReceivingSummaryResponseConverter;
import com.walmart.store.receive.dao.ReceiveDataRepository;
import com.walmart.store.receive.exception.ContentNotFoundException;
import com.walmart.store.receive.pojo.ReceiveSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    /*public List<ReceivingSummaryResponse> updateReceiveSummary(ReceivingSummaryRequest receivingSummaryRequest) {
        List<ReceiveSummary> storeList = receiveDataRepository.findByReceivingControlNumberAndPoReceiveIdAndStoreNumberAndBaseDivisionNumberAndTransactionType(receivingSummaryRequest.getReceivingControlNumber(),
                receivingSummaryRequest.getPoReceiveId(), receivingSummaryRequest.getStoreNumber(), receivingSummaryRequest.getBaseDivisionNumber(), receivingSummaryRequest.getTransactionType());
        if (!storeList.isEmpty())
            for (ReceiveSummary savedStore : storeList) {
                savedStore.setInitialReceiveTimestamp(LocalDateTime.now());
            }
        receiveDataRepository.saveAll(storeList);

        return storeList;
    }*/

    // TODO validation for incoming against MDM needs to be added later

    public ReceiveSummary saveReceiveSummary(ReceivingSummaryRequest receivingSummaryRequest) {
        ReceiveSummary receiveSummary = receivingSummaryReqConverter.convert(receivingSummaryRequest);
        return receiveDataRepository.save(receiveSummary);

    }


    public ReceivingSummaryResponse getReceiveSummary(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime) {
        String id = receivingControlNumber + separator + poReceiveId + separator + storeNumber + separator + baseDivisionNumber + separator + transactionType + separator + finalDate + separator + finalTime;
        Optional<ReceiveSummary> receiveSummary = receiveDataRepository.findById(id);

        if (receiveSummary.isPresent()) {
            ReceiveSummary savedReceiveSummary = receiveSummary.get();
            ReceivingSummaryResponse response = receivingSummaryResponseConverter.convert(savedReceiveSummary);
            return response;

        } else {
            throw new ContentNotFoundException("No content found");

        }


    }
}


