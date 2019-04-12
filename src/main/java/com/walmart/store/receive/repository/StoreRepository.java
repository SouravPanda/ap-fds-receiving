package com.walmart.store.receive.repository;

import com.walmart.store.receive.pojo.Store;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StoreRepository extends MongoRepository<Store, String> {

    List<Store> findByVendorNumber(Integer vendorNumber);
}
