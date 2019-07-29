package com.walmart.finance.ap.fds.receiving.messageproducer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.List;
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
    public class Producer {

        private final KafkaTemplate<String, String> kafkaTemplate;

        public static final Logger log = LoggerFactory.getLogger(Producer.class);


        Producer(KafkaTemplate<String, String> kafkaTemplate) {
            this.kafkaTemplate = kafkaTemplate;
        }

   /*
     TODO The call to Kafka had to be made Async in future.
    */

        /**
         *
         * @Param recvSummary
         * @Param topic To produce to receiveSummary
         */
        @Async
        public void sendReceiveSummary(ReceiveSummary recvSummary, String topic) {


            try {
                ObjectMapper obj= new ObjectMapper();
                String writeToTopic=obj.writeValueAsString(recvSummary);
                SendResult<String, String> result = this.kafkaTemplate.send(topic, writeToTopic).get(10, TimeUnit.SECONDS);
                log.info("Successfully produced" + writeToTopic + "RESULT DETAILS: DESTINATION EVENT HUB TOPIC" + result.getRecordMetadata().topic() + "DESTINATION EVENT HUB OFFSET" + result.getRecordMetadata().offset() + "RESULT PARTITION" + result.getRecordMetadata().partition());
            } catch (Exception e) {
                log.error("Error Processing the details from producer side :" + e.getMessage() + e.fillInStackTrace() + e.getStackTrace() + "Value" + recvSummary.toString());
            }


        }


        /**
         *
         * @Param recvLine
         * @Param topic To produce to receiveLine
         */

        @Async
        public void sendReceiveLine(List recvLineSummary, String topic) {


            try {
                ObjectMapper obj= new ObjectMapper();
                String writeToTopic=obj.writeValueAsString(recvLineSummary);
                SendResult<String, String> result = this.kafkaTemplate.send(topic, writeToTopic).get(10, TimeUnit.SECONDS);
                log.info("Successfully produced" + writeToTopic + "RESULT DETAILS: DESTINATION EVENT HUB TOPIC" + result.getRecordMetadata().topic() + "DESTINATION EVENT HUB OFFSET" + result.getRecordMetadata().offset() + "RESULT PARTITION" + result.getRecordMetadata().partition());
            } catch (Exception e) {
                log.error("Error Processing the details from producer side :" + e.getMessage() + e.fillInStackTrace() + e.getStackTrace() + "Value" + recvLineSummary.toString());
            }

        }

    }

