package com.walmart.finance.ap.fds.receiving.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.walmart.finance.ap.fds.receiving.deserializer.LocalDateDeserializer;
import com.walmart.finance.ap.fds.receiving.serializer.LocalDateSerializer;
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
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate changeTimestamp;

    private String changeUserId;

    private String deliveryNoteId;

    private String orgDelNoteId;
}
