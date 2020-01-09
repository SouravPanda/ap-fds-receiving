package com.walmart.finance.ap.fds.receiving.model;

import com.walmart.finance.ap.fds.receiving.response.ReceiveMDSResponse;
import com.walmart.finance.ap.fds.receiving.response.WHLinePOLineValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Document
public class ReceivingLine   {

    private static final String separator = "|";
    private String _id;
    private String receiveId;     //BASE_DIV_NBR + RPR_DATE(month) + RPR_SEQ_NBR from BKRM_PROC_XMIT_HDR
    private Integer lineNumber;     //BKRM_PROC_XMIT_DTL.LINE_NBR
    private Long itemNumber;     //BKRM_PROC_XMIT_DTL.ITEM_NBR
    private Integer vendorNumber;   //BKRM_PROC_XMIT_HDR.ORIG_DEST_ID
    private Double receivedQuantity;   //BKRM_PROC_XMIT_DTL.ITEM_QTY
    private Double costAmount;      //BKRM_PROC_XMIT_DTL.COST_AMT
    private Double retailAmount;    //BKRM_PROC_XMIT_DTL.STORE_SALE_AMT

    private String receivingControlNumber;     //BKRM_PROC_XMIT_HDR.CONTROL_NBR
    private Integer purchaseReceiptNumber;      //BASE_DIV_NBR + RPR_DATE(month) + RPR_SEQ_NBR
    private Integer purchasedOrderId;       //CONTROL_NBR for control_nbr_type = 0
    private String upcNumber;      //BKRM_PROC_XMIT_DTL.UPC_NBR

    private Integer transactionType;   //BKRM_PROC_XMIT_DTL.TRANSACTION_TYPE
    private Integer storeNumber;    //BKRM_PROC_XMIT_DTL.STORE_NBR
    private Integer baseDivisionNumber;     //BKRM_PROC_XMIT_DTL.BASE_DIV_NBR
    private LocalDate finalDate;       //BKRM_PROC_XMIT_DTL.FINAL_DATE
    private LocalDateTime finalTime;       //BKRM_PROC_XMIT_DTL.FINAL_TIME
    private Integer sequenceNumber; //BKRM_PROC_XMIT_DTL.SEQUENCE_NBR
    private LocalDateTime creationDate;
    private Character typeIndicator; // identifier for Store(S) and Warehouse(W)
    private String writeIndicator; //
    private String purchaseOrderNumber; // PO_NBR_XREF/P1A_KEY(warehouse) :BKRM_PROC_XMAT_HDR/CONTROL_NBR(store)
    private Integer quantity;   // PO_LINE_VALUE.PACK_QTY
    private LocalDate receivingDate;//RPR_DATE
    private Integer receiveSequenceNumber;// RPR_SEQ_NBR
    private Double receivedWeightQuantity;//RECV_WEIGHT_QTY(Column)INITIAL_RECV_ITEM(Table)
    private String receivedQuantityUOMCode;//RECV_QTY_UOM_CODE(Column)INITIAL_RECV_ITEM(Table)
    private Integer inventoryMatchStatus;
    private String variableWeightIndicator;
    private Map<String, ReceiveMDSResponse> merchandises;
    private Map<String, WHLinePOLineValue> poLineValue;
    private Integer lineSequenceNumber;
    private String bottleDepositFlag;
    private String itemDescription;
    private Integer costMultiple;
    private Long purchaseOrderId;
    private String summaryReference;
    private String dataSyncStatus;
    private LocalDateTime creationTimestamp;
    private LocalDateTime lastUpdatedDate;

