package com.walmart.store.receive.dao;

import com.walmart.store.receive.pojo.Store;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReceiveDataRepository extends MongoRepository<Store,String> {
}
