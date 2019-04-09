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
                Integer poReceiveId=store.getPoReceiveId();
                store.setPoReceiveId(poReceiveId);
                Integer purchaseOrderId=store.getPurchaseOrderId();
                store.setPurchaseOrderId(purchaseOrderId);
                char transactionType=store.getTransactionType();
                store.setTransactionType(transactionType);
                String controlNumber=store.getControlNumber();
                store.setControlNumber(controlNumber);
                Integer destinationBusinessUnitId=store.getDestinationBusinessUnitId();
                store.setDestinationBusinessUnitId(destinationBusinessUnitId);
                Date finalDate=store.getFinalDate();
                store.setFinalDate(finalDate);
                Time finalTime=store.getFinalTime();
                store.setFinalTime(finalTime);
                Integer vendorNumber=store.getVendorNumber();
                store.setVendorNumber(vendorNumber);
                Date loadTimestamp=store.getLoadTimestamp();
                store.setLoadTimestamp(loadTimestamp);
            }
            return store;
        }
    }
