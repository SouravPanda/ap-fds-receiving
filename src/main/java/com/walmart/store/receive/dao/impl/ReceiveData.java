    package com.walmart.store.receive.dao.impl;

    import com.walmart.store.receive.pojo.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;

import static com.walmart.store.receive.common.ReceiveConstants.*;

    @Repository
    public class ReceiveData {

        @Autowired
        private MongoTemplate mongoTemplate;
        public ReceiveData(MongoTemplate mongoTemplate){
            this.mongoTemplate=mongoTemplate;
        }

        @Value("${mongodb.claim.collection.name}")
        private String claimCollectionName;

        public Store addStores(Store store) {
           // store=mongoTemplate.insert(store,claimCollectionName);
            store= mongoTemplate.save(store,claimCollectionName);
            return store;

        }

        public Long updateStoreData(Store store){
            Update update = new Update();
            Query query = new Query();
            query.addCriteria(Criteria.where(RECEIVING_CONTROL_NUMBER).is(store.getReceivingControlNumber()).and(PO_RECEIVE_ID).is(store.getPoReceiveId())
                    .and(STORE_NUMBER).is(store.getStoreNumber()).and(BASE_DIVISON_NUMBER).is(store.getBaseDivisionNumber()).and(TRANSACTION_TYPE).is(store.getTransactionType()));
            Date date= new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            update.set("Initial Receive Timestamp", timestamp);
           return mongoTemplate.updateMulti(query,update,Store.class).getModifiedCount();
        }

    }

