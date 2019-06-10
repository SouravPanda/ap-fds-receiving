package com.walmart.finance.ap.fds.receiving.converter;

import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ReceivingLineReqConverter implements Converter<ReceivingSummaryLineRequest, ReceivingLine> {

    private static final String separator = "|";

    @Override
    public ReceivingLine convert(ReceivingSummaryLineRequest receivingLineRequest) {

        String id = receivingLineRequest.getControlNumber() + separator + receivingLineRequest.getReceiptNumber() + separator + receivingLineRequest.getLocationNumber() + separator+ receivingLineRequest.getReceiptDate() + separator + receivingLineRequest.getSequenceNumber();
        ReceivingLine receivingLine = new ReceivingLine();
        return receivingLine;
    }
}
