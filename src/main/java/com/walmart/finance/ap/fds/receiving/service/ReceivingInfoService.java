package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;

import java.util.List;

/**
 * Service layer interface for receiving info API.
 */
public interface ReceivingInfoService {

    ReceivingResponse getSevice(String code, String invoiceId, String invoiceNumber, String purchaseOrderNumber, String purchaseOrderId, List<String> receiptNumbers, String transactionType, String controlNumber, String locationNumber, String divisionNumber, String vendorNumber, String departmentNumber, List<String> itemNumbers, List<String> upcNumbers, String receiptDateStart, String receiptDateEnd, String countryCode);
}
