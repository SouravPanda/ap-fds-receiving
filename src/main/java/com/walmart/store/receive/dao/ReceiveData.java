package com.walmart.store.receive.dao;

import com.walmart.store.receive.walmart.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class ReceiveData {

    @Autowired
    private MongoTemplate mongoTemplate;
    public ReceiveData(MongoTemplate mongoTemplate){
        this.mongoTemplate=mongoTemplate;
    }

    @Value("${spring.data.mongodb.claim.collection.name}")
    private String claimCollectionName;

    public Collection<Object> getClaimLine(Integer po) {
        Query query = new Query(Criteria.where("Purchase Order Id").is(po));
        return mongoTemplate.find(query,Object.class,claimCollectionName);
    }

    public Object addClaimLine(Store store) {
        Query query = new Query(Criteria.where("Store").is(store));
        return mongoTemplate.save(Object.class,claimCollectionName);
    }
}
