package com.walmart.finance.ap.fds.receiving.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReceiveMDSResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer mdseDisplayCode;

    private Integer mdseConditionCode;
    private Long mdseQuantity;
    private String mdseQuantityUnitOfMeasureCode;
}
