package com.walmart.store.receive.converter;

import com.walmart.store.receive.Request.ReceivingLineRequest;
import com.walmart.store.receive.pojo.ReceivingLine;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReceivingLineReqConverter implements Converter<ReceivingLineRequest, ReceivingLine> {

    private static final String separator = "|";

    @Override
    public ReceivingLine convert(ReceivingLineRequest receivingLineRequest) {

        String id = receivingLineRequest.getReceivingControlNumber() + separator + receivingLineRequest.getPurchaseOrderReceiveID() + separator + receivingLineRequest.getPurchaseOrderReceiveID() + separator + receivingLineRequest.getBaseDivisionNumber() + separator + receivingLineRequest.getTransactionType() + separator + receivingLineRequest.getFinalDate() + separator + receivingLineRequest.getFinalTime() +separator + receivingLineRequest.getSequenceNumber();
        ReceivingLine receivingLine = new ReceivingLine();
        receivingLine.setPurchaseOrderReceiveID(receivingLineRequest.getPurchaseOrderReceiveID());
        receivingLine.setLineNumber(receivingLineRequest.getLineNumber());
        receivingLine.setItemNumber(receivingLineRequest.getItemNumber());
        receivingLine.setVendorNumber(receivingLineRequest.getVendorNumber());
        receivingLine.setReceivedQuantity(receivingLineRequest.getReceivedQuantity());
        receivingLine.setCostAmount(receivingLineRequest.getCostAmount());
        receivingLine.setRetailAmount(receivingLineRequest.getRetailAmount());
        receivingLine.setPackQuantity(receivingLineRequest.getPackQuantity());
        receivingLine.setNumberOfCasesReceived(receivingLineRequest.getNumberOfCasesReceived());
        receivingLine.setVendorStockNumber(receivingLineRequest.getVendorStockNumber());
        receivingLine.setBottleStockNumber(receivingLineRequest.getBottleStockNumber());
        receivingLine.setDamaged(receivingLineRequest.getDamaged());
        receivingLine.setReceivingControlNumber(receivingLineRequest.getReceivingControlNumber());
        receivingLine.setPurchaseReceiptNumber(receivingLineRequest.getPurchaseOrderReceiveID());
        receivingLine.setReceivingControlNumber(receivingLineRequest.getReceivingControlNumber());
        receivingLine.setUpcNumber(receivingLineRequest.getUpcNumber());
        receivingLine.setItemDescription(receivingLineRequest.getItemDescription());
        receivingLine.setVariableWeightInd(receivingLineRequest.getVariableWeightInd());
        receivingLine.setUnitOfMeasure(receivingLineRequest.getUnitOfMeasure());
        receivingLine.setReceivedWeightQuantity(receivingLineRequest.getReceivedWeightQuantity());
        receivingLine.setTransactionType(receivingLineRequest.getTransactionType());
        receivingLine.setReceivingControlNumber(receivingLineRequest.getReceivingControlNumber());
        receivingLine.setStoreNumber(receivingLineRequest.getStoreNumber());
        receivingLine.setBaseDivisionNumber(receivingLineRequest.getBaseDivisionNumber());
        return receivingLine;
    }
}
