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

//        private RestTemplate restTemplate = new RestTemplate();
/*
            public Store updateStores(Store store) {
           // Collection<Store> collectionOfStores = receiveDataRepository.findByInitialReceiveTimestamp(store.getInitialReceiveTimestamp());
                Collection<Store> collectionOfStores = receiveDataRepository.updateReceiveSummary(store);
                List<Store> listOfStores=null;
                if (collectionOfStores instanceof List)
                {
                    listOfStores = (List<Store>) collectionOfStores;
                }
            Store storeAtOne = null;
            if(!CollectionUtils.isEmpty(listOfStores)){
                storeAtOne = listOfStores.get(0);
            }
            Date date= new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            if(null!=storeAtOne){
                storeAtOne.setLastUpdatedDate(timestamp);
            }
        *//*    if(null!=store){
               // receiveDataRepository.saveAll(Arrays.asList(storeAtOne));
            }*//*
                return storeAtOne;
        }*/
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


