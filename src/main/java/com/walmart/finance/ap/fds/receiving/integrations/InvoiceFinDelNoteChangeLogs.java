package com.walmart.finance.ap.fds.receiving.integrations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InvoiceFinDelNoteChangeLogs {
    private Date changeTimestamp;

    private String changeUserId;

    private String deliveryNoteId;

    private String orgDelNoteId;
}
