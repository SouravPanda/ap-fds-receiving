package com.walmart.finance.ap.fds.receiving.converter;

import com.walmart.finance.ap.fds.receiving.config.DefaultValuesConfigProperties;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoLineResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.UOM_CODE_WH_EXCEPTION_RESOLUTION;
import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.UOM_CODE_WH_MATCHING;

@Component
public class ReceivingInfoLineResponseConverter implements Converter<ReceivingLine, ReceivingInfoLineResponse> {

    @Autowired
    DefaultValuesConfigProperties defaultValuesConfigProperties;

    @Override
    public ReceivingInfoLineResponse convert(ReceivingLine receivingLine) {
            ReceivingInfoLineResponse response = new ReceivingInfoLineResponse();
            response.setReceiptNumber(StringUtils.isNotEmpty(receivingLine.getReceiveId()) ?
                    receivingLine.getReceiveId() : "0");
            response.setReceiptLineNumber(receivingLine.getLineSequenceNumber());
            response.setItemNumber(receivingLine.getItemNumber() != null ? receivingLine.getItemNumber() :
                    defaultValuesConfigProperties.getItemNumber());
            response.setQuantity(receivingLine.getReceivedQuantity() != null ?
                    receivingLine.getReceivedQuantity().intValue() : defaultValuesConfigProperties.getReceivedQuantity());

            determinePackDetails(receivingLine, response);

            response.setNumberOfCasesReceived(receivingLine.getReceivedQuantity() != null ?
                receivingLine.getReceivedQuantity() : Double.valueOf(defaultValuesConfigProperties.getReceivedQuantity()));
            response.setBottleDepositFlag(StringUtils.isNotEmpty(receivingLine.getBottleDepositFlag()) ?
                    receivingLine.getBottleDepositFlag() : defaultValuesConfigProperties.getBottleDepositFlag());
            response.setUpc(StringUtils.isNotEmpty(receivingLine.getUpcNumber()) ? receivingLine.getUpcNumber() :
                    defaultValuesConfigProperties.getUpcNumber());
            response.setItemDescription(receivingLine.getItemDescription());
            response.setUnitOfMeasure(receivingLine.getReceivedQuantityUOMCode());
            response.setVariableWeightInd(StringUtils.isNotEmpty(receivingLine.getVariableWeightIndicator()) ?
                    receivingLine.getVariableWeightIndicator() : defaultValuesConfigProperties.getVariableWeightIndicator());
            response.setCostMultiple(receivingLine.getCostMultiple() != null ?
                    receivingLine.getCostMultiple() : defaultValuesConfigProperties.getCostMultiple());
            response.setReceivedWeightQuantity(receivingLine.getReceivedWeightQuantity() == null ?
                    defaultValuesConfigProperties.getReceivedWeightQuantity().toString() :
                    receivingLine.getReceivedWeightQuantity().toString());
            if (receivingLine.getMerchandises() != null) {
                response.setMerchandises(new ArrayList<>(receivingLine.getMerchandises().values()));
            }
            return response;
    }

    private void determinePackDetails(ReceivingLine receivingLine, ReceivingInfoLineResponse response) {
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
    }
}
