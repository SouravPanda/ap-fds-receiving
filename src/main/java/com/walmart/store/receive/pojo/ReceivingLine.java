package com.walmart.store.receive.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Document(collection="receive-line")
public class ReceivingLine {
    private int purchaseOrderReceiveID;
    private Integer lineNumber;
    private Integer itemNumber;
    private Integer vendorNumber;
    private Integer receivedQuantity;
    private Double costAmount;
    private Double retailAmount;
    private Double packQuantity;
    private int numberOfCasesReceived;
    private Integer vendorStockNumber;
    private Integer bottleStockNumber;
    private int damaged;
    private Integer receivingControlNumber;
    private Integer purchaseReceiptNumber;
    private Integer upcNumber;
    private String itemDescription;
    private Double unitOfMeasure;
    private Double variableWeightInd;
    private Integer receivedWeightQuantity;
    private char transactionType;
    private Integer storeNumber;
    private Integer baseDivisionNumber;
    private String finalDate;
    private String finalTime;
    private Integer sequenceNumber;

}
