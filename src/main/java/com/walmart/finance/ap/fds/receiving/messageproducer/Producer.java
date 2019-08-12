package com.walmart.finance.ap.fds.receiving.messageproducer;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Produces the record to destination topic.
 * The Call to producer from consumer is Async.
 * <p>
 * The produces produces record with a sync call to destination event hub.
 * In case of error its logged with the contents.
 */
@Component
@EnableAsync
@AllArgsConstructor
public class Producer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public static final Logger log = LoggerFactory.getLogger(Producer.class);
    //TODO The call to Kafka had to be made Async in future.

    /**
     * @param writeToTopic data to write into topic
     * @param topic
     */
    @Async
    public void sendToEventHub(String writeToTopic, String topic) {
        try {
            SendResult<String, String> result = this.kafkaTemplate.send(topic, writeToTopic).get(10, TimeUnit.SECONDS);
            log.info("Successfully produced" + writeToTopic + "RESULT DETAILS: DESTINATION EVENT HUB TOPIC" + result.getRecordMetadata().topic() + "DESTINATION EVENT HUB OFFSET" + result.getRecordMetadata().offset() + "RESULT PARTITION" + result.getRecordMetadata().partition());
        } catch (Exception e) {
            log.error("Error Processing the details from producer side :" + e.getMessage() + e.fillInStackTrace() + e.getStackTrace() + "Value" + writeToTopic);
        }
    }
}
