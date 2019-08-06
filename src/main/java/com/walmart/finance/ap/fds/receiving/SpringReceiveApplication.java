package com.walmart.finance.ap.fds.receiving;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@ImportResource("classpath:perf-monitoring.xml")
@SpringBootApplication
@ImportResource("classpath:perf-monitoring.xml")
public class SpringReceiveApplication {

    public static void main(String[] args) {

        SpringApplication.run(SpringReceiveApplication.class, args);
    }

}
