package com.walmart.finance.ap.fds.receiving.converter;

import com.walmart.finance.ap.fds.receiving.config.DefaultValuesConfigProperties;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.response.ReceiveMDSResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.response.WHLinePOLineValue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.UOM_CODE_WH_EXCEPTION_RESOLUTION;
import static com.walmart.finance.ap.fds.receiving.common.ReceivingConstants.UOM_CODE_WH_MATCHING;

;


@RunWith(MockitoJUnitRunner.class)
public class ReceivingLineResponseConverterTest {

    @InjectMocks
    ReceivingLineResponseConverter receivingLineResponseConverter;

    @Mock
    DefaultValuesConfigProperties defaultValuesConfigProperties;

    @Test
    public void TestConvert() {
        ReceivingLine receivingLine = new ReceivingLine();

        receivingLine.setBaseDivisionNumber(12345);
        receivingLine.setReceiveId("ReceiveId");
        receivingLine.setLineNumber(12345);
        receivingLine.setItemNumber(Long.valueOf("123456789"));
        receivingLine.setVendorNumber(12345);
        receivingLine.setReceivedQuantity(Double.parseDouble("123"));
        receivingLine.setReceivedQuantity(Double.parseDouble("123"));
        receivingLine.setReceivingControlNumber("12345");
        receivingLine.setPurchaseOrderId(Long.parseLong("123"));
        receivingLine.setVariableWeightIndicator("123");
        receivingLine.setReceivedQuantityUOMCode("123");
        receivingLine.setReceivedWeightQuantity(Double.parseDouble("123"));
        receivingLine.setTransactionType(Integer.parseInt("123"));
        receivingLine.setStoreNumber(Integer.parseInt("123"));
        receivingLine.setBaseDivisionNumber(Integer.parseInt("123"));
        receivingLine.setBottleDepositFlag("false");
        Map<String, ReceiveMDSResponse> merchandise = new HashMap<>();
        receivingLine.setMerchandises(merchandise);

        // PO
        WHLinePOLineValue whLinePOLineValue = new WHLinePOLineValue();
        whLinePOLineValue.setCostAmount(123d);
        whLinePOLineValue.setQuantity(12);
        whLinePOLineValue.setRetailAmount(626d);
        whLinePOLineValue.setUomCode("CM");
        Map<String, WHLinePOLineValue> poLineValue = new HashMap<>();
        receivingLine.setPoLineValue(poLineValue);

        poLineValue.put(UOM_CODE_WH_EXCEPTION_RESOLUTION,whLinePOLineValue);
        poLineValue.put(UOM_CODE_WH_MATCHING,whLinePOLineValue);

        ReceivingLineResponse response = receivingLineResponseConverter.convert(receivingLine);
        Assert.assertNotNull(response);

    }

    @Test
    public void TestConvertWithoutPO() {
        ReceivingLine receivingLine = new ReceivingLine();

        receivingLine.setBaseDivisionNumber(12345);
        receivingLine.setReceiveId("ReceiveId");
        receivingLine.setLineNumber(12345);
        receivingLine.setItemNumber(Long.valueOf("123456789"));
        receivingLine.setVendorNumber(12345);
        receivingLine.setReceivedQuantity(Double.parseDouble("123"));
        receivingLine.setReceivedQuantity(Double.parseDouble("123"));
        receivingLine.setReceivingControlNumber("12345");
        receivingLine.setPurchaseOrderId(Long.parseLong("123"));
        receivingLine.setVariableWeightIndicator("123");
        receivingLine.setReceivedQuantityUOMCode("123");
        receivingLine.setReceivedWeightQuantity(Double.parseDouble("123"));
        receivingLine.setTransactionType(Integer.parseInt("123"));
        receivingLine.setStoreNumber(Integer.parseInt("123"));
        receivingLine.setBaseDivisionNumber(Integer.parseInt("123"));
        receivingLine.setBottleDepositFlag("false");
        Map<String, ReceiveMDSResponse> merchandise = new HashMap<>();
        receivingLine.setMerchandises(merchandise);
        receivingLine.setCostAmount(123456d);
        receivingLine.setRetailAmount(12348d);
        receivingLine.setQuantity(12);

        Mockito.when(defaultValuesConfigProperties.getTotalCostAmount()).thenReturn(1234d);
        Mockito.when(defaultValuesConfigProperties.getTotalRetailAmount()).thenReturn(12345d);
        Mockito.when(defaultValuesConfigProperties.getQuantity()).thenReturn(12);


        ReceivingLineResponse response = receivingLineResponseConverter.convert(receivingLine);
        Assert.assertNotNull(response);

    }

