package com.walmart.finance.ap.fds.receiving.converter;

import com.walmart.finance.ap.fds.receiving.config.DefaultValuesConfigProperties;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class ReceivingSummaryResponseConverter implements Converter<ReceiveSummary, ReceivingSummaryResponse> {

    @Autowired
    DefaultValuesConfigProperties defaultValuesConfigProperties;

    @Override
    public ReceivingSummaryResponse convert(ReceiveSummary receiveSummary) {

        ReceivingSummaryResponse response = new ReceivingSummaryResponse();
        response.setPurchaseOrderId(receiveSummary.getPurchaseOrderId() == null ? "0" : receiveSummary.getPurchaseOrderId().toString());
        response.setReceiptNumber(StringUtils.isNotEmpty(receiveSummary.getReceiveId()) ? Long.valueOf(receiveSummary.getReceiveId()) : 0);
        response.setTransactionType(receiveSummary.getTransactionType());
        response.setControlNumber(receiveSummary.getReceivingControlNumber() != null ?
                receiveSummary.getReceivingControlNumber() : defaultValuesConfigProperties.getReceivingControlNumber());
        response.setLocationNumber(receiveSummary.getStoreNumber());
        response.setDivisionNumber(receiveSummary.getBaseDivisionNumber() != null ?
                receiveSummary.getBaseDivisionNumber() : defaultValuesConfigProperties.getBaseDivisionNumber());
        response.setReceiptDate(receiveSummary.getDateReceived().atZone(ZoneId.of("GMT")).toLocalDate()); // TODO will change once  Receipt_Date is
        // available : changed
        // to MDSReceivedate
        response.setReceiptStatus(receiveSummary.getBusinessStatusCode()); //   TODO will change once  TOTAL_MATCH_IND is available
        response.setVendorNumber(receiveSummary.getVendorNumber());
//        response.setCarrierCode("CRCode");
//        response.setTrailerNumber(0);
//        response.setAssociateName(receiveSummary.getUserId());
//        response.setAuthorizedBy(receiveSummary.getUserId());
//        response.setAuthorizedDate(receiveSummary.getCreationDate());
        //TODO need to add in pipeline code
        response.setTotalCostAmount(receiveSummary.getTotalCostAmount() != null ?
                receiveSummary.getTotalCostAmount() : defaultValuesConfigProperties.getTotalCostAmount());
        response.setTotalRetailAmount(receiveSummary.getTotalRetailAmount() != null ?
                receiveSummary.getTotalRetailAmount() : defaultValuesConfigProperties.getTotalRetailAmount());   //TODO need to add in pipeline code

//        response.setParentReceiptId(Integer.valueOf(receiveSummary.getReceiveId()));

//        response.setParentReceiptNumber(receiveSummary.getReceivingControlNumber());

        response.setDepartmentNumber(StringUtils.isNotEmpty(receiveSummary.getDepartmentNumber()) ? Integer.valueOf(receiveSummary.getDepartmentNumber()) : 0 );
//        response.setParentPurchaseOrderId(receiveSummary.getReceivingControlNumber());
//        response.setParentTransactionType(receiveSummary.getTransactionType());
//        response.setParentControlNumber(receiveSummary.getReceivingControlNumber());
//        response.setParentLocationNumber(receiveSummary.getStoreNumber());
//        response.setParentDivisionNumber(receiveSummary.getBaseDivisionNumber());
//        response.setMemo("MEMO");
        response.setControlSequenceNumber(receiveSummary.getControlSequenceNumber()!= null ?
                receiveSummary.getControlSequenceNumber() : defaultValuesConfigProperties.getControlSequenceNumber());
        response.setBottleDepositAmount(receiveSummary.getBottleDepositAmount() != null ?
                receiveSummary.getBottleDepositAmount() : defaultValuesConfigProperties.getBottleDepositAmount());
        return response;
    }
}
