package com.walmart.finance.ap.fds.receiving.converter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.walmart.finance.ap.fds.receiving.config.DefaultValuesConfigProperties;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.response.ReceiveMDSResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

@Component
public class ReceivingLineResponseConverter implements Converter<ReceivingLine, ReceivingLineResponse> {

    @Autowired
    DefaultValuesConfigProperties defaultValuesConfigProperties;

    Gson gson = new Gson();

    @Override
    public ReceivingLineResponse convert(ReceivingLine receivingLine) {

        ReceivingLineResponse response = new ReceivingLineResponse();

        response.setControlNumber(StringUtils.isNotEmpty(receivingLine.getReceivingControlNumber()) ?
                receivingLine.getReceivingControlNumber() : defaultValuesConfigProperties.getReceivingControlNumber());

//        response.setDamaged(" ");

        response.setDivisionNumber(receivingLine.getBaseDivisionNumber() != null ?
                receivingLine.getBaseDivisionNumber() : defaultValuesConfigProperties.getBaseDivisionNumber());
        response.setReceiptNumber(Long.valueOf(receivingLine.getReceiveId()));
        response.setReceiptLineNumber(receivingLine.getLineNumber() != null ?
                receivingLine.getLineNumber() : defaultValuesConfigProperties.getLineNumber());
        response.setItemNumber(receivingLine.getItemNumber() != null ?
                receivingLine.getItemNumber() : defaultValuesConfigProperties.getItemNumber());
        response.setVendorNumber(receivingLine.getVendorNumber());
        response.setQuantity(receivingLine.getReceivedQuantity() != null ?
                receivingLine.getReceivedQuantity() : defaultValuesConfigProperties.getReceivedQuantity());
        response.setEachCostAmount(receivingLine.getCostAmount() != null ?
                receivingLine.getCostAmount() : defaultValuesConfigProperties.getTotalCostAmount());
        response.setEachRetailAmount(receivingLine.getRetailAmount() != null ?
                receivingLine.getRetailAmount() : defaultValuesConfigProperties.getTotalRetailAmount());
        response.setPackQuantity(receivingLine.getQuantity() != null ?
                receivingLine.getQuantity() : defaultValuesConfigProperties.getQuantity());

        response.setNumberofCasesReceived(receivingLine.getReceivedQuantity() != null ?
                receivingLine.getReceivedQuantity() : defaultValuesConfigProperties.getReceivedQuantity());
//        response.setVendorStockNumber(0);
//        response.setBottleDepositAmount(0);

        response.setPurchaseOrderNumber(StringUtils.isNotEmpty(receivingLine.getReceivingControlNumber()) ?
                receivingLine.getReceivingControlNumber() : defaultValuesConfigProperties.getReceivingControlNumber());
//        response.setParentReceiptNumber(Integer.valueOf(receivingLine.getReceiveId()));
        response.setPurchaseOrderId(receivingLine.getPurchaseOrderId()== null ? "0" : receivingLine.getPurchaseOrderId().toString());
        /*if (receivingLine.getUpcNumber() == null) {
            response.setUpc("0");
        } else {
            response.setUpc(receivingLine.getUpcNumber());
        }*/
        // TODO Need to check Item Desc. From Item Service ?
//        response.setItemDescription("NA");

        response.setVariableWeightInd(StringUtils.isNotEmpty(receivingLine.getVariableWeightIndicator()) ?
                receivingLine.getVariableWeightIndicator() : defaultValuesConfigProperties.getVariableWeightIndicator());

        //TODO Need to check  it is present in DB2?
       response.setUnitOfMeasure(receivingLine.getReceivedQuantityUnitOfMeasureCode());

        response.setReceivedWeightQuantity(receivingLine.getReceivedWeightQuantity()== null  ?
                defaultValuesConfigProperties.getReceivedWeightQuantity() : receivingLine.getReceivedWeightQuantity());
        // TODO default to 99 if not there

        response.setTransactionType(receivingLine.getTransactionType());
//        response.setControlNumber(receivingLine.getReceivingControlNumber());
        response.setLocationNumber(receivingLine.getStoreNumber());
        response.setDivisionNumber(receivingLine.getBaseDivisionNumber() != null ?
                receivingLine.getBaseDivisionNumber() : defaultValuesConfigProperties.getBaseDivisionNumber());
        response.setBottleDepositFlag(StringUtils.isNotEmpty(receivingLine.getBottleDepositFlag()) ?
                receivingLine.getBottleDepositFlag() : defaultValuesConfigProperties.getBottleDepositFlag());
        if (receivingLine.getMerchandises() != null) {
            response.setMerchandises(new ArrayList<>(receivingLine.getMerchandises().values()));
        }
        return response;
    }
}
