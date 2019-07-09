package com.walmart.finance.ap.fds.receiving.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {
    @Value("${spring.data.mongodb.uri}")
    private String mongoURI;

    @Getter
    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Bean
    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(mongoURI));
    }
}
 