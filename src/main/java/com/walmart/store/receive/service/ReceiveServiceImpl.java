package com.walmart.store.receive.service;

import com.walmart.store.receive.dao.ReceiveDataRepository;
import com.walmart.store.receive.dao.impl.ReceiveData;
import com.walmart.store.receive.mapper.StoreMapper;
import com.walmart.store.receive.pojo.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

    @Component
    public class ReceiveServiceImpl implements ReceiveService {

        @Autowired
        private ReceiveData receiveData;

        @Autowired
        ReceiveDataRepository receiveDataRepository;

        public Long updateStores(Store store){
           return receiveData.updateStoreData(store);
        }

       public Store addNewStores(Store store) {
            StoreMapper storeMapper = new StoreMapper();
            Store storeAt1 = storeMapper.getStores(store);
            Store savedStore =  receiveDataRepository.save(storeAt1);
            if (null != savedStore) {
                return savedStore;
            } else {
                return null;
            }
        }
    }


