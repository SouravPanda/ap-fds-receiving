package com.walmart.finance.ap.fds.receiving.integrations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FreightResponse {

    @JsonProperty("carrierCode")
    private String carrierCode;

    @JsonProperty("trailerNbr")
    private String trailerNumber;


}
