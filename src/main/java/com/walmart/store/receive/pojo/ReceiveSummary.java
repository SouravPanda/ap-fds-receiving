package com.walmart.store.receive.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "receive-summary")
public class ReceiveSummary {
        @Id
        private String _id;
        @NotEmpty
        @JsonProperty("Receiving Control Number")
        // CONTROL_NBR
        private String receivingControlNumber;

        @NotEmpty
        @JsonProperty("ReceiveSummary Number")
        //STORE_NBR
        private Integer storeNumber;


        @NotEmpty
        @JsonProperty("Transaction Type")

        // TRANSACTION_TYPE
        private Integer transactionType;


        @NotEmpty
        @JsonProperty("Final Date")

        // FINAL_DATE
        private LocalDate finalDate;


        //FINAL_TIME
        @NotEmpty
        @JsonProperty("Final Time")
        private LocalTime finalTime;


         // CONTROL_NBR_TYPE
        @NotEmpty
        @JsonProperty("Control Type")
        private Integer controlType;



        // ORIG_DEST_ID  last 6 digit needed
        @NotEmpty
        @JsonProperty("Vendor Number")

        private Integer vendorNumber;


        // ACCTG_DIV_NBR
        @NotEmpty
        @JsonProperty("Account Number")
        private Integer accountNumber;


        // CONTROL_SEQ_NBR
        @JsonProperty("Control Sequence Number")
        private Integer controlSequenceNumber;


        // RPR_SEQ_NBR
        @JsonProperty("Receive Sequence Number")
        private Integer receiveSequenceNumber;


        // TOTAL_MATCH_IND
        @NotEmpty
        @JsonProperty("Match Indicator")
        private char matchIndicator;


        // TOTAL_COST_AMT
        @JsonProperty("Total Cost Amount")
        private Double totalCostAmount;



        // TOTAL_SALE_AMT
        @JsonProperty("Total Retail Amount")
        private Double totalRetailAmount;


        // Default value zero(s)
        @JsonProperty("Freight Bill ID")
        private Integer freightBillId;

       // Default value should be blank
        @JsonProperty("Business Status Code")
        private Character businessStatusCode;

        // default value is zero
        private Long freightBillExpandID;

        // default value is space
        private  Character claimPendingIndicator;


        // default value is space
        private  Character freeAstrayIndicator;

        // default value is space
        private  Character freightConslIndicator;


        //RECV_TIMESTAMP
        @JsonProperty("Initial Receive Timestamp")
        private LocalDateTime initialReceiveTimestamp;

        //RPR_DATE
        private LocalDate MDSReceiveDate;

        //
        private LocalDate receiveProcessDate;

        private  Double receiveWeightQuantity;


         private String sequenceNumber;


        // BKRM_PROC_XMIT_DTL/ ACCTG_DEPT_NBR
        @JsonProperty("Department Number")
        private Integer departmentNumber;

        //CASES_RECV
        @JsonProperty("Cases Received")
        private Integer casesReceived;


        @JsonProperty("Finalized Load Timestamp")
        private LocalDateTime finalizedLoadTimestamp;


        private Integer finalizedSequenceNumber;

        @NotEmpty
        @JsonProperty("PO Receive ID")

        // BASE_DIV_NBR + RPR_DATE(month) + RPR_SEQ_NBR

        private Integer poReceiveId;


        @NotEmpty
        @JsonProperty("Base Division Number")
        //BASE_DIV_NBR
        private Integer baseDivisionNumber;


    @JsonProperty("Create/Update Channel or Source")
    private String userId;

    @JsonProperty("Creation Date")
    private LocalDateTime creationDate;


























/*
        @NotEmpty
        @JsonProperty("Receive MDS")
        private List<ReceiveMDS> receiveMDS;
        @JsonProperty("Receive Expense")
        private List<ReceiveExpense> receiveExpense;

        @JsonProperty("Finalized Update Sequence Number")
        private Integer finalizedUpdateSequenceNumber;
        @JsonProperty("Last Updated Date")
        private LocalDateTime lastUpdatedDate;

        @JsonProperty("cosmos DB2 Sync Status")
        private Character cosmosDB2SyncStatus;
        @JsonProperty("warehouse store indicator")
        private Character warehouseStoreIndicator;

        private Integer recordType;

        private Character countryCode;


      */
}

