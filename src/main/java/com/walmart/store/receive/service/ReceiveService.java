package com.walmart.store.receive.service;

import com.walmart.store.receive.pojo.Store;


public interface ReceiveService {
   Store updateStores(Store store);
   Store addNewStores(Store store);
}
