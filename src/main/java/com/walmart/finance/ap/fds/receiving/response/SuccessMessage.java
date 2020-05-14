package com.walmart.finance.ap.fds.receiving.response;

import com.walmart.finance.ap.fds.receiving.request.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessMessage {


    private Meta meta;

    private Boolean success;

    private String objectName;

    private long messageTimeStamp;

    private String operation;

    private String domain;

    private String _id;

    private String partitionKey;

    private Object payload;
}
