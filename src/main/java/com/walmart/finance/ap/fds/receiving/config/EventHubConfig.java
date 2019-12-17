package com.walmart.finance.ap.fds.receiving.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventHubConfig {

    @Value("${ap-fds-receiving-eh-jass-config-${spring.profiles.active}}")
    private String connectionString;

    public static final Logger log = LoggerFactory.getLogger(EventHubConfig.class);

    @Bean
    public void configureBean() {

        log.info("Setting Eventhub Configuration Start");
        System.setProperty("spring.cloud.stream.kafka.binder.configuration.sasl.jaas.config", connectionString);
        log.info("Setting Eventhub Configuration end");
    }
}
