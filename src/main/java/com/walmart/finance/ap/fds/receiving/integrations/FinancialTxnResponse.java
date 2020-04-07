package com.walmart.finance.ap.fds.receiving.integrations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FinancialTxnResponse {

    @JsonProperty("data")
    private Object financialTxnResponseData;
}
