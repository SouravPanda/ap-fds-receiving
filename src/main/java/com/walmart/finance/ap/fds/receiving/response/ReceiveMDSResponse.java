package com.walmart.finance.ap.fds.receiving.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReceiveMDSResponse {
    private Integer mdseConditionCode;
    private Integer mdseQuantity;
    private String mdseQuantityUnitOfMeasureCode;
}
