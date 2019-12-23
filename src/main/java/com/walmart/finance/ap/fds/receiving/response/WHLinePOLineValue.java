package com.walmart.finance.ap.fds.receiving.response;

import lombok.*;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WHLinePOLineValue {

    private String uomCode;
    private Integer quantity;
    private Double costAmt;
    private Double retailAmt;

}
