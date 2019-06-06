package com.walmart.finance.ap.fds.receiving.listener;

import com.walmart.finance.ap.fds.receiving.KafkaProducer.Producer;
import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ReceiveLineListener {

    @Autowired
    private Producer producer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReceiveLineCommit(ReceivingLine event)
    {

        producer.sendReceiveLine(event, ReceivingConstants.RECEIVELINEWAREHOUSE);
    }

}
