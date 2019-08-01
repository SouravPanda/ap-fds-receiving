package com.walmart.finance.ap.fds.receiving.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private Integer itemNumber;     //BKRM_PROC_XMIT_DTL.ITEM_NBR
    private Integer vendorNumber;   //BKRM_PROC_XMIT_HDR.ORIG_DEST_ID
    private Integer receivedQuantity;   //BKRM_PROC_XMIT_DTL.ITEM_QTY
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
    private String receivedQuantityUnitOfMeasureCode;//RECV_QTY_UOM_CODE(Column)INITIAL_RECV_ITEM(Table)
    private Integer inventoryMatchStatus;
    private String variableWeightIndicator;
    private String merchandises;
    private Integer lineSequenceNumber;
    private String bottleDepositFlag;
    private String itemDescription;
    private Integer costMultiple;
    private Long purchaseOrderId;
    private String summaryReference;
    private String dataSyncStatus;
    private LocalDateTime creationTimestamp;
    private LocalDateTime lastUpdatedDate;
}