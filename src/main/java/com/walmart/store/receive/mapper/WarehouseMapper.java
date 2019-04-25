            package com.walmart.store.receive.mapper;

            import com.walmart.store.receive.pojo.ReceiveExpense;
    import com.walmart.store.receive.pojo.ReceiveMDS;
    import com.walmart.store.receive.pojo.Warehouse;
    import org.springframework.stereotype.Component;

    import java.sql.Timestamp;
    import java.util.Date;
    import java.util.List;

            @Component
            public class WarehouseMapper {

                public Warehouse getWarehouse(Warehouse warehouse) {
                    int vendorNumber = warehouse.getVendorNumber();
                    if (vendorNumber != 0) {
                        warehouse.setVendorNumber(vendorNumber);
                    }
                    char receivingControlNumber = warehouse.getReceivingControlNumber();
                    if (receivingControlNumber != 0) {
                        warehouse.setReceivingControlNumber(receivingControlNumber);
                    }
                    Integer freightBillId = warehouse.getFreightBillId();
                    if (freightBillId != null) {
                        warehouse.setFreightBillId(freightBillId);
                    }
                    Integer poReceiveId = warehouse.getPoReceiveId();
                    if (poReceiveId != null) {
                        warehouse.setPoReceiveId(poReceiveId);
                    }
                    Integer poId = warehouse.getPoId();
                    if (poId != null) {
                        warehouse.setPoId(poId);
                    }
                    char businessStatusCode = warehouse.getBusinessStatusCode();
                    if (businessStatusCode != 0) {
                        warehouse.setBusinessStatusCode(businessStatusCode);
                    }
                    Date receiveProcessDate = warehouse.getReceiveProcessDate();
                    if (null != receiveProcessDate) {
                        warehouse.setReceiveProcessDate(receiveProcessDate);
                    }
                    Double receiveWeightQuantity = warehouse.getReceiveWeightQuantity();
                    if (receiveWeightQuantity != 0) {
                        warehouse.setReceiveWeightQuantity(receiveWeightQuantity);
                    }
                    Timestamp finalizedLoadTimestamp = warehouse.getFinalizedLoadTimestamp();
                    if (finalizedLoadTimestamp != null) {
                        warehouse.setFinalizedLoadTimestamp(finalizedLoadTimestamp);
                    }
                    Integer finalizedUpdateSequenceNumber = warehouse.getFinalizedUpdateSequenceNumber();
                    if (finalizedUpdateSequenceNumber != 0) {
                        warehouse.setFinalizedUpdateSequenceNumber(finalizedUpdateSequenceNumber);
                    }
                    List<ReceiveMDS> receiveMDS = warehouse.getReceiveMDS();
                    if (!receiveMDS.isEmpty()) {
                        warehouse.setReceiveMDS(receiveMDS);
                    }
                    List<ReceiveExpense> receiveExpense = warehouse.getReceiveExpense();
                    if (!receiveExpense.isEmpty()) {
                        warehouse.setReceiveExpense(receiveExpense);
                    }
                    Timestamp creationDate = warehouse.getCreationDate();
                    if (creationDate != null) {
                        warehouse.setCreationDate(creationDate);
                    }
                    Timestamp lastUpdatedDate = warehouse.getLastUpdatedDate();
                    if (lastUpdatedDate != null) {
                        warehouse.setLastUpdatedDate(lastUpdatedDate);
                    }
                    char createChannelOrSource = warehouse.getCreateChannelOrSource();
                    if (createChannelOrSource != 0) {
                        warehouse.setCreateChannelOrSource(createChannelOrSource);
                    }
                    char cosmosDB2SyncStatus = warehouse.getCosmosDB2SyncStatus();
                    if (cosmosDB2SyncStatus != 0) {
                        warehouse.setCosmosDB2SyncStatus(cosmosDB2SyncStatus);
                    }
                    char warehouseStoreIndicator = warehouse.getWarehouseStoreIndicator();
                    if (warehouseStoreIndicator != 0) {
                        warehouse.setWarehouseStoreIndicator(warehouseStoreIndicator);
                    }

                    return warehouse;
                }
            }
