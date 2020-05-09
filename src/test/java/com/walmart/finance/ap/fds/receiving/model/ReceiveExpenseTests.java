package com.walmart.finance.ap.fds.receiving.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest(ReceiveExpense.class)
@RunWith(PowerMockRunner.class)
public class ReceiveExpenseTests {

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testModel() {

        ReceiveExpense receiveExpense = new ReceiveExpense();
        receiveExpense.setExpenseTypeCode(1234);
        receiveExpense.setActualExpenseValue(Double.valueOf("1234"));
        receiveExpense.setBusinessStatusCode('1');
        receiveExpense.setDealID(1234);
        receiveExpense.setExpenseValueFormat('e');
        receiveExpense.setPaymentMethodCode('r');
        receiveExpense.setPaymentFrequencyCode(123);
        receiveExpense.setRemitCompanyID(1234);

        Assert.assertNotNull(receiveExpense.getExpenseTypeCode());
        Assert.assertNotNull(receiveExpense.getActualExpenseValue());
        Assert.assertNotNull(receiveExpense.getBusinessStatusCode());
        Assert.assertNotNull(receiveExpense.getDealID());
        Assert.assertNotNull(receiveExpense.getExpenseValueFormat());
        Assert.assertNotNull(receiveExpense.getPaymentMethodCode());
        Assert.assertNotNull(receiveExpense.getPaymentFrequencyCode());
        Assert.assertNotNull(receiveExpense.getRemitCompanyID());
    }

}
