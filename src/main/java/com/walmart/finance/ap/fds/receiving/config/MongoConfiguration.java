package com.walmart.finance.ap.fds.receiving.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfiguration  {

    private static final Logger LOG = LoggerFactory.getLogger(MongoConfiguration.class);

    @Value("${receiving-${spring.profiles.active}}")
    private String mongoURI;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.mongoMinPoolSize}")
    private String mongoMinPoolSize;

    @Value("${spring.data.mongodb.mongoMaxPoolSize}")
    private String mongoMaxPoolSize;

    @Bean
    public MongoClient mongoClient() {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
        return mongoClient;
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, databaseName);
        return mongoTemplate;
    }

}
