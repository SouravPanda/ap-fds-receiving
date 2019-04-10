        package com.walmart.store.receive.mapper;

        import com.walmart.store.receive.walmart.Store;
        import org.springframework.stereotype.Component;

        import java.sql.Time;
        import java.util.Date;

        @Component
        public class StoreMapper {

            public Store getStores(Store store) {
                if (null != store) {
                    String _id = store.get_id();
                    store.set_id(_id);
                }
                    Integer poReceiveId=store.getPoReceiveId();
                if(null!=poReceiveId) {
                    store.setPoReceiveId(poReceiveId);
                }
                    Integer purchaseOrderId=store.getPurchaseOrderId();
                if(null!=purchaseOrderId) {
                    store.setPurchaseOrderId(purchaseOrderId);
                }
                    char transactionType=store.getTransactionType();
                if((int)transactionType!=0) {
                    store.setTransactionType(transactionType);
                }
                    String controlNumber=store.getControlNumber();
                if(null!=controlNumber) {
                    store.setControlNumber(controlNumber);
                }
                    Integer destinationBusinessUnitId=store.getDestinationBusinessUnitId();
                if(null!=destinationBusinessUnitId) {
                    store.setDestinationBusinessUnitId(destinationBusinessUnitId);
                }
                    Date finalDate=store.getFinalDate();
                if(null!=finalDate) {
                    store.setFinalDate(finalDate);
                }
                    Time finalTime=store.getFinalTime();
                if(null!=finalTime) {
                    store.setFinalTime(finalTime);
                }
                    Integer vendorNumber=store.getVendorNumber();
                if(null!=vendorNumber) {
                    store.setVendorNumber(vendorNumber);
                }
                    Date loadTimestamp=store.getLoadTimestamp();
                if(null!=loadTimestamp){
                    store.setLoadTimestamp(loadTimestamp);
                }
                return store;
            }
        }
