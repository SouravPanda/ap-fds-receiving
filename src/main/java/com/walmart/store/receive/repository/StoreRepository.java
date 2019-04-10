package com.walmart.store.receive.repository;

import com.walmart.store.receive.walmart.Store;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

public interface StoreRepository extends MongoRepository<Store, String> {

    List<Store> findByVendorNumber(Integer vendorNumber);
}
