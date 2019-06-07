package com.walmart.finance.ap.fds.receiving.converter;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReceivingSummaryReqConverter implements Converter<ReceivingSummaryRequest, ReceiveSummary> {

    private static final String separator = "|";

    @Override
    public ReceiveSummary convert(ReceivingSummaryRequest receivingSummaryRequest) {

        String id = receivingSummaryRequest.getControlNumber() + separator + receivingSummaryRequest.getReceiptNumber() + separator + receivingSummaryRequest.getLocationNumber() + separator + receivingSummaryRequest.getReceiptDate();
        ReceiveSummary receiveSummary = new ReceiveSummary();
        receiveSummary.set_id(id);
        receiveSummary.setReceivingControlNumber(receivingSummaryRequest.getControlNumber());
        receiveSummary.setStoreNumber(receivingSummaryRequest.getLocationNumber());
        return receiveSummary;
    }
}
