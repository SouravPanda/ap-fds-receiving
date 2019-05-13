package com.walmart.finance.ap.fds.receiving.converter;

import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ReceivingLineResponseConverter implements Converter<ReceivingLine, ReceivingLineResponse> {

    @Override
    public ReceivingLineResponse convert(ReceivingLine receivingLine) {

        ReceivingLineResponse response = new ReceivingLineResponse();

        response.setControlNumber(Integer.parseInt(receivingLine.getReceivingControlNumber()));

        response.setDamaged(" ");

        if (receivingLine.getBaseDivisionNumber() == 0) {
            response.setDivisionNumber(0);
        } else {
            response.setDivisionNumber(receivingLine.getBaseDivisionNumber());
        }
        response.setEachCostAmount(receivingLine.getCostAmount());
        response.setReceiptNumber(Integer.valueOf(receivingLine.getPurchaseOrderReceiveID()));
        if(receivingLine.getLineNumber()==null){
            response.setReceiptLineNumber(0) ;
        } else {
            response.setReceiptLineNumber(receivingLine.getLineNumber());
        }
        response.setItemNumber(receivingLine.getItemNumber());
        response.setVendorNumber(receivingLine.getVendorNumber());
        response.setQuantity(receivingLine.getReceivedQuantity());
        response.setEachCostAmount(receivingLine.getCostAmount());
        response.setEachRetailAmount(receivingLine.getRetailAmount());
        response.setPackQuantity(receivingLine.getQuantity());


        response.setNumberofCasesReceived(0);
        response.setVendorStockNumber(0);
        response.setBottleStockNumber(0);

        response.setPurchaseOrderNumber(Integer.parseInt(receivingLine.getReceivingControlNumber()));
        response.setPurchaseReceiptNumber(Integer.valueOf(receivingLine.getPurchaseOrderReceiveID()));
        response.setPurchasedOrderId(Integer.parseInt(receivingLine.getReceivingControlNumber()));
        if (receivingLine.getUpcNumber() == null) {
            response.setUpc(0);
        } else {
            response.setUpc(receivingLine.getUpcNumber());
        }
        // TODO Need to check Item Desc. From Item Service ?
        response.setItemDescription("NA");

        response.setVariableWeightInd(" ");

        //TODO Need to check  it is present in DB2?
       response.setUnitOfMeasure("lbs");

        response.setReceivedWeightQuantity(" ");
        // TODO default to 99 if not there

        if(receivingLine.getTransactionType()==null){
            response.setTransactionType(99);
        }else {

            response.setTransactionType(receivingLine.getTransactionType());
        }
        response.setControlNumber(Integer.parseInt(receivingLine.getReceivingControlNumber()));
        response.setLocationNumber(receivingLine.getStoreNumber());
        // TODO default to 0 if not there
        if(receivingLine.getBaseDivisionNumber()==null){
            response.setDivisionNumber(0);
        }
        else {
            response.setDivisionNumber(receivingLine.getBaseDivisionNumber());
        }
        return response;
    }
}
