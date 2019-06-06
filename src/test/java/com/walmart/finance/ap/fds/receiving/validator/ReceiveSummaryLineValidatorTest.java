package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.integrations.VendorIntegrationServiceImpl;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
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
@WebMvcTest(ReceiveSummaryLineValidator.class)
public class ReceiveSummaryLineValidatorTest {


    @Mock
    VendorIntegrationServiceImpl vendorIntegrationService;

    @InjectMocks
    ReceiveSummaryLineValidator receiveSummaryLineValidator;

    String countryCode = "US";

    ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest(65267L, 33383L, 99, "56HKKL",
            0, 0, LocalDate.now(), LocalTime.now(), 0, 122663, 0, 98, 0, 8.9, 8.7,
            0, "A",
            8897L, 'A', 'N', 'L', LocalDate.now(), 22.0, 0, 0, 0,
            LocalDateTime.of(1990, 12, 12, 18, 56, 22), 9,
            "UUU", "user", "purchase", 11.0, "hyhh", LocalDateTime.of(1998, 12, 12, 18, 56, 22),
            LocalDateTime.of(2000, 12, 12, 18, 56, 22),
            "988", 2222,
            2228, "bbb", 7665, 0, 0, 11.8, 22.9, 0, 0, 0,0,null);

    @Test
    public void validateVendorNumberUpdateSummaryTest() {
        Mockito.when(vendorIntegrationService.getVendorBySupplierNumberAndCountryCode(receivingSummaryLineRequest.getVendorNumber(), countryCode)).thenReturn(receivingSummaryLineRequest.getVendorNumber());
        Assert.assertTrue(receiveSummaryLineValidator.validateVendorNumberUpdateSummary(receivingSummaryLineRequest, receivingSummaryLineRequest.getVendorNumber(), countryCode));
    }

    @Test
    public void validateBusinessStatUpdateSummary() {
        Assert.assertTrue(receiveSummaryLineValidator.validateBusinessStatUpdateSummary(receivingSummaryLineRequest));
    }

    @Test
    public void validateControlTypeTest() {
        Assert.assertTrue(receiveSummaryLineValidator.validateControlType(receivingSummaryLineRequest));
    }
}

