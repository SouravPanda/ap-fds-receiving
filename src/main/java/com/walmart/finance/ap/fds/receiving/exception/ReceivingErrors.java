package com.walmart.finance.ap.fds.receiving.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ReceivingErrors {

    CONTENTNOTFOUNDSUMMARY("Receive summary not found for the given id"),
    CONTENTNOTFOUNDLINE("Receive line not found for the given id"),
    VALIDID("please enter a valid id"),
    INVALIDBUSINESSSTATUSCODE("Value of field  businessStatusCode passed is not valid"),
    BUSINESSSTATUSDETAILS("it should be one among A,C,D,I,M,X,Z"),
    INVALIDINVENTORYMATCHSTATUSCODE("Invalid value, inventoryMatchStatus"),
    INVALIDINVENTORYMATCHSTATUSDETAILS("it should be in range 0-9"),
    RECEIVELINENOTFOUND("Receiving line not found for given search criteria"),
    INVALIDQUERYPARAMS("please enter valid query parameters"),
    INVALIDDATATYPE("Data Type is invalid for input values");

    @Getter
    private String parameterName;
}
