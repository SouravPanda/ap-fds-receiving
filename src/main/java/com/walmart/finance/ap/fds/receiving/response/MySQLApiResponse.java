package com.walmart.finance.ap.fds.receiving.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MySQLApiResponse {
    @JsonProperty("data")
    private Object mySQLApiResponseData;
}
