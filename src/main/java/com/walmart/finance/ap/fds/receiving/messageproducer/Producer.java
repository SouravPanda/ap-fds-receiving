package com.walmart.finance.ap.fds.receiving.messageproducer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.common.errors.NetworkException;
import org.apache.kafka.common.errors.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


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

    public void sendSummaryToEventHub(ObjectNode writeToTopic, String topic) {
        try {
            log.info("Inside Receive Summary producer ");
            customSource.summaryTopic().send(MessageBuilder.withPayload(writeToTopic).build());
            log.info("Successfully produced Summary record " + writeToTopic + " to event " + topic);

        } catch (NetworkException | TimeoutException ex) {
            log.error("Error Producing summary record " + writeToTopic + " to event :" + ex);
            log.info("calling Audit API to save the summary record to MySQL failure table");
            mySQLApi.saveFailureRecordTOMysql(writeToTopic);
        }
    }

    public void sendSummaryLineToEventHub(ObjectNode writeToTopic, String topic) {
        try {
            log.info("Inside Receive summaryLine producer ");
            customSource.lineSummaryTopic().send(MessageBuilder.withPayload(writeToTopic).build());
            log.info("Successfully produced summaryLine record " + writeToTopic + " to event " + topic);
        } catch (NetworkException | TimeoutException ex) {
            log.error("Error Producing summaryLine record " + writeToTopic + " to event :" + ex);
            log.info("calling Audit API to save the summaryLine record to MySQL failure table");
            mySQLApi.saveFailureRecordTOMysql(writeToTopic);
        }
    }
}

