package com.walmart.finance.ap.fds.receiving.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
//@Profile(value = {"local", "dev", "qa"})
@EnableSwagger2
public class SwaggerConfig  {

    public static final Contact DEFAULT_CONTACT = new Contact("AP FDS Receiving", "",
            "Vedprakash.Pandey@walmartlabs.com");

    public static final ApiInfo DEFAULT_API_INFO = new ApiInfoBuilder()
            .title("AP FDS Receiving")
            .description(
                    "AP FDS Receiving services")
            .contact(DEFAULT_CONTACT)
            .build();

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}