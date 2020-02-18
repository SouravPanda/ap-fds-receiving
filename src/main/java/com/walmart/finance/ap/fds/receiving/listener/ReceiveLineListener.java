package com.walmart.finance.ap.fds.receiving.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.messageproducer.Producer;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import org.apache.kafka.common.errors.NetworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;


@Component
public class ReceiveLineListener {

    @Autowired
    private Producer producer;

    public static final Logger log = LoggerFactory.getLogger(ReceiveLineListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReceiveLineCommit(ReceivingSummaryLineRequest event) {
        try {
            log.info("Inside ReceiveSummaryLine Listener for event " + event);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            String value = mapper.writeValueAsString(event);
            ObjectNode valueTree;
            ObjectNode objNode = mapper.createObjectNode();
            valueTree = (ObjectNode) mapper.readTree(value);
            ObjectNode meta = (ObjectNode) valueTree.get(ReceivingConstants.META);
            valueTree.remove(ReceivingConstants.META);
            objNode.set(ReceivingConstants.META, meta);
            objNode.put(ReceivingConstants.SUCCESS, ReceivingConstants.TRUE);
            objNode.put(ReceivingConstants.OBJECT_NAME, ReceivingConstants.APPLICATION_TYPE_LINE_SUMMARY);
            objNode.put(ReceivingConstants.TIMESTAMP, OffsetDateTime.now(ZoneOffset.UTC).toString());
            objNode.put(ReceivingConstants.OPERATION, ReceivingConstants.OPERATION_TYPE);
            objNode.set(ReceivingConstants.PAYLOAD, valueTree);
            producer.sendSummaryLineToEventHub(objNode, ReceivingConstants.RECEIVELINEWAREHOUSE);
        } catch (IOException exception) {
            log.error("Exception while converting the consumer record to JsonNode in DBSyncErrorHandling " + exception.toString());
        } catch (NetworkException exception) {
            log.error("Exception while writing message to event hub topic " + exception.toString());
        } catch(Exception exception){
            log.error("Unknown exception has occured" + exception);
        }
    }
}
