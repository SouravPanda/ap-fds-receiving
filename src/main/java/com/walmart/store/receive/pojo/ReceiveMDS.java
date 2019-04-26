package com.walmart.store.receive.pojo;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ReceiveMDS {
    private int mdseConditionCode;
    private Integer mdseQuantity;
    private char mdseQuantityUnitOfMeasureCode;
}
