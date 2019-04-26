package com.walmart.store.receive.config;

import com.walmart.store.receive.converter.ReceivingSummaryResponseConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConverterRegistation implements WebMvcConfigurer {

    @Autowired
    private ReceivingSummaryResponseConverter receivingSummaryResponseConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(receivingSummaryResponseConverter);
    }
}
