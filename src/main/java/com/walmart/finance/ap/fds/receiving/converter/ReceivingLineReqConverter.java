package com.walmart.finance.ap.fds.receiving.converter;

import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ReceivingLineReqConverter implements Converter<ReceivingSummaryLineRequest, ReceivingLine> {

    private static final String separator = "|";

    @Override
    public ReceivingLine convert(ReceivingSummaryLineRequest receivingLineRequest) {

        String id = receivingLineRequest.getReceivingControlNumber() + separator + receivingLineRequest.getPurchaseOrderReceiveID() + separator + receivingLineRequest.getPurchaseOrderReceiveID() + separator + receivingLineRequest.getBaseDivisionNumber() + separator + receivingLineRequest.getTransactionType() + separator + receivingLineRequest.getFinalDate() + separator + receivingLineRequest.getFinalTime() +separator + receivingLineRequest.getSequenceNumber();
        ReceivingLine receivingLine = new ReceivingLine();
        receivingLine.setPurchaseOrderReceiveID(receivingLineRequest.getPurchaseOrderReceiveID().toString());
        receivingLine.setLineNumber(receivingLineRequest.getLineNumber());
        receivingLine.setItemNumber(receivingLineRequest.getItemNumber());
        receivingLine.setVendorNumber(receivingLineRequest.getVendorNumber());
        receivingLine.setReceivedQuantity(receivingLineRequest.getReceivedQuantity());
        receivingLine.setCostAmount(receivingLineRequest.getCostAmount());
        receivingLine.setRetailAmount(receivingLineRequest.getRetailAmount());
        receivingLine.setTransactionType(receivingLineRequest.getTransactionType());
        receivingLine.setReceivingControlNumber(String.valueOf(receivingLineRequest.getReceivingControlNumber()));
        receivingLine.setStoreNumber(receivingLineRequest.getStoreNumber());
        receivingLine.setBaseDivisionNumber(receivingLineRequest.getBaseDivisionNumber());
        return receivingLine;
    }
}
