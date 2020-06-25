package com.walmart.finance.ap.fds.receiving.messageproducer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.walmart.finance.ap.fds.receiving.response.SuccessMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;


@EnableBinding(CustomSource.class)
@Component
public class Producer {

    public static final Logger log = LoggerFactory.getLogger(Producer.class);

    /**
     * @param writeToTopic data to write into topic
     * @param topic
     */

    @Autowired
    private CustomSource customSource;

    @Autowired
    private MySQLApi mySQLApi;


    public void sendSummaryToEventHub(SuccessMessage summaryMessage, String topic) {

        log.info("Inside Receive Summary producer ");
        boolean sent = false;
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            String value = mapper.writeValueAsString(summaryMessage);
            ObjectNode valueTree = (ObjectNode) mapper.readTree(value);
            sent = customSource.summaryTopic().send(MessageBuilder.withPayload(valueTree).setHeader(KafkaHeaders.MESSAGE_KEY, summaryMessage.get_id().getBytes()).build());
            log.info("Successfully produced Summary record " + valueTree + " to event " + topic);
        } catch (IOException ex) {
            log.error("Error mapping the summary record to objectnode " + summaryMessage + "  " + ex);
        } catch (Exception ex) {
            log.error("Error Producing summary record " + summaryMessage + " to event :" + ex);
            log.info("calling Audit API to save the summary record to MySQL failure table");
            sent=true;
            mySQLApi.saveFailureRecordTOMysql(summaryMessage);
        }
        if (!sent) {
            log.error("Error Producing summary record " + summaryMessage);
            log.info("calling Audit API to save the summary record to MySQL failure table");
            mySQLApi.saveFailureRecordTOMysql(summaryMessage);
        }
    }

    public void sendSummaryLineToEventHub(SuccessMessage summaryLineMessage, String topic) {
        log.info("Inside Receive summaryLine producer ");
        boolean sent = false;
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            String value = mapper.writeValueAsString(summaryLineMessage);
            ObjectNode valueTree = (ObjectNode) mapper.readTree(value);
            sent = customSource.lineSummaryTopic().send(MessageBuilder.withPayload(summaryLineMessage).setHeader(KafkaHeaders.MESSAGE_KEY, summaryLineMessage.get_id().getBytes()).build());
            log.info("Successfully produced summaryLine record " + valueTree + " to event " + topic);
        } catch (IOException ex) {
            log.error("Error mapping the summaryLine record to objectnode " + summaryLineMessage + "  " + ex);
        } catch (Exception ex) {
            log.error("Error Producing summaryLine record " + summaryLineMessage + " to event :" + ex);
            log.info("calling Audit API to save the summaryLine record to MySQL failure table");
            sent=true;
            mySQLApi.saveFailureRecordTOMysql(summaryLineMessage);
        }
        if (!sent) {
            log.error("Error Producing summaryLine record " + summaryLineMessage);
            log.info("calling Audit API to save the summaryLine record to MySQL failure table");
            mySQLApi.saveFailureRecordTOMysql(summaryLineMessage);
        }

    }
}

