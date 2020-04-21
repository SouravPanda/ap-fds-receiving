package com.walmart.finance.ap.fds.receiving.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest(ReceiveMDS.class)
@RunWith(PowerMockRunner.class)
public class ReceiveMDSTests {

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testModel() {

        ReceiveMDS receiveMDS = new ReceiveMDS();
        receiveMDS.setMdseConditionCode(1234);
        receiveMDS.setMdseQuantity(12345);
        receiveMDS.setMdseQuantityUnitOfMeasureCode('1');

        Assert.assertNotNull(receiveMDS.getMdseConditionCode());
        Assert.assertNotNull(receiveMDS.getMdseQuantity());
        Assert.assertNotNull(receiveMDS.getMdseQuantityUnitOfMeasureCode());
    }

}