    public void merge(ReceivingLine receivingLine) {

        if(this.lastUpdatedDate.isAfter(receivingLine.lastUpdatedDate)) {

            // This block will only be executed if 'creationTimestamp' for 'this' object is after 'receivingLine'
            // Hence 'this' has the latest changes
            // We copy values from 'receivingLine' only if the corresponding value in 'this' is null/empty

            this.receiveId = StringUtils.isEmpty(this.receiveId) ?
                    receivingLine.receiveId : this.receiveId ;
            this.lineNumber = (this.lineNumber == null) ?
                    receivingLine.lineNumber : this.lineNumber;
            this.itemNumber = (this.itemNumber == null) ?
                    receivingLine.itemNumber : this.itemNumber;
            this.vendorNumber = (this.vendorNumber == null) ?
                    receivingLine.vendorNumber : this.vendorNumber;
            this.receivedQuantity = (this.receivedQuantity == null) ?
                    receivingLine.receivedQuantity : this.receivedQuantity;
            this.costAmount = (this.costAmount == null) ?
                    receivingLine.costAmount : this.costAmount;
            this.retailAmount = (this.retailAmount == null) ?
                    receivingLine.retailAmount : this.retailAmount;
            this.receivingControlNumber = StringUtils.isEmpty(this.receivingControlNumber) ?
                    receivingLine.receivingControlNumber : this.receivingControlNumber ;
            this.purchaseReceiptNumber = (this.purchaseReceiptNumber == null) ?
                    receivingLine.purchaseReceiptNumber : this.purchaseReceiptNumber;
            this.purchasedOrderId = (this.purchasedOrderId == null) ?
                    receivingLine.purchasedOrderId : this.purchasedOrderId;
            this.upcNumber = StringUtils.isEmpty(this.upcNumber) ?
                    receivingLine.upcNumber : this.upcNumber ;
            this.transactionType = (this.transactionType == null) ?
                    receivingLine.transactionType : this.transactionType;
            this.storeNumber = (this.storeNumber == null) ?
                    receivingLine.storeNumber : this.storeNumber;
            this.baseDivisionNumber = (this.baseDivisionNumber == null) ?
                    receivingLine.baseDivisionNumber : this.baseDivisionNumber;
            this.finalDate = ((this.finalDate == null)
                    || this.finalDate.isEqual(LocalDate.ofEpochDay(0))) ?
                    receivingLine.finalDate : this.finalDate;
            this.finalTime = (this.finalTime == null) ?
                    receivingLine.finalTime : this.finalTime;
            this.sequenceNumber = (this.sequenceNumber == null) ?
                    receivingLine.sequenceNumber : this.sequenceNumber;
            this.creationDate = (this.creationDate == null) ?
                    receivingLine.creationDate : this.creationDate;
            this.typeIndicator = (this.typeIndicator == null) ?
                    receivingLine.typeIndicator : this.typeIndicator;
            this.writeIndicator = StringUtils.isEmpty(this.writeIndicator) ?
                    receivingLine.writeIndicator : this.writeIndicator ;
            this.purchaseOrderNumber = StringUtils.isEmpty(this.purchaseOrderNumber) ?
                    receivingLine.purchaseOrderNumber : this.purchaseOrderNumber ;
            this.quantity = (this.quantity == null) ?
                    receivingLine.quantity : this.quantity;
            this.receivingDate = ((this.receivingDate == null)
                    || this.receivingDate.isEqual(LocalDate.ofEpochDay(0))) ?
                    receivingLine.receivingDate : this.receivingDate;
            this.receiveSequenceNumber = (this.receiveSequenceNumber == null) ?
                    receivingLine.receiveSequenceNumber : this.receiveSequenceNumber;
            this.receivedWeightQuantity = (this.receivedWeightQuantity == null) ?
                    receivingLine.receivedWeightQuantity : this.receivedWeightQuantity;
            this.receivedQuantityUOMCode = StringUtils.isEmpty(this.receivedQuantityUOMCode) ?
                    receivingLine.receivedQuantityUOMCode : this.receivedQuantityUOMCode;
            this.inventoryMatchStatus = (this.inventoryMatchStatus == null) ?
                    receivingLine.inventoryMatchStatus : this.inventoryMatchStatus;
            this.variableWeightIndicator = StringUtils.isEmpty(this.variableWeightIndicator) ?
                    receivingLine.variableWeightIndicator : this.variableWeightIndicator;
            this.merchandises = (this.merchandises == null
                    || receivingLine.merchandises.isEmpty()) ?
                    receivingLine.merchandises : this.merchandises;
            this.poLineValue = (this.poLineValue == null
                    || receivingLine.poLineValue.isEmpty()) ?
                    receivingLine.poLineValue : this.poLineValue;
            this.lineSequenceNumber = (this.lineSequenceNumber == null) ?
                    receivingLine.lineSequenceNumber : this.lineSequenceNumber;
            this.bottleDepositFlag = StringUtils.isEmpty(this.bottleDepositFlag) ?
                    receivingLine.bottleDepositFlag : this.bottleDepositFlag;
            this.itemDescription = StringUtils.isEmpty(this.itemDescription) ?
                    receivingLine.itemDescription : this.itemDescription;
            this.costMultiple = (this.costMultiple == null) ?
                    receivingLine.costMultiple : this.costMultiple;
            this.purchaseOrderId = (this.purchaseOrderId == null) ?
                    receivingLine.purchaseOrderId : this.purchaseOrderId;
            this.dataSyncStatus = StringUtils.isEmpty(this.dataSyncStatus) ?
                    receivingLine.dataSyncStatus : this.dataSyncStatus;

        } else {

            // This block will only be executed if 'creationTimestamp' for 'this' object is before 'receivingLine'
            // Hence 'receiveSummary' has the latest changes
            // We copy values from 'receivingLine' if the corresponding value in it is NOT null/empty

            this.receiveId = StringUtils.isNotEmpty(receivingLine.receiveId) ?
                    receivingLine.receiveId : this.receiveId ;
            this.lineNumber = (receivingLine.lineNumber != null) ?
                    receivingLine.lineNumber : this.lineNumber;
            this.itemNumber = (receivingLine.itemNumber != null) ?
                    receivingLine.itemNumber : this.itemNumber;
            this.vendorNumber = (receivingLine.vendorNumber != null) ?
                    receivingLine.vendorNumber : this.vendorNumber;
            this.receivedQuantity = (receivingLine.receivedQuantity != null) ?
                    receivingLine.receivedQuantity : this.receivedQuantity;
            this.costAmount = (receivingLine.costAmount != null) ?
                    receivingLine.costAmount : this.costAmount;
            this.retailAmount = (receivingLine.retailAmount != null) ?
                    receivingLine.retailAmount : this.retailAmount;
            this.receivingControlNumber = StringUtils.isNotEmpty(receivingLine.receivingControlNumber) ?
                    receivingLine.receivingControlNumber : this.receivingControlNumber ;
            this.purchaseReceiptNumber = (receivingLine.purchaseReceiptNumber != null) ?
                    receivingLine.purchaseReceiptNumber : this.purchaseReceiptNumber;
            this.purchasedOrderId = (receivingLine.purchasedOrderId != null) ?
                    receivingLine.purchasedOrderId : this.purchasedOrderId;
            this.upcNumber = StringUtils.isNotEmpty(receivingLine.upcNumber) ?
                    receivingLine.upcNumber : this.upcNumber ;
            this.transactionType = (receivingLine.transactionType != null) ?
                    receivingLine.transactionType : this.transactionType;
            this.storeNumber = (receivingLine.storeNumber != null) ?
                    receivingLine.storeNumber : this.storeNumber;
            this.baseDivisionNumber = (receivingLine.baseDivisionNumber != null) ?
                    receivingLine.baseDivisionNumber : this.baseDivisionNumber;
            this.finalDate = ((receivingLine.finalDate != null)
                    && receivingLine.finalDate.isAfter(LocalDate.ofEpochDay(0))) ?
                    receivingLine.finalDate : this.finalDate;
            this.finalTime = (receivingLine.finalTime != null) ?
                    receivingLine.finalTime : this.finalTime;
            this.sequenceNumber = (receivingLine.sequenceNumber != null) ?
                    receivingLine.sequenceNumber : this.sequenceNumber;
            this.creationDate = (receivingLine.creationDate != null) ?
                    receivingLine.creationDate : this.creationDate;
            this.typeIndicator = (receivingLine.typeIndicator != null) ?
                    receivingLine.typeIndicator : this.typeIndicator;
            this.writeIndicator = StringUtils.isNotEmpty(receivingLine.writeIndicator) ?
                    receivingLine.writeIndicator : this.writeIndicator ;
            this.purchaseOrderNumber = StringUtils.isNotEmpty(receivingLine.purchaseOrderNumber) ?
                    receivingLine.purchaseOrderNumber : this.purchaseOrderNumber ;
            this.quantity = (receivingLine.quantity != null) ?
                    receivingLine.quantity : this.quantity;
            this.receivingDate = ((receivingLine.receivingDate != null)
                    && receivingLine.receivingDate.isAfter(LocalDate.ofEpochDay(0))) ?
                    receivingLine.receivingDate : this.receivingDate;
            this.receiveSequenceNumber = (receivingLine.receiveSequenceNumber != null) ?
                    receivingLine.receiveSequenceNumber : this.receiveSequenceNumber;
            this.receivedWeightQuantity = (receivingLine.receivedWeightQuantity != null) ?
                    receivingLine.receivedWeightQuantity : this.receivedWeightQuantity;
            this.receivedQuantityUOMCode = StringUtils.isNotEmpty(receivingLine.receivedQuantityUOMCode) ?
                    receivingLine.receivedQuantityUOMCode : this.receivedQuantityUOMCode ;
            this.inventoryMatchStatus = (receivingLine.inventoryMatchStatus != null) ?
                    receivingLine.inventoryMatchStatus : this.inventoryMatchStatus;
            this.variableWeightIndicator = StringUtils.isNotEmpty(receivingLine.variableWeightIndicator) ?
                    receivingLine.variableWeightIndicator : this.variableWeightIndicator;
            this.merchandises = (receivingLine.merchandises != null
                    && !receivingLine.merchandises.isEmpty()) ?
                    receivingLine.merchandises : this.merchandises;
            this.poLineValue = (receivingLine.poLineValue != null
                    && !receivingLine.poLineValue.isEmpty()) ?
                    receivingLine.poLineValue : this.poLineValue;
            this.lineSequenceNumber = (receivingLine.lineSequenceNumber != null) ?
                    receivingLine.lineSequenceNumber : this.lineSequenceNumber;
            this.bottleDepositFlag = StringUtils.isNotEmpty(receivingLine.bottleDepositFlag) ?
                    receivingLine.bottleDepositFlag : this.bottleDepositFlag;
            this.itemDescription = StringUtils.isNotEmpty(receivingLine.itemDescription) ?
                    receivingLine.itemDescription : this.itemDescription;
            this.costMultiple = (receivingLine.costMultiple != null) ?
                    receivingLine.costMultiple : this.costMultiple;
            this.purchaseOrderId = (receivingLine.purchaseOrderId != null) ?
                    receivingLine.purchaseOrderId : this.purchaseOrderId;
            this.dataSyncStatus = StringUtils.isNotEmpty(receivingLine.dataSyncStatus) ?
                    receivingLine.dataSyncStatus : this.dataSyncStatus;
            this.lastUpdatedDate = receivingLine.lastUpdatedDate;

        }

    }
}