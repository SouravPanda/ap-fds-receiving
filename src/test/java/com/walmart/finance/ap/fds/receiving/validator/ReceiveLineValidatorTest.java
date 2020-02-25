package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.model.ReceiveLineRequestParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

@PrepareForTest(ReceiveSummaryValidator.class)
@RunWith(PowerMockRunner.class)
public class ReceiveLineValidatorTest {

    @InjectMocks
    ReceiveLineValidator receiveLineValidator;

    @Test
    public void validateLineArguments() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingConstants.PURCHASEORDERID, "111");
        allRequestParams.put(ReceivingConstants.TRANSACTIONTYPE, "99");
        allRequestParams.put(ReceivingConstants.LOCATIONNUMBER, "113");
        allRequestParams.put(ReceivingConstants.RECEIPTNUMBER, "555");
        allRequestParams.put(ReceivingConstants.CONTROLNUMBER, "999");
        allRequestParams.put(ReceivingConstants.DIVISIONNUMBER, "88");
        allRequestParams.put(ReceiveLineRequestParams.ORDERBY.getParameterName(),"");
        allRequestParams.put(ReceiveLineRequestParams.ORDER.getParameterName(),"");
        allRequestParams.put(ReceiveLineRequestParams.PAGENBR.getParameterName(),"");
        allRequestParams.put(ReceiveLineRequestParams.PAGESIZE.getParameterName(),"");
        ReceiveLineValidator.validate("US", allRequestParams);
    }

    @Test(expected = BadRequestException.class)
    public void validateInvalidLineArguments() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingConstants.PURCHASEORDERID, "111");
        allRequestParams.put(ReceivingConstants.TRANSACTIONTYPE, "99");
        allRequestParams.put(ReceivingConstants.SM_WM_ENV, "113");
        ReceiveLineValidator.validate("US", allRequestParams);
    }

    @Test
    public void validateEmptyLineArguments() {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(ReceivingConstants.PURCHASEORDERID, null);
        allRequestParams.put(ReceivingConstants.TRANSACTIONTYPE, "99");
        allRequestParams.put(ReceivingConstants.LOCATIONNUMBER, "113");
        allRequestParams.put(ReceivingConstants.RECEIPTNUMBER, "555");
        allRequestParams.put(ReceivingConstants.CONTROLNUMBER, "999");
        allRequestParams.put(ReceivingConstants.DIVISIONNUMBER, "00");
        ReceiveLineValidator.validate("US", allRequestParams);
    }
}
