package com.walmart.finance.ap.fds.receiving.converter;

import com.google.gson.Gson;
import com.walmart.finance.ap.fds.receiving.config.DefaultValuesConfigProperties;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.UOM_CODE_WH_EXCEPTION_RESOLUTION;
import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.UOM_CODE_WH_MATCHING;

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
        response.setReceiptNumber(receivingLine.getReceiveId());
        response.setReceiptLineNumber(receivingLine.getLineNumber() != null ?
                receivingLine.getLineNumber() : defaultValuesConfigProperties.getLineNumber());
        response.setItemNumber(receivingLine.getItemNumber() != null ?
                receivingLine.getItemNumber() : defaultValuesConfigProperties.getItemNumber());
        response.setVendorNumber(receivingLine.getVendorNumber());
        response.setQuantity(receivingLine.getReceivedQuantity() != null ?
                receivingLine.getReceivedQuantity().intValue() : defaultValuesConfigProperties.getReceivedQuantity());

        if (receivingLine.getPoLineValue() == null) {
            response.setEachCostAmount(receivingLine.getCostAmount() != null ?
                    receivingLine.getCostAmount() : defaultValuesConfigProperties.getTotalCostAmount());
            response.setEachRetailAmount(receivingLine.getRetailAmount() != null ?
                    receivingLine.getRetailAmount() : defaultValuesConfigProperties.getTotalRetailAmount());
            response.setPackQuantity(receivingLine.getQuantity() != null ?
                    receivingLine.getQuantity() : defaultValuesConfigProperties.getQuantity());


            response.setEachVendorCostAmount(defaultValuesConfigProperties.getTotalCostAmount());
            response.setEachVendorRetailAmount(defaultValuesConfigProperties.getTotalRetailAmount());
            response.setVendorPackQuantity(defaultValuesConfigProperties.getQuantity());
        } else {
            response.setEachCostAmount(receivingLine.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION) != null
                    && receivingLine.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION).getCostAmount() != null?
                    receivingLine.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION).getCostAmount() :
                    defaultValuesConfigProperties.getTotalCostAmount());
            response.setEachRetailAmount(receivingLine.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION) != null
                    && receivingLine.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION).getRetailAmount() != null?
                    receivingLine.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION).getRetailAmount() :
                    defaultValuesConfigProperties.getTotalRetailAmount());
            response.setPackQuantity(receivingLine.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION) != null
                    && receivingLine.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION).getQuantity() != null?
                    receivingLine.getPoLineValue().get(UOM_CODE_WH_EXCEPTION_RESOLUTION).getQuantity() :
                    defaultValuesConfigProperties.getQuantity());

            response.setEachVendorCostAmount(receivingLine.getPoLineValue().get(UOM_CODE_WH_MATCHING) != null
                    && receivingLine.getPoLineValue().get(UOM_CODE_WH_MATCHING).getCostAmount() != null?
                    receivingLine.getPoLineValue().get(UOM_CODE_WH_MATCHING).getCostAmount() :
                    defaultValuesConfigProperties.getTotalCostAmount());
            response.setEachVendorRetailAmount(receivingLine.getPoLineValue().get(UOM_CODE_WH_MATCHING) != null
                    && receivingLine.getPoLineValue().get(UOM_CODE_WH_MATCHING).getRetailAmount() != null?
                    receivingLine.getPoLineValue().get(UOM_CODE_WH_MATCHING).getRetailAmount() :
                    defaultValuesConfigProperties.getTotalRetailAmount());
            response.setVendorPackQuantity(receivingLine.getPoLineValue().get(UOM_CODE_WH_MATCHING) != null
                    && receivingLine.getPoLineValue().get(UOM_CODE_WH_MATCHING).getQuantity() != null?
                    receivingLine.getPoLineValue().get(UOM_CODE_WH_MATCHING).getQuantity() :
                    defaultValuesConfigProperties.getQuantity());
        }

        response.setNumberOfCasesReceived(receivingLine.getReceivedQuantity() != null ?
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
       response.setUnitOfMeasure(receivingLine.getReceivedQuantityUOMCode());

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
