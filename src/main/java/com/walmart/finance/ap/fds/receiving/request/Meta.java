package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

    String unitofWorkid;

    @Valid
    SorRoutingCtx sorRoutingCtx;


}
