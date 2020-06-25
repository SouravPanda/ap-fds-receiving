package com.walmart.finance.ap.fds.receiving;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@ImportResource("classpath:perf-monitoring.xml")
@SpringBootApplication
@ComponentScan({"com.walmart.finance.audit.rest.*","com.walmart.finance.ap.fds.receiving.*"})
public class SpringReceiveApplication {

    public static void main(String[] args) {

        SpringApplication.run(SpringReceiveApplication.class, args);
    }

}
