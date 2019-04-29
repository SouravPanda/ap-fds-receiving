package com.walmart.finance.ap.fds.receiving.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
@Profile(value = {"local", "dev", "qa"})
@EnableSwagger2
public class SwaggerConfig {

    private static final ApiInfo DEFAULT_API_INFO =  new ApiInfo(
            "AP FDS Receive","AP FDS Receive","1.0","urn:tos",
            "AP FDS Receive","Walmart Labs","http://"
    );

    public static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES =  new HashSet<String>(Arrays.asList("appplication/json"));

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(DEFAULT_API_INFO).produces(DEFAULT_PRODUCES_AND_CONSUMES).consumes(DEFAULT_PRODUCES_AND_CONSUMES);
    }
}