package com.walmart.finance.ap.fds.receiving.listener;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.common.ReceivingUtils;
import com.walmart.finance.ap.fds.receiving.messageproducer.Producer;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.SuccessMessage;
import com.walmart.finance.ap.fds.receiving.response.SummaryPayload;
import com.walmart.finance.ap.fds.receiving.service.ReceiveSummaryServiceImpl;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryValidator;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@AllArgsConstructor
public class ReceiveSummaryListener {

    public static final Logger log = LoggerFactory.getLogger(ReceiveSummaryListener.class);
    @Autowired
    private Producer producer;
    @Autowired
    private ReceiveSummaryValidator receiveSummaryValidator;
    @Autowired
    private ReceiveSummaryServiceImpl receiveSummaryServiceImpl;

    @EventListener
    public void onReceiveSummaryCommit(ReceivingSummaryRequest receivingSummaryRequest) {
        log.info("Inside Receive Summary Listener for event: " + receivingSummaryRequest);
        Boolean isWareHouseData = receiveSummaryValidator.isWareHouseData(receivingSummaryRequest.getMeta().getSorRoutingCtx());
        SuccessMessage summaryMessage = new SuccessMessage();
        summaryMessage.setMeta(receivingSummaryRequest.getMeta());
        summaryMessage.setSuccess(ReceivingConstants.TRUE);
        summaryMessage.setObjectName(ReceivingConstants.APPLICATION_TYPE_SUMMARY);
        summaryMessage.setMessageTimeStamp(LocalDateTime.now().atZone(ZoneId.of(ReceivingConstants.ZONE_ID)).toInstant().toEpochMilli());
        summaryMessage.setOperation(ReceivingConstants.OPERATION_TYPE);
        summaryMessage.setDomain(ReceivingConstants.DOMAIN_NAME);
        summaryMessage.set_id(receiveSummaryServiceImpl.formulateId(receivingSummaryRequest.getPurchaseOrderId(), receivingSummaryRequest.getReceiptNumber(), receivingSummaryRequest.getLocationNumber().toString(), isWareHouseData ? "0" : receivingSummaryRequest.getReceiptDate().toString()));
        summaryMessage.setPartitionKey(ReceivingUtils.getPartitionKey(String.valueOf(receivingSummaryRequest.getLocationNumber()),
                receivingSummaryRequest.getReceiptDate(), 1));
        SummaryPayload summaryPayload = new SummaryPayload();
        summaryPayload.setBusinessStatusCode(receivingSummaryRequest.getBusinessStatusCode());
        summaryPayload.setLocationNumber(receivingSummaryRequest.getLocationNumber());
        summaryPayload.setPurchaseOrderId(receivingSummaryRequest.getPurchaseOrderId());
        summaryPayload.setReceiveId(receivingSummaryRequest.getReceiptNumber());
        summaryPayload.setReceiveDate(receivingSummaryRequest.getReceiptDate());
        summaryMessage.setPayload(summaryPayload);
        producer.sendSummaryToEventHub(summaryMessage, ReceivingConstants.RECEIVESUMMARYWAREHOUSE);
    }
}

