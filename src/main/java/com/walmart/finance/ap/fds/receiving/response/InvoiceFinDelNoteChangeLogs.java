package com.walmart.finance.ap.fds.receiving.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InvoiceFinDelNoteChangeLogs {
    private LocalDate changeTimestamp;

    private String changeUserId;

    private String deliveryNoteId;

    private String orgDelNoteId;
}
