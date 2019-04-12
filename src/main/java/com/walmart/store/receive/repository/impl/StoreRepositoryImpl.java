package com.walmart.store.receive.repository.impl;

import com.walmart.store.receive.repository.StoreRepository;
import com.walmart.store.receive.pojo.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;


public class StoreRepositoryImpl {

@Autowired
private StoreRepository storeRepository;
    @Value("${mongodb.claim.collection.name}")
    private String claimCollectionName;

    public Object findByVendorNumber(Integer poId) {
        return storeRepository.findAll();
    }


    public void updateReceiveSummary(Store store){
        if(null!=store){
            storeRepository.saveAll(Arrays.asList(store));
        }
    }
}
