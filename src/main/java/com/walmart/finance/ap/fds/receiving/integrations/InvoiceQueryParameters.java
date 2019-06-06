package com.walmart.finance.ap.fds.receiving.integrations;

// Removed CountryCode from this as it got set as path param.

//TODO Removed "vendorNumber" as of noe due to the dilemma of 9 digits and 6 digits
// Constants in capitals are not available.
public enum InvoiceQueryParameters {
    invoiceId, invoiceNumber, locationNumber, purchaseOrderNumber, purchaseOrderId, divisionNumber, departmentNumber, controlNumber, LOCATIONTYPE, STORENUMBER, TOTALAMOUNT, INVDATE, INVOICEDATESTART, INVOICEDATEEND
}