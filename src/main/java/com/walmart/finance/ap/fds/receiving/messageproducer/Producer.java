package com.walmart.finance.ap.fds.receiving.messageproducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * Produces the record to destination topic.
 * The Call to producer from consumer is Async.
 * <p>
 * The produces produces record with a sync call to destination event hub.
 * In case of error its logged with the contents.
 */
@EnableBinding(CustomSource.class)
@Component
public class Producer {

    public static final Logger log = LoggerFactory.getLogger(Producer.class);
    //TODO The call to Kafka had to be made Async in future.

    /**
     * @param writeToTopic data to write into topic
     * @param topic
     */

    @Autowired
    private CustomSource customSource;


    public void sendSummaryToEventHub(String writeToTopic, String topic) {
        try {
            log.info("Inside Receive Summary producer ");
            customSource.summaryTopic().send(MessageBuilder.withPayload(writeToTopic).build());
            log.info("Successfully produced Summary record " + writeToTopic + " to event " + topic);

        } catch (Exception ex) {
            log.error("Error Producing summary record " + writeToTopic + " to event :" + ex);

        }
    }

    public void sendSummaryLineToEventHub(String writeToTopic, String topic) {
        try {
            log.info("Inside Receive summaryLine producer ");
            customSource.lineSummaryTopic().send(MessageBuilder.withPayload(writeToTopic).build());
            log.info("Successfully produced summaryLine record " + writeToTopic + " to event "+ topic);
        } catch (Exception ex) {
            log.error("Error Producing summaryLine record " + writeToTopic + " to event :"  + ex);

        }
    }
}
