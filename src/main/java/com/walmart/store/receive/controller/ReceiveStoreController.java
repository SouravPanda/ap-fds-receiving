    package com.walmart.store.receive.controller;


    import com.walmart.store.receive.service.ReceiveServiceImpl;
    import com.walmart.store.receive.walmart.Store;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;



    @RestController
    public class ReceiveStoreController {

         @Autowired(required = true)
         private ReceiveServiceImpl receiveServiceImpl;


        @RequestMapping(method = { RequestMethod.POST },path = {"/stores"})

        /**
         * Method calls Service class to add stores in Db
         * @param store
         * @return
         */
        public ResponseEntity updateStoreByVendorNumber(@RequestBody Store store) {
            receiveServiceImpl.updateStores(store);
            return ResponseEntity.ok(HttpStatus.OK);
        }


        @RequestMapping(method = { RequestMethod.POST },path = {"/store"})
        /**
         * Method calls Service class to fetch stores by the given vendorNumber
         * @param store
         * @return
         */
        public void addNewStore(@RequestBody Store store){
            receiveServiceImpl.addNewStores(store);
                }
        }
