package com.walmart.finance.ap.fds.receiving.converter;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReceivingSummaryReqConverter implements Converter<ReceivingSummaryRequest, ReceiveSummary> {

    private static final String separator = "|";

    @Override
    public ReceiveSummary convert(ReceivingSummaryRequest receivingSummaryRequest) {

        String id = receivingSummaryRequest.getControlNumber() + separator + receivingSummaryRequest.getReceiptNumbers() + separator + receivingSummaryRequest.getLocationNumber() + separator + receivingSummaryRequest.getDivisionNumber() + separator + receivingSummaryRequest.getTransactionType() + separator + receivingSummaryRequest.getReceiptDateStart() + separator + receivingSummaryRequest.getReceiptDateEnd();
        ReceiveSummary receiveSummary = new ReceiveSummary();
        receiveSummary.set_id(id);
        receiveSummary.setReceivingControlNumber(receivingSummaryRequest.getControlNumber());
        receiveSummary.setStoreNumber(receivingSummaryRequest.getLocationNumber());
        receiveSummary.setTransactionType(receivingSummaryRequest.getTransactionType());
        receiveSummary.setControlType(receivingSummaryRequest.getControlType());
        receiveSummary.setVendorNumber(receivingSummaryRequest.getVendorNumber());
        receiveSummary.setAccountNumber(receivingSummaryRequest.getAccountNumber());
        receiveSummary.setControlSequenceNumber(receivingSummaryRequest.getControlSequenceNumber());
        receiveSummary.setReceiveSequenceNumber(receivingSummaryRequest.getReceiveSequenceNumber());
        receiveSummary.setMatchIndicator(receivingSummaryRequest.getMatchIndicator());
        receiveSummary.setTotalCostAmount(receivingSummaryRequest.getTotalCostAmount());
        receiveSummary.setTotalRetailAmount(receivingSummaryRequest.getTotalRetailAmount());
        receiveSummary.setFreightBillId(receivingSummaryRequest.getFreightBillId());
        receiveSummary.setClaimPendingIndicator(receivingSummaryRequest.getClaimPendingIndicator());
        receiveSummary.setFreeAstrayIndicator(receivingSummaryRequest.getFreeAstrayIndicator());
        receiveSummary.setFreightConslIndicator(receivingSummaryRequest.getFreightConslIndicator());
        receiveSummary.setReceiveWeightQuantity(receivingSummaryRequest.getReceiveWeightQuantity());
        receiveSummary.setSequenceNumber(Integer.valueOf(receivingSummaryRequest.getSequenceNumber()));
        receiveSummary.setDepartmentNumber(receivingSummaryRequest.getDepartmentNumber());
        receiveSummary.setCasesReceived(receivingSummaryRequest.getCasesReceived());
        receiveSummary.setUserId(receivingSummaryRequest.getUserId());
        receiveSummary.setCreationDate(LocalDateTime.now());

        return receiveSummary;
    }
}
