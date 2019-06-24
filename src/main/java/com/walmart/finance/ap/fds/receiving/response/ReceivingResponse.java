package com.walmart.finance.ap.fds.receiving.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceivingResponse {
    private boolean success;
    private LocalDateTime timestamp;
    private List data;
}
