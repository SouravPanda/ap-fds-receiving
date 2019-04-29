package com.walmart.finance.ap.fds.receiving.model;

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
