package com.walmart.finance.ap.fds.receiving.listener;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.messageproducer.Producer;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
public class ReceiveLineListener {

    @Autowired
    private Producer producer;

    public static final Logger log = LoggerFactory.getLogger(ReceiveLineListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReceiveLineCommit(ReceivingSummaryLineRequest event) {
        log.info("Inside ReceiveSummaryLine Listener for event " + event);
        producer.sendSummaryLineToEventHub(event.toString(), ReceivingConstants.RECEIVELINEWAREHOUSE);
    }
}
