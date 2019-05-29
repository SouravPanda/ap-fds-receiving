package com.walmart.finance.ap.fds.receiving.integrations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdditionalResponse {

    private String trailerNumber;
    private String carrierCode;
    private Long lineCount;
    private Double totalCostAmount;
    private Double totalRetailAmount;
}
