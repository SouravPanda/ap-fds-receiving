package com.walmart.finance.ap.fds.receiving.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryPayload {

    private String receiveId;

    private String purchaseOrderId;

    private LocalDate receiveDate;

    private Integer locationNumber;

    private String businessStatusCode;
}
