package com.walmart.finance.ap.fds.receiving.model;

import com.walmart.finance.ap.fds.receiving.response.ReceiveMDSResponse;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

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
    private Long freightBillExpandId;

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
    private String departmentNumber;

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

    private Long purchaseOrderId ;

    private String dataSyncStatus;
    private LocalDateTime creationTimestamp;
    private LocalDateTime lastUpdatedDate;
    private LocalDateTime lastUpdatedTimestamp;
    private LocalDateTime dateReceived;

    private Map<String, ReceiveMDSResponse> merchandises;


    public void merge(ReceiveSummary receiveSummary) {

        if(this.dateReceived.isAfter(receiveSummary.dateReceived)) {

            // This block will only be executed if 'dateReceived' for 'this' object is after 'receiveSummary'
            // Hence 'this' has the latest changes
            // We copy values from 'receiveSummary' only if the corresponding value in 'this' is null/empty

            this.receivingControlNumber = StringUtils.isEmpty(this.receivingControlNumber) ?
                    receiveSummary.receivingControlNumber : this.receivingControlNumber ;
            this.storeNumber = (this.storeNumber == null) ?
                    receiveSummary.storeNumber : this.storeNumber;
            this.baseDivisionNumber = (this.baseDivisionNumber == null) ?
                    receiveSummary.baseDivisionNumber : this.baseDivisionNumber;
            this.transactionType = (this.transactionType == null) ?
                    receiveSummary.transactionType : this.transactionType;
            this.finalDate = ((this.finalDate == null)
                    || this.finalDate.isEqual(LocalDate.ofEpochDay(0))) ?
                    receiveSummary.finalDate : this.finalDate;
            this.finalTime = (this.finalTime == null) ?
                    receiveSummary.finalTime : this.finalTime;
            this.controlType = (this.controlType == null) ?
                    receiveSummary.controlType : this.controlType;
            this.vendorNumber = (this.vendorNumber == null) ?
                    receiveSummary.vendorNumber : this.vendorNumber;
            this.accountNumber = (this.accountNumber == null) ?
                    receiveSummary.accountNumber : this.accountNumber;
            this.controlSequenceNumber = (this.controlSequenceNumber == null) ?
                    receiveSummary.controlSequenceNumber : this.controlSequenceNumber;
            this.receiveSequenceNumber = (this.receiveSequenceNumber == null) ?
                    receiveSummary.receiveSequenceNumber : this.receiveSequenceNumber;
            this.matchIndicator = StringUtils.isEmpty(this.matchIndicator) ?
                    receiveSummary.matchIndicator : this.matchIndicator ;
            this.totalCostAmount = (this.totalCostAmount == null) ?
                    receiveSummary.totalCostAmount : this.totalCostAmount;
            this.totalRetailAmount = (this.totalRetailAmount == null) ?
                    receiveSummary.totalRetailAmount : this.totalRetailAmount;
            this.businessStatusCode = (this.businessStatusCode == null) ?
                    receiveSummary.businessStatusCode : this.businessStatusCode;
            this.freightBillExpandId = (this.freightBillExpandId == null) ?
                    receiveSummary.freightBillExpandId : this.freightBillExpandId;
            this.claimPendingIndicator = (this.claimPendingIndicator == null) ?
                    receiveSummary.claimPendingIndicator : this.claimPendingIndicator;
            this.freeAstrayIndicator = (this.freeAstrayIndicator == null) ?
                    receiveSummary.freeAstrayIndicator : this.freeAstrayIndicator;
            this.freightConslIndicator = (this.freightConslIndicator == null) ?
                    receiveSummary.freightConslIndicator : this.freightConslIndicator;
            this.receiveTimestamp = (this.receiveTimestamp == null) ?
                    receiveSummary.receiveTimestamp : this.receiveTimestamp;
            this.receivingDate = ((this.receivingDate == null)
                    || this.receivingDate.isEqual(LocalDate.ofEpochDay(0))) ?
                    receiveSummary.receivingDate : this.receivingDate;
            this.receiveProcessDate = ((this.receiveProcessDate == null)
                    || this.receiveProcessDate.isEqual(LocalDate.ofEpochDay(0))) ?
                    receiveSummary.receiveProcessDate : this.receiveProcessDate;
            this.receiveWeightQuantity = (this.receiveWeightQuantity == null) ?
                    receiveSummary.receiveWeightQuantity : this.receiveWeightQuantity;
            this.sequenceNumber = (this.sequenceNumber == null) ?
                    receiveSummary.sequenceNumber : this.sequenceNumber;
            this.departmentNumber = StringUtils.isEmpty(this.departmentNumber) ?
                    receiveSummary.departmentNumber : this.departmentNumber ;
            this.casesReceived = (this.casesReceived == null) ?
                    receiveSummary.casesReceived : this.casesReceived;
            this.finalizedLoadTimestamp = (this.finalizedLoadTimestamp == null) ?
                    receiveSummary.finalizedLoadTimestamp : this.finalizedLoadTimestamp;
            this.finalizedSequenceNumber = (this.finalizedSequenceNumber == null) ?
                    receiveSummary.finalizedSequenceNumber : this.finalizedSequenceNumber;
            this.receiveId = StringUtils.isEmpty(this.receiveId) ?
                    receiveSummary.receiveId : this.receiveId ;
            this.userId = StringUtils.isEmpty(this.userId) ?
                    receiveSummary.userId : this.userId ;
            this.creationDate = (this.creationDate == null)  ?
                    receiveSummary.creationDate : this.creationDate;
            this.purchaseOrderNumber = StringUtils.isEmpty(this.purchaseOrderNumber) ?
                    receiveSummary.purchaseOrderNumber : this.purchaseOrderNumber ;
            this.typeIndicator = (this.typeIndicator == null)  ?
                    receiveSummary.typeIndicator : this.typeIndicator;
            this.writeIndicator = StringUtils.isEmpty(this.writeIndicator) ?
                    receiveSummary.writeIndicator : this.writeIndicator;
            this.bottleDepositAmount = (this.bottleDepositAmount == null)  ?
                    receiveSummary.bottleDepositAmount : this.bottleDepositAmount;
            this.purchaseOrderId = (this.purchaseOrderId == null)  ?
                    receiveSummary.purchaseOrderId : this.purchaseOrderId;
            this.dataSyncStatus = StringUtils.isEmpty(this.dataSyncStatus) ?
                    receiveSummary.dataSyncStatus : this.dataSyncStatus;
            this.creationTimestamp = (this.creationTimestamp == null)  ?
                    receiveSummary.creationTimestamp : this.creationTimestamp;
            this.lastUpdatedDate = (this.lastUpdatedDate == null)  ?
                    receiveSummary.lastUpdatedDate : this.lastUpdatedDate;
            this.lastUpdatedTimestamp = (this.lastUpdatedTimestamp == null)  ?
                    receiveSummary.lastUpdatedTimestamp : this.lastUpdatedTimestamp;
            this.merchandises = (this.merchandises == null) ?
                    receiveSummary.merchandises : this.merchandises;

        } else {

            // This block will only be executed if 'dateReceived' for 'this' object is before 'receiveSummary'
            // Hence 'receiveSummary' has the latest changes
            // We copy values from 'receiveSummary' if the corresponding value in it is NOT null/empty

            this.receivingControlNumber = StringUtils.isNotEmpty(receiveSummary.receivingControlNumber) ?
                    receiveSummary.receivingControlNumber : this.receivingControlNumber ;
            this.storeNumber = (receiveSummary.storeNumber != null) ?
                    receiveSummary.storeNumber : this.storeNumber;
            this.baseDivisionNumber = (receiveSummary.baseDivisionNumber != null) ?
                    receiveSummary.baseDivisionNumber : this.baseDivisionNumber;
            this.transactionType = (receiveSummary.transactionType != null) ?
                    receiveSummary.transactionType : this.transactionType;
            this.finalDate = ((receiveSummary.finalDate != null)
                    && receiveSummary.finalDate.isAfter(LocalDate.ofEpochDay(0))) ?
                    receiveSummary.finalDate : this.finalDate;
            this.finalTime = (receiveSummary.finalTime != null) ?
                    receiveSummary.finalTime : this.finalTime;
            this.controlType = (receiveSummary.controlType != null) ?
                    receiveSummary.controlType : this.controlType;
            this.vendorNumber = (receiveSummary.vendorNumber != null) ?
                    receiveSummary.vendorNumber : this.vendorNumber;
            this.accountNumber = (receiveSummary.accountNumber != null) ?
                    receiveSummary.accountNumber : this.accountNumber;
            this.controlSequenceNumber = (receiveSummary.controlSequenceNumber != null) ?
                    receiveSummary.controlSequenceNumber : this.controlSequenceNumber;
            this.receiveSequenceNumber = (receiveSummary.receiveSequenceNumber != null) ?
                    receiveSummary.receiveSequenceNumber : this.receiveSequenceNumber;
            this.matchIndicator = StringUtils.isNotEmpty(receiveSummary.matchIndicator) ?
                    receiveSummary.matchIndicator : this.matchIndicator ;
            this.totalCostAmount = (receiveSummary.totalCostAmount != null) ?
                    receiveSummary.totalCostAmount : this.totalCostAmount;
            this.totalRetailAmount = (receiveSummary.totalRetailAmount != null) ?
                    receiveSummary.totalRetailAmount : this.totalRetailAmount;
            this.businessStatusCode = (receiveSummary.businessStatusCode != null) ?
                    receiveSummary.businessStatusCode : this.businessStatusCode;
            this.freightBillExpandId = (receiveSummary.freightBillExpandId != null) ?
                    receiveSummary.freightBillExpandId : this.freightBillExpandId;
            this.claimPendingIndicator = (receiveSummary.claimPendingIndicator != null) ?
                    receiveSummary.claimPendingIndicator : this.claimPendingIndicator;
            this.freeAstrayIndicator = (receiveSummary.freeAstrayIndicator != null) ?
                    receiveSummary.freeAstrayIndicator : this.freeAstrayIndicator;
            this.freightConslIndicator = (receiveSummary.freightConslIndicator != null) ?
                    receiveSummary.freightConslIndicator : this.freightConslIndicator;
            this.receiveTimestamp = (receiveSummary.receiveTimestamp != null) ?
                    receiveSummary.receiveTimestamp : this.receiveTimestamp;
            this.receivingDate = ((receiveSummary.receivingDate != null)
                    && receiveSummary.receivingDate.isAfter(LocalDate.ofEpochDay(0))) ?
                    receiveSummary.receivingDate : this.receivingDate;
            this.receiveProcessDate = ((receiveSummary.receiveProcessDate != null)
                    && receiveSummary.receiveProcessDate.isAfter(LocalDate.ofEpochDay(0))) ?
                    receiveSummary.receiveProcessDate : this.receiveProcessDate;
            this.receiveWeightQuantity = (receiveSummary.receiveWeightQuantity != null) ?
                    receiveSummary.receiveWeightQuantity : this.receiveWeightQuantity;
            this.sequenceNumber = (receiveSummary.sequenceNumber != null) ?
                    receiveSummary.sequenceNumber : this.sequenceNumber;
            this.departmentNumber = StringUtils.isNotEmpty(receiveSummary.departmentNumber) ?
                    receiveSummary.departmentNumber : this.departmentNumber ;
            this.casesReceived = (receiveSummary.casesReceived != null) ?
                    receiveSummary.casesReceived : this.casesReceived;
            this.finalizedLoadTimestamp = (receiveSummary.finalizedLoadTimestamp != null) ?
                    receiveSummary.finalizedLoadTimestamp : this.finalizedLoadTimestamp;
            this.finalizedSequenceNumber = (receiveSummary.finalizedSequenceNumber != null) ?
                    receiveSummary.finalizedSequenceNumber : this.finalizedSequenceNumber;
            this.receiveId = StringUtils.isNotEmpty(receiveSummary.receiveId) ?
                    receiveSummary.receiveId : this.receiveId ;
            this.userId = StringUtils.isNotEmpty(receiveSummary.userId) ?
                    receiveSummary.userId : this.userId ;
            this.creationDate = (receiveSummary.creationDate != null)  ?
                    receiveSummary.creationDate : this.creationDate;
            this.purchaseOrderNumber = StringUtils.isNotEmpty(receiveSummary.purchaseOrderNumber) ?
                    receiveSummary.purchaseOrderNumber : this.purchaseOrderNumber ;
            this.typeIndicator = (receiveSummary.typeIndicator != null)  ?
                    receiveSummary.typeIndicator : this.typeIndicator;
            this.writeIndicator = StringUtils.isNotEmpty(receiveSummary.writeIndicator) ?
                    receiveSummary.writeIndicator : this.writeIndicator ;
            this.bottleDepositAmount = (receiveSummary.bottleDepositAmount != null)  ?
                    receiveSummary.bottleDepositAmount : this.bottleDepositAmount;
            this.purchaseOrderId = (receiveSummary.purchaseOrderId != null)  ?
                    receiveSummary.purchaseOrderId : this.purchaseOrderId;
            this.dataSyncStatus = StringUtils.isNotEmpty(receiveSummary.dataSyncStatus) ?
                    receiveSummary.dataSyncStatus : this.dataSyncStatus;
            this.creationTimestamp = (receiveSummary.creationTimestamp != null)  ?
                    receiveSummary.creationTimestamp : this.creationTimestamp;
            this.lastUpdatedDate = (this.lastUpdatedDate == null)  ?
                    receiveSummary.lastUpdatedDate : this.lastUpdatedDate;
            this.lastUpdatedTimestamp = (receiveSummary.lastUpdatedTimestamp != null)  ?
                    receiveSummary.lastUpdatedTimestamp : this.lastUpdatedTimestamp;
            this.merchandises = ( receiveSummary.merchandises != null ) ?
                    receiveSummary.merchandises : this.merchandises;
            this.dateReceived = receiveSummary.dateReceived;
        }
    }

}

