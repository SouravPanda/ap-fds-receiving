package com.walmart.store.receive.walmart;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "receive-summary")
public class Store {

       // private ObjectId _id;
        @Id
        private String _id;
        private Integer poReceiveId;
        private Integer purchaseOrderId;
        private char transactionType;
        private String controlNumber;
        private Integer destinationBusinessUnitId;
        private Integer baseDivisionNumber;
        private Date finalDate;
        private Time finalTime;
        private char businessStatusCode;
        private BigInteger freightBillExpandId;
        private Timestamp initialReceiveTimestamp;
        private Date mdsReceiveDate;
        private Date receiveProcessDate;
        private char sequenceNumber;
        private Integer mdseConditionCode;
        private Integer vendorNumber;
        private Date loadTimestamp;
        private char mdseQuantityUnitOfMeasureCode;
}

