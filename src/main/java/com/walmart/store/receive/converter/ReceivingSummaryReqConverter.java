package com.walmart.store.receive.converter;

import com.walmart.store.receive.Request.ReceivingSummaryRequest;
import com.walmart.store.receive.Response.ReceivingSummaryResponse;
import com.walmart.store.receive.pojo.ReceiveSummary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReceivingSummaryReqConverter implements Converter<ReceivingSummaryRequest, ReceiveSummary> {

    private static final String separator = "|";

    @Override
    public ReceiveSummary convert(ReceivingSummaryRequest receivingSummaryRequest) {

        String id = receivingSummaryRequest.getReceivingControlNumber() + separator + receivingSummaryRequest.getPoReceiveId() + separator + receivingSummaryRequest.getStoreNumber() + separator + receivingSummaryRequest.getBaseDivisionNumber() + separator + receivingSummaryRequest.getTransactionType() + separator + receivingSummaryRequest.getFinalDate() + separator + receivingSummaryRequest.getFinalTime();
        ReceiveSummary receiveSummary = new ReceiveSummary();
        receiveSummary.set_id(id);
        receiveSummary.setReceivingControlNumber(receivingSummaryRequest.getReceivingControlNumber());
        receiveSummary.setStoreNumber(receivingSummaryRequest.getStoreNumber());
        receiveSummary.setTransactionType(receivingSummaryRequest.getTransactionType());
        receiveSummary.setFinalDate(receivingSummaryRequest.getFinalDate());
        receiveSummary.setFinalTime(receivingSummaryRequest.getFinalTime());
        receiveSummary.setControlType(receivingSummaryRequest.getControlType());
        receiveSummary.setVendorNumber(receivingSummaryRequest.getVendorNumber());
        receiveSummary.setAccountNumber(receivingSummaryRequest.getAccountNumber());
        receiveSummary.setControlSequenceNumber(receivingSummaryRequest.getControlSequenceNumber());
        receiveSummary.setReceiveSequenceNumber(receivingSummaryRequest.getReceiveSequenceNumber());
        receiveSummary.setMatchIndicator(receivingSummaryRequest.getMatchIndicator());
        receiveSummary.setTotalCostAmount(receivingSummaryRequest.getTotalCostAmount());
        receiveSummary.setTotalRetailAmount(receivingSummaryRequest.getTotalRetailAmount());
        receiveSummary.setFreightBillId(receivingSummaryRequest.getFreightBillId());
        receiveSummary.setBusinessStatusCode(receivingSummaryRequest.getBusinessStatusCode());
        receiveSummary.setFreightBillExpandID(receivingSummaryRequest.getFreightBillExpandID());
        receiveSummary.setClaimPendingIndicator(receivingSummaryRequest.getClaimPendingIndicator());
        receiveSummary.setFreeAstrayIndicator(receivingSummaryRequest.getFreeAstrayIndicator());
        receiveSummary.setFreightConslIndicator(receivingSummaryRequest.getFreightConslIndicator());
        receiveSummary.setInitialReceiveTimestamp(receivingSummaryRequest.getInitialReceiveTimestamp());
        receiveSummary.setMDSReceiveDate(receivingSummaryRequest.getMDSReceiveDate());
        receiveSummary.setReceiveProcessDate(receivingSummaryRequest.getReceiveProcessDate());
        receiveSummary.setReceiveWeightQuantity(receivingSummaryRequest.getReceiveWeightQuantity());
        receiveSummary.setSequenceNumber(receivingSummaryRequest.getSequenceNumber());
        receiveSummary.setDepartmentNumber(receivingSummaryRequest.getDepartmentNumber());
        receiveSummary.setCasesReceived(receivingSummaryRequest.getCasesReceived());
        receiveSummary.setFinalizedLoadTimestamp(receivingSummaryRequest.getFinalizedLoadTimestamp());
        receiveSummary.setFinalizedSequenceNumber(receivingSummaryRequest.getFinalizedSequenceNumber());
        receiveSummary.setPoReceiveId(receivingSummaryRequest.getPoReceiveId());
        receiveSummary.setBaseDivisionNumber(receivingSummaryRequest.getBaseDivisionNumber());
        receiveSummary.setUserId(receivingSummaryRequest.getUserId());
        receiveSummary.setCreationDate(LocalDateTime.now());

        return receiveSummary;
    }
}
