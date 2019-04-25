package com.walmart.store.receive.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "col2")
public class Store {

        @Id
        private String _id;
        @NotEmpty
        @JsonProperty("Receiving Control Number")
        private char receivingControlNumber;
        @NotEmpty
        @JsonProperty("Store Number")
        private int storeNumber;
        @NotEmpty
        @JsonProperty("Account Number")
        private int accountNumber;
        @JsonProperty("Control Sequence Number")
        private int controlSequenceNumber;
        @JsonProperty("Receive Sequence Number")
        private Integer receiveSequenceNumber;
        @NotEmpty
        @JsonProperty("Match Indicator")
        private char matchIndicator;
        @JsonProperty("Total Cost Amount")
        private Double totalCostAmount;
        @JsonProperty("Total Retail Amount")
        private Double totalRetailAmount;
        @NotEmpty
        @JsonProperty("PO Receive ID")
        private Integer poReceiveId;
        @NotEmpty
        @JsonProperty("Transaction Type")
        private Integer transactionType;
        @NotEmpty
        @JsonProperty("Base Division Number")
        private int baseDivisionNumber;
        @NotEmpty
        @JsonProperty("Final Date")
        private Date finalDate;
        @NotEmpty
        @JsonProperty("Final Time")
        private Time finalTime;
        @NotEmpty
        @JsonProperty("Control Type")
        private Integer controlType;
        @JsonProperty("Receive Weight Quantity")
        private Double receiveWeightQuantity;
        @NotEmpty
        @JsonProperty("Receive MDS")
        private List<ReceiveMDS> receiveMDS;
        @JsonProperty("Receive Expense")
        private List<ReceiveExpense> receiveExpense;
        @JsonProperty("Department Number")
        private char departmentNumber;
        @JsonProperty("Cases Received")
        private Integer casesReceived;
        @JsonProperty("Finalized Load Timestamp")
        private Timestamp finalizedLoadTimestamp;
        @JsonProperty("Finalized Update Sequence Number")
        private Integer finalizedUpdateSequenceNumber;
        @JsonProperty("Initial Receive Timestamp")
        private Timestamp initialReceiveTimestamp;
        @JsonProperty("MDS Receive Date")
        private Date mdsReceiveDate;
        @JsonProperty("Creation Date")
        private Timestamp creationDate;
        @JsonProperty("Last Updated Date")
        private Timestamp lastUpdatedDate;
        @JsonProperty("Create/Update Channel or Source")
        private char USERID;
        @JsonProperty("cosmos DB2 Sync Status")
        private char cosmosDB2SyncStatus;
        @JsonProperty("warehouse store indicator")
        private char warehouseStoreIndicator;
        @JsonProperty("Receive Process Date")
        private Date receiveProcessDate;
        @JsonProperty("Sequence Number")
        private char sequenceNumber;
        @NotEmpty
        @JsonProperty("Vendor Number")
        private Integer vendorNumber;
        private Integer recordType;
        private char countryCode;
}

