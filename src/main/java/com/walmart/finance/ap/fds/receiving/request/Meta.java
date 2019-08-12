package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

    String unitofWorkid;

    @Valid
    @NotNull(message = "Missing mandatory parameter,please enter a valid sorRoutingCtx")
    SorRoutingCtx sorRoutingCtx;
}
