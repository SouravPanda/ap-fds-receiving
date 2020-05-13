package com.walmart.finance.ap.fds.receiving.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Details {
    String ref;
    String type;
    String code;
    String description;
    String additionalInfo;
}

