package com.walmart.store.receive.pojo;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "receive-summary")
public class Store {

        private char receivingControlNumber;
        private int storeNumber;
        private int accountNumber;
        private int controlSequenceNumber;
        private Integer receiveSequenceNumber;
        private char matchIndicator;
        private Double totalCostAmount;
        private Double totalRetailAmount;
        private Integer freightBillId;
        private Integer poReceiveId;
        private Integer controlType;
        private Integer poId;
        private Integer transactionType;
        private int baseDivisionNumber;
        private Date finalDate;
        private Time finalTime;
        private char claimPendingIndicator;
        private char freeAstrayIndicator;
        private char freightConslIndicator;
        private Double receiveWeightQuantity;
        private char businessStatusCode;
        private List<ReceiveMDS> receiveMDS;
        private List<ReceiveExpense> receiveExpense;
        private char departmentNumber;
        private Integer casesReceived;
        private Timestamp finalizedLoadTimestamp;
        private Integer finalizedUpdateSequenceNumber;
        private BigInteger freightBillExpandId;
        private Timestamp initialReceiveTimestamp;
        private Date mdsReceiveDate;
        private Timestamp creationDate;
        private Timestamp lastUpdatedDate;
        private char createChannelOrSource;
        private char cosmosDB2SyncStatus;
        private char warehouseStoreIndicator;
        private Date receiveProcessDate;
        private char sequenceNumber;
        private Integer vendorNumber;
        private Date loadTimestamp;
        private char mdseQuantityUnitOfMeasureCode;
}

