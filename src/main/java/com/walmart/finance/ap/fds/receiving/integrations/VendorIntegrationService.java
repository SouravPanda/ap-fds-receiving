package com.walmart.finance.ap.fds.receiving.integrations;


public interface VendorIntegrationService {
    Integer getVendorBySupplierNumberAndCountryCode(Integer supplierNumber, String countryCode);
}
