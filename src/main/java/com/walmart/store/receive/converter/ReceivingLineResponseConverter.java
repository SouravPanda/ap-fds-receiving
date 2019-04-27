package com.walmart.store.receive.converter;

import com.walmart.store.receive.Response.ReceivingLineResponse;
import com.walmart.store.receive.Response.ReceivingSummaryResponse;
import com.walmart.store.receive.pojo.ReceiveSummary;
import com.walmart.store.receive.pojo.ReceivingLine;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ReceivingLineResponseConverter implements Converter<ReceivingLine, ReceivingLineResponse> {

    @Override
    public ReceivingLineResponse convert(ReceivingLine receivingLine) {

        ReceivingLineResponse response = new ReceivingLineResponse();
        response.setBottleStockNumber(receivingLine.getBottleStockNumber());
        response.setControlNumber(receivingLine.getReceivingControlNumber());
        response.setDamaged(receivingLine.getDamaged());
        response.setDivisionNumber(receivingLine.getBaseDivisionNumber());
        response.setEachCostAmount(receivingLine.getCostAmount());
        response.setReceiptNumber(receivingLine.getPurchaseOrderReceiveID());
        response.setReceiptLineNumber(receivingLine.getLineNumber());
        response.setItemNumber(receivingLine.getItemNumber());
        response.setVendorNumber(receivingLine.getVendorNumber());
        response.setQuantity(receivingLine.getReceivedQuantity());
        response.setEachCostAmount(receivingLine.getCostAmount());
        response.setEachRetailAmount(receivingLine.getRetailAmount());
        response.setPackQuantity(receivingLine.getPackQuantity());
        response.setNumberofCasesReceived(receivingLine.getNumberOfCasesReceived());
        response.setVendorStockNumber(receivingLine.getVendorStockNumber());
        response.setBottleStockNumber(receivingLine.getBottleStockNumber());
        response.setDamaged(receivingLine.getDamaged());
        response.setPurchaseOrderNumber(receivingLine.getReceivingControlNumber());
        response.setPurchaseReceiptNumber(receivingLine.getPurchaseOrderReceiveID());
        response.setPurchasedOrderId(receivingLine.getReceivingControlNumber());
        response.setUpc(receivingLine.getUpcNumber());
        response.setItemDescription(receivingLine.getItemDescription());
        response.setVariableWeightInd(receivingLine.getVariableWeightInd());
        response.setUnitOfMeasure(receivingLine.getUnitOfMeasure());
        response.setReceivedWeightQuantity(receivingLine.getReceivedWeightQuantity());
        response.setTransactionType(receivingLine.getTransactionType());
        response.setControlNumber(receivingLine.getReceivingControlNumber());
        response.setLocationNumber(receivingLine.getStoreNumber());
        response.setDivisionNumber(receivingLine.getBaseDivisionNumber());
        response.setFinalDate(receivingLine.getFinalDate());
        response.setFinalTimestamp(receivingLine.getFinalTime());
        response.setSequenceNumber(receivingLine.getSequenceNumber());
        return response;
    }
}