    @Test
    public void TestConvertWithoutPOANdDefault() {
        ReceivingLine receivingLine = new ReceivingLine();

        receivingLine.setBaseDivisionNumber(12345);
        receivingLine.setReceiveId("ReceiveId");
        receivingLine.setLineNumber(12345);
        receivingLine.setItemNumber(Long.valueOf("123456789"));
        receivingLine.setVendorNumber(12345);
        receivingLine.setReceivedQuantity(Double.parseDouble("123"));
        receivingLine.setReceivedQuantity(Double.parseDouble("123"));
        receivingLine.setReceivingControlNumber("12345");
        receivingLine.setPurchaseOrderId(Long.parseLong("123"));
        receivingLine.setVariableWeightIndicator("123");
        receivingLine.setReceivedQuantityUOMCode("123");
        receivingLine.setReceivedWeightQuantity(Double.parseDouble("123"));
        receivingLine.setTransactionType(Integer.parseInt("123"));
        receivingLine.setStoreNumber(Integer.parseInt("123"));
        receivingLine.setBaseDivisionNumber(Integer.parseInt("123"));
        receivingLine.setBottleDepositFlag("false");
        Map<String, ReceiveMDSResponse> merchandise = new HashMap<>();
        receivingLine.setMerchandises(merchandise);
        receivingLine.setCostAmount(123456d);
        receivingLine.setRetailAmount(12348d);
        receivingLine.setQuantity(12);


        WHLinePOLineValue whLinePOLineValue = new WHLinePOLineValue();
        whLinePOLineValue.setCostAmount(null);
        whLinePOLineValue.setQuantity(null);
        whLinePOLineValue.setRetailAmount(null);
        whLinePOLineValue.setUomCode(null);
        Map<String, WHLinePOLineValue> poLineValue = new HashMap<>();
        poLineValue.put(UOM_CODE_WH_EXCEPTION_RESOLUTION,whLinePOLineValue);
        poLineValue.put(UOM_CODE_WH_MATCHING,whLinePOLineValue);
        receivingLine.setPoLineValue(poLineValue);

        Mockito.when(defaultValuesConfigProperties.getTotalCostAmount()).thenReturn(1234d);
        Mockito.when(defaultValuesConfigProperties.getTotalRetailAmount()).thenReturn(12345d);
        Mockito.when(defaultValuesConfigProperties.getQuantity()).thenReturn(12);


        ReceivingLineResponse response = receivingLineResponseConverter.convert(receivingLine);
        Assert.assertNotNull(response);

    }

    @Test
    public void TestConvertWithOjutWeightQuantity() {
        ReceivingLine receivingLine = new ReceivingLine();

        receivingLine.setBaseDivisionNumber(12345);
        receivingLine.setReceiveId("ReceiveId");
        receivingLine.setLineNumber(12345);
        receivingLine.setItemNumber(Long.valueOf("123456789"));
        receivingLine.setVendorNumber(12345);
        receivingLine.setReceivedQuantity(Double.parseDouble("123"));
        receivingLine.setReceivedQuantity(Double.parseDouble("123"));
        receivingLine.setReceivingControlNumber("12345");
        receivingLine.setPurchaseOrderId(Long.parseLong("123"));
        receivingLine.setVariableWeightIndicator("123");
        receivingLine.setReceivedQuantityUOMCode("123");
        // receivingLine.setReceivedWeightQuantity(Double.parseDouble("123"));
        receivingLine.setTransactionType(Integer.parseInt("123"));
        receivingLine.setStoreNumber(Integer.parseInt("123"));
        receivingLine.setBaseDivisionNumber(Integer.parseInt("123"));
        receivingLine.setBottleDepositFlag("false");
        Map<String, ReceiveMDSResponse> merchandise = new HashMap<>();
        receivingLine.setMerchandises(merchandise);
        receivingLine.setCostAmount(123456d);
        receivingLine.setRetailAmount(12348d);
        receivingLine.setQuantity(12);


        WHLinePOLineValue whLinePOLineValue = new WHLinePOLineValue();
        whLinePOLineValue.setCostAmount(null);
        whLinePOLineValue.setQuantity(null);
        whLinePOLineValue.setRetailAmount(null);
        whLinePOLineValue.setUomCode(null);
        Map<String, WHLinePOLineValue> poLineValue = new HashMap<>();
        poLineValue.put(UOM_CODE_WH_EXCEPTION_RESOLUTION,whLinePOLineValue);
        poLineValue.put(UOM_CODE_WH_MATCHING,whLinePOLineValue);
        receivingLine.setPoLineValue(poLineValue);

        Mockito.when(defaultValuesConfigProperties.getTotalCostAmount()).thenReturn(1234d);
        Mockito.when(defaultValuesConfigProperties.getTotalRetailAmount()).thenReturn(12345d);
        Mockito.when(defaultValuesConfigProperties.getQuantity()).thenReturn(12);
        Mockito.when(defaultValuesConfigProperties.getReceivedWeightQuantity()).thenReturn(12d);


        ReceivingLineResponse response = receivingLineResponseConverter.convert(receivingLine);
        Assert.assertNotNull(response);

    }
}