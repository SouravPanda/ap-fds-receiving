package com.walmart.finance.ap.fds.receiving;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication
public class SpringReceiveApplication {

    public static void main(String[] args) {

        SpringApplication.run(SpringReceiveApplication.class, args);
    }

}
