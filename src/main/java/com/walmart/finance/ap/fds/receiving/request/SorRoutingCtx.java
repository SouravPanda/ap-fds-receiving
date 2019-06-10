package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SorRoutingCtx {

    String repLnTypCd;

    Integer invProcAreaCode;

    String locationCountryCd;
}