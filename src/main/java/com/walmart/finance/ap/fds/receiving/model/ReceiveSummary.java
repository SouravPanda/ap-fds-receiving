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
@Document
public class ReceiveSummary {
    @Id
    private String _id;
    @NotEmpty
    // CONTROL_NBR
    //To be checked
    private String receivingControlNumber;//purchaseOrderId //Long

    @NotEmpty
    //STORE_NBR
    private Integer storeNumber;//locationNumber

    @NotEmpty
    //BASE_DIV_NBR
    private Integer baseDivisionNumber;

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
    //To be checked, not present in search or response,
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
    private String matchIndicator;


    // TOTAL_COST_AMT
    private Double totalCostAmount;


    // TOTAL_SALE_AMT
    private Double totalRetailAmount;


    // Default value zero(s)
//    private Integer freightBillId;

    // Default value should be blank
    //To be checked, not present in search or response
    private Character businessStatusCode;

    // default value is zero
    //To be checked, not present in search or response
    private Long freightBillExpandID;

    // default value is space
    //To be checked, not present in search or response
    private Character claimPendingIndicator;


    // default value is space
    //To be checked, not present in search or response
    private Character freeAstrayIndicator;

    // default value is space
    //To be checked, not present in search or response
    private Character freightConslIndicator;


    //RECV_TIMESTAMP
    //To be checked, not present in search or response
    private LocalDateTime receiveTimestamp;

    //RPR_DATE
    //To be checked, not present in search or response
    //private LocalDate MDSReceiveDate;
    private LocalDate receivingDate;

    //To be checked, not present in search or response
    private LocalDate receiveProcessDate;

    private Double receiveWeightQuantity;

    //To be checked, not present in search or response
    private Integer sequenceNumber;


    // BKRM_PROC_XMIT_DTL/ ACCTG_DEPT_NBR
    private Integer departmentNumber;

    //CASES_RECV
    //To be checked, not present in search or response
    private Integer casesReceived;

    //To be checked, not present in search or response
    private LocalDateTime finalizedLoadTimestamp;

    //To be checked, not present in search or response
    private Integer finalizedSequenceNumber;


    // BASE_DIV_NBR + RPR_DATE(month) + RPR_SEQ_NBR
    @NotEmpty
    //To be checked
    private String receiveId;//  receiptNumber //Long



    //@Builder.Default("user")
    private String userId;

    //To be checked, not present in search or response instead receiptDateStart present in SummarySearch
    private LocalDateTime creationDate;


    // PO_NBR_XREF/P1A_KEY(warehouse) :BKRM_PROC_XMAT_HDR/CONTROL_NBR(store)
    private String purchaseOrderNumber;

// identifier for Store(S) and Warehouse(W)
//To be checked, not present in search or response
    private Character typeIndicator;


    //   identifier for SOE write and sync Service write
//To be checked, not present in search or response
    private String writeIndicator;

    private Double bottleDepositAmount;

    private Integer purchaseOrderId ;

    @Override
    public String toString() {
        return "ReceiveSummary{" +
                "receivingControlNumber=" + receivingControlNumber +
                ", storeNumber=" + storeNumber +
                ", receiveId=" + receiveId +
                ", businessStatusCode=" + businessStatusCode +
                ", _id='" + _id + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", finalDate='" + finalDate + '\'' +
                ", finalTime='" + finalTime + '\'' +
                ", controlType='" + controlType + '\'' +
                ", vendorNumber='" + vendorNumber + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", controlSequenceNumber='" + controlSequenceNumber + '\'' +
                ", receiveSequenceNumber='" + receiveSequenceNumber + '\'' +
                ", matchIndicator='" + matchIndicator + '\'' +
                ", totalCostAmount='" + totalCostAmount + '\'' +
                ", totalRetailAmount='" + totalRetailAmount + '\'' +
//                ", freightBillId='" + freightBillId + '\'' +
                ", freightBillExpandID='" + freightBillExpandID + '\'' +
                ", claimPendingIndicator='" + claimPendingIndicator + '\'' +
                ", freeAstrayIndicator='" + freeAstrayIndicator + '\'' +
                ", freightConslIndicator='" + freightConslIndicator + '\'' +
                ", receiveTimestamp='" + receiveTimestamp + '\'' +
                ", receivingDate ='" + receivingDate + '\'' +
                ", receiveProcessDate='" + receiveProcessDate + '\'' +
                ", receiveWeightQuantity='" + receiveWeightQuantity + '\'' +
                ", sequenceNumber='" + sequenceNumber + '\'' +
                ", departmentNumber='" + departmentNumber + '\'' +
                ", casesReceived='" + casesReceived + '\'' +
                ", finalizedLoadTimestamp='" + finalizedLoadTimestamp + '\'' +
                ", finalizedSequenceNumber='" + finalizedSequenceNumber + '\'' +
                ", baseDivisionNumber='" + baseDivisionNumber + '\'' +
                ", userId='" + userId + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", purchaseOrderNumber='" + purchaseOrderNumber + '\'' +
                ", typeIndicator='" + typeIndicator + '\'' +
                ", writeIndicator='" + writeIndicator + '\'' +
                ", bottleDepositAmount='" + bottleDepositAmount + '\'' +
                ", purchaseOrderId='" + purchaseOrderId + '\'' +
                '}';
    }
}

