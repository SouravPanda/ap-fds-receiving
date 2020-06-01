package com.walmart.finance.ap.fds.receiving.listener;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.common.ReceivingUtils;
import com.walmart.finance.ap.fds.receiving.messageproducer.Producer;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.response.SuccessMessage;
import com.walmart.finance.ap.fds.receiving.response.SummaryLinePayload;
import com.walmart.finance.ap.fds.receiving.service.ReceiveSummaryServiceImpl;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;


@Component
public class ReceiveLineListener {

    @Autowired
    private Producer producer;

    @Autowired
    private ReceiveSummaryValidator receiveSummaryValidator;

    @Autowired
    private ReceiveSummaryServiceImpl receiveSummaryServiceImpl;

    public static final Logger log = LoggerFactory.getLogger(ReceiveLineListener.class);

    @EventListener
    public void onReceiveLineCommit(ReceivingSummaryLineRequest receivingSummaryLineRequest) {

        log.info("Inside ReceiveSummaryLine Listener for event " + receivingSummaryLineRequest);
        Boolean isWareHouseData = receiveSummaryValidator.isWareHouseData(receivingSummaryLineRequest.getMeta().getSorRoutingCtx());
        SuccessMessage summaryLineMessage = new SuccessMessage();
        summaryLineMessage.setMeta(receivingSummaryLineRequest.getMeta());
        summaryLineMessage.setSuccess(ReceivingConstants.TRUE);
        summaryLineMessage.setObjectName(ReceivingConstants.APPLICATION_TYPE_LINE_SUMMARY);
        summaryLineMessage.setMessageTimeStamp(LocalDateTime.now().atZone(ZoneId.of(ReceivingConstants.ZONE_ID)).toInstant().toEpochMilli());
        summaryLineMessage.setOperation(ReceivingConstants.OPERATION_TYPE);
        summaryLineMessage.setDomain(ReceivingConstants.DOMAIN_NAME);
        summaryLineMessage.set_id(receiveSummaryServiceImpl.formulateId(receivingSummaryLineRequest.getPurchaseOrderId(), receivingSummaryLineRequest.getReceiptNumber(), receivingSummaryLineRequest.getLocationNumber().toString(), isWareHouseData ? "0" : receivingSummaryLineRequest.getReceiptDate().toString()));
        summaryLineMessage.setPartitionKey(ReceivingUtils.getPartitionKey(String.valueOf(receivingSummaryLineRequest.getLocationNumber()),
                receivingSummaryLineRequest.getReceiptDate(), 1));
        SummaryLinePayload summaryLinePayload = new SummaryLinePayload();
        summaryLinePayload.setBusinessStatusCode(receivingSummaryLineRequest.getBusinessStatusCode());
        summaryLinePayload.setLocationNumber(receivingSummaryLineRequest.getLocationNumber());
        summaryLinePayload.setPurchaseOrderId(receivingSummaryLineRequest.getPurchaseOrderId());
        summaryLinePayload.setReceiveId(receivingSummaryLineRequest.getReceiptNumber());
        summaryLinePayload.setReceiveDate(receivingSummaryLineRequest.getReceiptDate());
        summaryLinePayload.setInventoryMatchStatus(receivingSummaryLineRequest.getInventoryMatchStatus());
        summaryLinePayload.setLineSequenceNumber(receivingSummaryLineRequest.getReceiptLineNumber());
        summaryLineMessage.setPayload(summaryLinePayload);
        producer.sendSummaryLineToEventHub(summaryLineMessage, ReceivingConstants.RECEIVELINEWAREHOUSE);
    }
}
