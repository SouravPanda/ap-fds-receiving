        package com.walmart.store.receive.service;

        import com.walmart.store.receive.ReceiveService;
        import com.walmart.store.receive.mapper.StoreMapper;
        import com.walmart.store.receive.repository.StoreRepository;
        import com.walmart.store.receive.walmart.Store;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.context.annotation.ComponentScan;
        import org.springframework.http.HttpHeaders;
        import org.springframework.http.MediaType;
        import org.springframework.stereotype.Component;
        import org.springframework.util.CollectionUtils;
        import org.springframework.web.client.RestTemplate;

        import java.util.List;

        import static com.walmart.store.receive.common.ReceiveConstants.WALMART_ITEM_JSON_ACCEPT;
        import static org.springframework.http.HttpHeaders.ACCEPT;

        @ComponentScan(basePackages = "com.walmart.store.receive.service,com.walmart.store.receive.repository")
        @Component
        public class ReceiveServiceImpl implements ReceiveService {

            @Autowired(required = true)
            private StoreRepository storeRepository;

            private RestTemplate restTemplate = new RestTemplate();

            public Store updateStores(Store store) {

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set(ACCEPT, WALMART_ITEM_JSON_ACCEPT);

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

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.set(ACCEPT, WALMART_ITEM_JSON_ACCEPT);

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
