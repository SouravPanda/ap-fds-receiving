package com.walmart.store.receive.repository.impl;

import com.walmart.store.receive.repository.StoreRepository;
import com.walmart.store.receive.walmart.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.util.Arrays;


@ComponentScan(basePackages = "com.walmart.store.receive.repository")
public class StoreRepositoryImpl {

@Autowired
private StoreRepository storeRepository;
    @Value("${spring.data.mongodb.claim.collection.name}")
    private String claimCollectionName;

    public Object findByVendorNumber(Integer poId) {
     //   Query query = new Query(Criteria.where("purchaseOrderId").is(poId));
        return storeRepository.findAll();
    }


    public void updateReceiveSummary(Store store){
        if(null!=store){
            storeRepository.saveAll(Arrays.asList(store));
        }
    }
}
