package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.common.ReceiveSummaryBusinessStat;
import com.walmart.finance.ap.fds.receiving.integrations.VendorIntegrationServiceImpl;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ReceiveSummaryLineValidator {

    @Autowired
    VendorIntegrationServiceImpl vendorIntegrationService;


    List<ReceiveSummaryBusinessStat> businessStatList = Arrays.asList(ReceiveSummaryBusinessStat.values());
    boolean verdict = false;

    public boolean validateVendorNumberUpdateSummary(ReceivingSummaryLineRequest receivingSummaryLineRequest, Integer vendorNumber, String countryCode) {
        if (vendorIntegrationService.getVendorBySupplierNumberAndCountryCode(vendorNumber, countryCode).equals(receivingSummaryLineRequest.getVendorNumber())) {
            verdict=true;
        }
        return verdict;
    }

    public boolean validateBusinessStatUpdateSummary(ReceivingSummaryLineRequest receivingSummaryLineRequest) {
        for (ReceiveSummaryBusinessStat businessStat : businessStatList) {
            if (businessStat.toString().equalsIgnoreCase(receivingSummaryLineRequest.getBusinessStatusCode())) {
                verdict = true;
                break;
            }
        }
        return verdict;

    }

    public boolean validateControlType(ReceivingSummaryLineRequest receivingSummaryLineSearch) {
        Set<Integer> controlNumberSet = new HashSet<>();
        controlNumberSet.add(0);
        controlNumberSet.add(1);
        controlNumberSet.add(2);
        controlNumberSet.add(3);
        controlNumberSet.add(99);
        if (controlNumberSet.contains(receivingSummaryLineSearch.getControlType())) {
            verdict = true;
        }
        return verdict;
    }

}



