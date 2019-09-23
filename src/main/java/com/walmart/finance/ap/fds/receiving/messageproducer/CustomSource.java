package com.walmart.finance.ap.fds.receiving.messageproducer;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;

public interface CustomSource extends Source {

    String SUMMARY_TOPIC = "summaryTopic";

    @Output(CustomSource.SUMMARY_TOPIC)
    MessageChannel summaryTopic();

    String LINE_SUMMARY_TOPIC = "lineSummaryTopic";

    @Output(CustomSource.LINE_SUMMARY_TOPIC)
    MessageChannel lineSummaryTopic();


}