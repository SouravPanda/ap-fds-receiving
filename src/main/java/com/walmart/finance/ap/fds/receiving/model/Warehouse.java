package com.walmart.finance.ap.fds.receiving.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "receiving-summary")
public class Warehouse {
        @Id
        private String _id;
        private char receivingControlNumber;
        private int storeNumber;
        private Integer freightBillId;
        private Integer poReceiveId;
        private Integer poId;
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

