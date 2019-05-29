package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.integrations.VendorIntegrationServiceImpl;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummarySearch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.time.LocalDateTime;

@RunWith(PowerMockRunner.class)
@WebMvcTest(ReceiveSummaryValidator.class)
public class ReceiveSummaryValidatorTest {

    @Mock
    VendorIntegrationServiceImpl vendorIntegrationService;

    @InjectMocks
    ReceiveSummaryValidator receiveSummaryValidator;

    String countryCode="US";
    ReceivingSummarySearch receivingSummarySearch = new ReceivingSummarySearch(65267L, 33383L, "56HKKL", 0, "0",
            8897, 99, 122663, 997, 999L, "kkk",
            LocalDateTime.of(1990, 12, 12, 18, 56, 22),
            LocalDateTime.of(1991, 12, 12, 18, 56, 22),
            "A", 11.0, 11.9, 988, 2222, 2228, 7665,
            'A', 11.8, 22.9, 90, 'A', 'B', 'C', 88.0,
            44, 49, "hh", 'J', "99");

    @Test
    public void validateVendorNumberUpdateSummaryTest(){
        Mockito.when(vendorIntegrationService.getVendorBySupplierNumberAndCountryCode(receivingSummarySearch.getVendorNumber(), countryCode)).thenReturn(receivingSummarySearch.getVendorNumber());
        Assert.assertTrue(receiveSummaryValidator.validateVendorNumberUpdateSummary(receivingSummarySearch,receivingSummarySearch.getVendorNumber(),countryCode));
    }

    @Test
    public void validateBusinessStatUpdateSummary(){
        Assert.assertTrue(receiveSummaryValidator.validateBusinessStatUpdateSummary(receivingSummarySearch));
    }

    @Test
    public void validateControlTypeTest(){
        Assert.assertTrue(receiveSummaryValidator.validateControlType(receivingSummarySearch));
    }
}
