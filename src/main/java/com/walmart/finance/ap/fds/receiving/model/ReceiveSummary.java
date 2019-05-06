package com.walmart.finance.ap.fds.receiving.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    // CONTROL_NBR
    private String receivingControlNumber;//purchaseOrderId

    @NotEmpty
    //STORE_NBR
    private Integer storeNumber;//locationNumber


    @NotEmpty
    // TRANSACTION_TYPE
    private Integer transactionType;


    @NotEmpty
    // FINAL_DATE
    private LocalDate finalDate;


    //FINAL_TIME
    @NotEmpty
    private LocalTime finalTime;


    // CONTROL_NBR_TYPE
    @NotEmpty
    private Integer controlType;


    // ORIG_DEST_ID  last 6 digit needed
    @NotEmpty
    private Integer vendorNumber;


    // ACCTG_DIV_NBR
    @NotEmpty
    private Integer accountNumber;


    // CONTROL_SEQ_NBR
    private Integer controlSequenceNumber;


    // RPR_SEQ_NBR
    private Integer receiveSequenceNumber;


    // TOTAL_MATCH_IND
    @NotEmpty
    private char matchIndicator;


    // TOTAL_COST_AMT
    private Double totalCostAmount;


    // TOTAL_SALE_AMT
    private Double totalRetailAmount;


    // Default value zero(s)
    private Integer freightBillId;

    // Default value should be blank
    private Character businessStatusCode;

    // default value is zero
    private Long freightBillExpandID;

    // default value is space
    private Character claimPendingIndicator;


    // default value is space
    private Character freeAstrayIndicator;

    // default value is space
    private Character freightConslIndicator;


    //RECV_TIMESTAMP
    private LocalDateTime initialReceiveTimestamp;

    //RPR_DATE
    private LocalDate MDSReceiveDate;

    //
    private LocalDate receiveProcessDate;

    private Double receiveWeightQuantity;


    private String sequenceNumber;


    // BKRM_PROC_XMIT_DTL/ ACCTG_DEPT_NBR
    private Integer departmentNumber;

    //CASES_RECV
    private Integer casesReceived;


    private LocalDateTime finalizedLoadTimestamp;


    private Integer finalizedSequenceNumber;


    // BASE_DIV_NBR + RPR_DATE(month) + RPR_SEQ_NBR
    @NotEmpty
    private Integer poReceiveId;//  receiptNumber


    @NotEmpty
    //BASE_DIV_NBR
    private Integer baseDivisionNumber;


    private String userId;

    private LocalDateTime creationDate;


    // PO_NBR_XREF/P1A_KEY(warehouse) :BKRM_PROC_XMAT_HDR/CONTROL_NBR(store)
    private String purchaseOrderNumber;







//TODO, pagination , indexing



















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

