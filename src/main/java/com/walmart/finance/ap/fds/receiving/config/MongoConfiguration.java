package com.walmart.finance.ap.fds.receiving.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(MongoConfiguration.class);

    @Value("${receiving-${spring.profiles.active}}")
    private String mongoURI;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.mongoMinPoolSize}")
    private String mongoMinPoolSize;

    @Value("${spring.data.mongodb.mongoMaxPoolSize}")
    private String mongoMaxPoolSize;


    @Override
    public MongoClient mongoClient() {
        LOG.debug("country is {} ",System.getenv("COUNTRY_NAME"));
        MongoClientOptions.Builder optionsBuilder = new MongoClientOptions.Builder();
//        optionsBuilder.minConnectionsPerHost(Integer.valueOf(mongoMinPoolSize));
//        optionsBuilder.connectionsPerHost(Integer.valueOf(mongoMaxPoolSize));
        optionsBuilder.maxConnectionIdleTime(600000);
        LOG.debug("mongo uri {}",mongoURI);
        MongoClientURI uri = new MongoClientURI(mongoURI, optionsBuilder);
        return  new MongoClient (uri);
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }
}
