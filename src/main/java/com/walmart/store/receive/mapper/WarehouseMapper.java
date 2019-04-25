        package com.walmart.store.receive.mapper;

        import com.walmart.store.receive.pojo.ReceiveExpense;
        import com.walmart.store.receive.pojo.ReceiveMDS;
        import com.walmart.store.receive.pojo.Store;
        import org.springframework.stereotype.Component;

        import java.sql.Time;
        import java.sql.Timestamp;
        import java.util.Date;
        import java.util.List;

        @Component
        public class WarehouseMapper {

            public Store getStores(Store store) {
                int vendorNumber=store.getVendorNumber();
                if(vendorNumber!=0){
                    store.setVendorNumber(vendorNumber);
                }
                int accountNumber=store.getAccountNumber();
                if(accountNumber!=0){
                    store.setAccountNumber(accountNumber);
                }
                int controlSequenceNumber=store.getControlSequenceNumber();
                if(controlSequenceNumber!=0){
                    store.setControlSequenceNumber(controlSequenceNumber);
                }
                char matchIndicator=store.getMatchIndicator();
                if(matchIndicator!=0){
                    store.setMatchIndicator(matchIndicator);
                }
                Double totalCostAmount=store.getTotalCostAmount();
                if(totalCostAmount!=null){
                    store.setTotalCostAmount(totalCostAmount);
                }
                Double totalRetailAmount=store.getTotalRetailAmount();
                if(totalRetailAmount!=null){
                    store.setTotalRetailAmount(totalRetailAmount);
                }
                Integer freightBillId=store.getFreightBillId();
                if(freightBillId!=null){
                    store.setFreightBillId(freightBillId);
                }
                Integer poReceiveId=store.getPoReceiveId();
                if(poReceiveId!=null){
                    store.setPoReceiveId(poReceiveId);
                }
                Integer poId=store.getPoId();
                if(poId!=null){
                    store.setPoId(poId);
                }
                Integer controlType=store.getControlType();
                if(controlType!=null){
                    store.setControlType(controlType);
                }
                Integer transactionType=store.getTransactionType();
                if(transactionType!=null){
                    store.setTransactionType(transactionType);
                }
                int baseDivisionNumber=store.getBaseDivisionNumber();
                if(baseDivisionNumber!=0){
                    store.setBaseDivisionNumber(baseDivisionNumber);
                }
                Date finalDate=store.getFinalDate();
                if(null!=finalDate) {
                    store.setFinalDate(finalDate);
                }
                    Time finalTime=store.getFinalTime();
                if(null!=finalTime) {
                    store.setFinalTime(finalTime);
                }
                Integer receiveSequenceNumber=store.getReceiveSequenceNumber();
                if(null!=receiveSequenceNumber) {
                    store.setReceiveSequenceNumber(receiveSequenceNumber);
                }
                Date loadTimestamp=store.getLoadTimestamp();
                if(null!=loadTimestamp){
                    store.setLoadTimestamp(loadTimestamp);
                }
                char claimPendingIndicator=store.getClaimPendingIndicator();
                if(claimPendingIndicator!=0){
                    store.setClaimPendingIndicator(claimPendingIndicator);
                }
                char freeAstrayIndicator=store.getFreeAstrayIndicator();
                if(freeAstrayIndicator!=0){
                    store.setFreeAstrayIndicator(freeAstrayIndicator);
                }
                char freightConslIndicator=store.getFreightConslIndicator();
                if(freightConslIndicator!=0){
                    store.setFreightConslIndicator(freightConslIndicator);
                }
                char createChannelOrSource=store.getCreateChannelOrSource();
                if(createChannelOrSource!=0){
                    store.setCreateChannelOrSource(createChannelOrSource);
                }
                char cosmosDB2SyncStatus=store.getCosmosDB2SyncStatus();
                if(cosmosDB2SyncStatus!=0){
                    store.setCosmosDB2SyncStatus(cosmosDB2SyncStatus);
                }
                char warehouseStoreIndicator=store.getWarehouseStoreIndicator();
                if(warehouseStoreIndicator!=0){
                    store.setWarehouseStoreIndicator(warehouseStoreIndicator);
                }
                char sequenceNumber=store.getSequenceNumber();
                if(sequenceNumber!=0){
                    store.setSequenceNumber(sequenceNumber);
                }
                char mdseQuantityUnitOfMeasureCode=store.getMdseQuantityUnitOfMeasureCode();
                if(mdseQuantityUnitOfMeasureCode!=0){
                    store.setMdseQuantityUnitOfMeasureCode(mdseQuantityUnitOfMeasureCode);
                }
                List<ReceiveMDS> receiveMDS=store.getReceiveMDS();
                if(!receiveMDS.isEmpty()){
                    store.setReceiveMDS(receiveMDS);
                }
                List<ReceiveExpense> receiveExpense=store.getReceiveExpense();
                if(!receiveExpense.isEmpty()){
                    store.setReceiveExpense(receiveExpense);
                }
                Integer casesReceived=store.getCasesReceived();
                if(casesReceived!=null){
                    store.setCasesReceived(casesReceived);
                }
                Timestamp finalizedLoadTimestamp= store.getFinalizedLoadTimestamp();
                if(finalizedLoadTimestamp!=null){
                    store.setFinalizedLoadTimestamp(finalizedLoadTimestamp);
                }
                Timestamp initialReceiveTimestamp= store.getInitialReceiveTimestamp();
                if(initialReceiveTimestamp!=null){
                    store.setInitialReceiveTimestamp(initialReceiveTimestamp);
                }
                Date mdsReceiveDate=store.getFinalDate();
                if(null!=mdsReceiveDate) {
                    store.setMdsReceiveDate(mdsReceiveDate);
                }
                Date receiveProcessDate=store.getReceiveProcessDate();
                if(null!=receiveProcessDate) {
                    store.setReceiveProcessDate(receiveProcessDate);
                }
                return store;
            }
        }
