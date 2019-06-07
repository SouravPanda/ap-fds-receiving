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

    ReceivingSummaryLineRequest receivingSummaryLineRequest = new ReceivingSummaryLineRequest();

/*    @Test
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
    }*/
}

