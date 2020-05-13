package com.walmart.finance.ap.fds.receiving.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.messageproducer.Producer;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
@AllArgsConstructor
public class ReceiveSummaryListener {

    @Autowired
    private Producer producer;

    public static final Logger log = LoggerFactory.getLogger(ReceiveSummaryListener.class);

    @EventListener
    public void onReceiveSummaryCommit(ReceivingSummaryRequest event) {
        log.info("Inside Receive Summary Listener for event: " + event);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ObjectNode valueTree;
        ObjectNode objNode = mapper.createObjectNode();
        try {
            String value = mapper.writeValueAsString(event);
            valueTree = (ObjectNode) mapper.readTree(value);
            ObjectNode meta = (ObjectNode) valueTree.get(ReceivingConstants.META);
            valueTree.remove(ReceivingConstants.META);
            objNode.set(ReceivingConstants.META, meta);
            objNode.put(ReceivingConstants.SUCCESS, ReceivingConstants.TRUE);
            objNode.put(ReceivingConstants.OBJECT_NAME, ReceivingConstants.APPLICATION_TYPE_SUMMARY);
            objNode.put(ReceivingConstants.TIMESTAMP, OffsetDateTime.now(ZoneOffset.UTC).toString());
            objNode.put(ReceivingConstants.OPERATION, ReceivingConstants.OPERATION_TYPE);
            objNode.set(ReceivingConstants.ID, valueTree.get(ReceivingConstants.ID));
            objNode.set(ReceivingConstants.PARTITIONKEY, valueTree.get(ReceivingConstants.PARTITIONKEY));
            valueTree.remove(ReceivingConstants.PARTITIONKEY);
            valueTree.remove(ReceivingConstants.ID);
            objNode.set(ReceivingConstants.PAYLOAD, valueTree);
        } catch (IOException ex) {
            log.error("Exception while forming the JsonNode in Receive Summary Listener " + ex);
        }
        producer.sendSummaryToEventHub(objNode, ReceivingConstants.RECEIVESUMMARYWAREHOUSE);
    }
}

