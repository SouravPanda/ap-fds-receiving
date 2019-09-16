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
        response.setEachCostAmount(receivingLine.getCostAmount());
        response.setReceiptNumber(Long.valueOf(receivingLine.getReceiveId()));
        response.setReceiptLineNumber(receivingLine.getLineNumber() != null ?
                receivingLine.getLineNumber() : defaultValuesConfigProperties.getLineNumber());
        response.setItemNumber(receivingLine.getItemNumber());
        response.setVendorNumber(receivingLine.getVendorNumber());
        response.setQuantity(receivingLine.getReceivedQuantity());
        response.setEachCostAmount(receivingLine.getCostAmount());
        response.setEachRetailAmount(receivingLine.getRetailAmount());
        response.setPackQuantity(receivingLine.getQuantity());

        response.setNumberofCasesReceived(receivingLine.getReceivedQuantity());
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

        response.setReceivedWeightQuantity(receivingLine.getReceivedWeightQuantity()== null  ?  null : receivingLine.getReceivedWeightQuantity().toString());
        // TODO default to 99 if not there

        response.setTransactionType(receivingLine.getTransactionType() != null ?
                receivingLine.getTransactionType() : defaultValuesConfigProperties.getTransactionType());
//        response.setControlNumber(receivingLine.getReceivingControlNumber());
        response.setLocationNumber(receivingLine.getStoreNumber());
        response.setDivisionNumber(receivingLine.getBaseDivisionNumber() != null ?
                receivingLine.getBaseDivisionNumber() : defaultValuesConfigProperties.getBaseDivisionNumber());
        response.setBottleDepositFlag(StringUtils.isNotEmpty(receivingLine.getBottleDepositFlag()) ?
                receivingLine.getBottleDepositFlag() : defaultValuesConfigProperties.getBottleDepositFlag());
        if(StringUtils.isNotEmpty(receivingLine.getMerchandises())){
            JsonObject jsonObject = gson.fromJson(receivingLine.getMerchandises(), JsonObject.class);
            response.setMerchandises(new ArrayList<>());
            for (Map.Entry<String, JsonElement> jsonElementEntry : jsonObject.entrySet()) {
                JsonObject innerJsonObject = (JsonObject) jsonElementEntry.getValue();
                ReceiveMDSResponse receiveMDSResponse = new ReceiveMDSResponse(
                        innerJsonObject.get("mdseDisplayCode").getAsInt(),
                        innerJsonObject.get("mdseConditionCode").getAsInt(),
                        innerJsonObject.get("mdseQuantity").getAsInt(),
                        innerJsonObject.get("mdseQuantityUnitOfMeasureCode").getAsString());
                response.getMerchandises().add(receiveMDSResponse);
            }
        }
        return response;
    }
}
