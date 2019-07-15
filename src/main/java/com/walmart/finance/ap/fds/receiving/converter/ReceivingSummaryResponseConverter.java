package com.walmart.finance.ap.fds.receiving.converter;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ReceivingSummaryResponseConverter implements Converter<ReceiveSummary, ReceivingSummaryResponse> {

    @Override
    public ReceivingSummaryResponse convert(ReceiveSummary receiveSummary) {

        ReceivingSummaryResponse response = new ReceivingSummaryResponse();
        response.setPurchaseOrderId(receiveSummary.getReceivingControlNumber());
        response.setReceiptNumber(Integer.valueOf(receiveSummary.getPoReceiveId()));
        response.setTransactionType(receiveSummary.getTransactionType());
        response.setControlNumber(receiveSummary.getReceivingControlNumber());
        response.setLocationNumber(receiveSummary.getStoreNumber());
        response.setDivisionNumber(receiveSummary.getBaseDivisionNumber());
        response.setReceiptDate(receiveSummary.getDateReceived()); // TODO will change once  Receipt_Date is available : changed to MDSReceivedate
        response.setReceiptStatus(receiveSummary.getBusinessStatusCode()); //   TODO will change once  TOTAL_MATCH_IND is available
        response.setVendorNumber(receiveSummary.getVendorNumber());
//        response.setCarrierCode("CRCode");
//        response.setTrailerNumber(0);
//        response.setAssociateName(receiveSummary.getUserId());
//        response.setAuthorizedBy(receiveSummary.getUserId());
//        response.setAuthorizedDate(receiveSummary.getCreationDate());
        //TODO need to add in pipeline code
        response.setTotalCostAmount(receiveSummary.getTotalCostAmount());
        response.setTotalRetailAmount(receiveSummary.getTotalRetailAmount());   //TODO need to add in pipeline code

//        response.setParentReceiptId(Integer.valueOf(receiveSummary.getPoReceiveId()));

//        response.setParentReceiptNumber(receiveSummary.getReceivingControlNumber());

        response.setDepartmentNumber(receiveSummary.getDepartmentNumber());
//        response.setParentPurchaseOrderId(receiveSummary.getReceivingControlNumber());
//        response.setParentTransactionType(receiveSummary.getTransactionType());
//        response.setParentControlNumber(receiveSummary.getReceivingControlNumber());
//        response.setParentLocationNumber(receiveSummary.getStoreNumber());
//        response.setParentDivisionNumber(receiveSummary.getBaseDivisionNumber());
//        response.setMemo("MEMO");
        return response;
    }
}
