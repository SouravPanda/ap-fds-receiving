package com.walmart.finance.ap.fds.receiving.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.listener.RetryListenerSupport;
import java.util.Collections;
import java.util.List;
@Configuration
@EnableRetry
public class RetryConfiguration {
    @Bean
    public List<RetryListener> retryListeners() {
        Logger logger = LogManager.getLogger(getClass());
        return Collections.singletonList(new RetryListenerSupport() {
            @Override
            public <T, E extends Throwable> void onError(
                    RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
                logger.warn("Retryable method {} threw {}th, attempted retries = {} with exception {}",
                        context.getAttribute("context.name"),
                        context.getRetryCount(), context.getRetryCount() - 1, throwable.toString());
            }
        });
    }
}