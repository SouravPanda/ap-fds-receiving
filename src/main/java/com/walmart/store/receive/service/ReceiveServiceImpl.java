        package com.walmart.store.receive.service;

        import com.walmart.store.receive.mapper.StoreMapper;
        import com.walmart.store.receive.repository.StoreRepository;
        import com.walmart.store.receive.pojo.Store;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.context.annotation.ComponentScan;
        import org.springframework.stereotype.Component;
        import org.springframework.util.CollectionUtils;
        import org.springframework.web.client.RestTemplate;

        import java.util.List;

        @Component
        public class ReceiveServiceImpl implements ReceiveService {

            @Autowired(required = true)
            private StoreRepository storeRepository;

            private RestTemplate restTemplate = new RestTemplate();

            public Store updateStores(Store store) {

                Integer vendorId = store.getVendorNumber();
                Store storeAt1 = new Store();
                List<Store> stores = storeRepository.findByVendorNumber(vendorId);
                if (!CollectionUtils.isEmpty(stores)) {
                    Store existingStore = stores.get(0);
                    existingStore.setDestinationBusinessUnitId(store.getDestinationBusinessUnitId());
                    storeRepository.save(existingStore);
                }
                return storeAt1;
            }

                public Store addNewStores(Store store) {

                    StoreMapper storeMapper = new StoreMapper();
                    Store storeAt1 = storeMapper.getStores(store);
                    Store storeAt2 = storeRepository.save(storeAt1);
                    if (null != storeAt2) {

                        return storeAt2;
                    } else {
                        return null;
                    }
                }
            }
