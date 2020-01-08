package com.walmart.finance.ap.fds.receiving.listener;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.messageproducer.Producer;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class ReceiveSummaryListener {

    @Autowired
    private Producer producer;

    public static final Logger log = LoggerFactory.getLogger(ReceiveSummaryListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReceiveSummaryCommit(ReceivingSummaryRequest event) {
        log.info("Inside ReceiveSummary Listener for event " + event);
        producer.sendSummaryToEventHub(event.toString(), ReceivingConstants.RECEIVESUMMARYWAREHOUSE);
    }
}
