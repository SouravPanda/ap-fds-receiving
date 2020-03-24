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
    JUNKPARAMS("The given query params are not valid"),
    INVALIDQUERYPARAMS("please enter valid query parameters"),
    INVALIDDATATYPE("Data Type is invalid for input values"),
    INVALIDLINESEQUENCE("Invalid value, lineSequenceNumber"),
    INVALIDLINESEQUENCEDETAILS("it should be in an integer"),
    RECEIVINGINFO("Receipt Line is not matching for the upc numbers or item numbers"),
    FREIGHTIDDETAILS(", it should be a number"),
    CONTENTNOTFOUNDFREIGHT("Freight details not found for the given id: ");

    @Getter
    private String parameterName;
}
