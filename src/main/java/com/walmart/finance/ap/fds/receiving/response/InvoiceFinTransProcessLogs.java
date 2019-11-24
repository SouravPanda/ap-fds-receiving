package com.walmart.finance.ap.fds.receiving.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class InvoiceFinTransProcessLogs {

    private String actionIndicator;

    private String memoComments;

    private Integer processStatusCode;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate processStatusTimestamp;

    private String statusUserId;
}
