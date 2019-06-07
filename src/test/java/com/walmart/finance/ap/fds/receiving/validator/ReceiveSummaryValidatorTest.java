package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.integrations.VendorIntegrationServiceImpl;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RunWith(PowerMockRunner.class)
@WebMvcTest(ReceiveSummaryValidator.class)
public class ReceiveSummaryValidatorTest {

    @Mock
    VendorIntegrationServiceImpl vendorIntegrationService;

    @InjectMocks
    ReceiveSummaryValidator receiveSummaryValidator;

    String countryCode="US";
    ReceivingSummaryRequest receivingSummaryRequest = new ReceivingSummaryRequest(65267L, 33383L, "56HKKL", 0, "0",
            8897, 99, 122663, 997, 999L, "kkk",LocalDate.now(),
            LocalDateTime.of(1990, 12, 12, 18, 56, 22),
            LocalTime.now(),
            LocalDateTime.of(1991, 12, 12, 18, 56, 22),
            "A", 11.0, 11.9, 0, 2222, 2228, 7665,
            'A', 11.8, 22.9, 90, 'A', 'B', 'C', 88.0,
            44, 49, "hh", 'J', "99",null);

    @Test
    public void validateVendorNumberUpdateSummaryTest(){
        Mockito.when(vendorIntegrationService.getVendorBySupplierNumberAndCountryCode(receivingSummaryRequest.getVendorNumber(), countryCode)).thenReturn(receivingSummaryRequest.getVendorNumber());
        Assert.assertTrue(receiveSummaryValidator.validateVendorNumberUpdateSummary(receivingSummaryRequest,receivingSummaryRequest.getVendorNumber(),countryCode));
    }

    @Test
    public void validateBusinessStatUpdateSummary(){
        Assert.assertTrue(receiveSummaryValidator.validateBusinessStatUpdateSummary(receivingSummaryRequest));
    }

    @Test
    public void validateControlTypeTest(){
        Assert.assertTrue(receiveSummaryValidator.validateControlType(receivingSummaryRequest));
    }
}
