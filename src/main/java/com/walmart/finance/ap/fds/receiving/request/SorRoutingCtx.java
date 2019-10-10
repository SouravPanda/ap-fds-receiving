package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SorRoutingCtx {

    @NotEmpty(message = "Missing mandatory parameter,please enter a valid replnTypCd")
    String replnTypCd;

    @NotNull(message = "Missing mandatory parameter,please enter a valid invProcAreaCode")
    Integer invProcAreaCode;

    @NotEmpty(message = "Missing mandatory parameter,please enter a valid locationCountryCd")
    String locationCountryCd;
}