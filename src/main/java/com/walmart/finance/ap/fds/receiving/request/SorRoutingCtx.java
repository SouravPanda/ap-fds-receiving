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

    @NotEmpty
    String replnTypCd;

    @NotNull
    Integer invProcAreaCode;

    @NotEmpty
    String locationCountryCd;
}