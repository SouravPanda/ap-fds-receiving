    package com.walmart.store.receive.controller;


    import com.walmart.store.receive.pojo.Store;
import com.walmart.store.receive.service.ReceiveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



    @RestController
    @Api(value = "RESTful APIs for FoundationSupplier services")
    public class ReceiveStoreController {

        @Autowired
        private ReceiveService receiveServiceImpl;


        @RequestMapping(method = { RequestMethod.PATCH },value = "/update")
        @ApiOperation(value = "API to update Stores based on the Vendor Number")
        @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Exception")})
        /**
         * Method calls Service class to fetch stores by the given vendorNumber
         * @param store
         * @return
         */
            public ResponseEntity updateStoresBasedOnTimestamp(@RequestBody Store store) {
                Long storeNew=receiveServiceImpl.updateStores(store);
                if(storeNew!=0) {
                    return ResponseEntity.ok(HttpStatus.OK);
                }
                else{
                    return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
                }
            }

        /**
         * Method calls Service class to add stores in Db
         * @param store
         * @return
         */
        @RequestMapping(method = { RequestMethod.POST},path = {"/store"})
        @ApiOperation(value = "API to add new Stores based on the payload")
        @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Exception")})

            public ResponseEntity addNewStore(@RequestBody Store store) {

                Store saveStore = receiveServiceImpl.addNewStores(store);
                if (saveStore != null) {
                    return ResponseEntity.ok(HttpStatus.OK);
                }
                else{
                    return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
                }
            }
            }
