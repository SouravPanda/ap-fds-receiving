package com.walmart.finance.ap.fds.receiving.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDateTime;

@PrepareForTest(ReceiveSummary.class)
@RunWith(PowerMockRunner.class)
public class ReceiveSummaryTests {

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMergeAAfterB() {

        ReceiveSummary receiveSummaryB = new ReceiveSummary();
        receiveSummaryB.setDateReceived(LocalDateTime.now());
        receiveSummaryB.setReceivingControlNumber("OlderControlNumber");
        receiveSummaryB.setBaseDivisionNumber(10);

        ReceiveSummary receiveSummaryA = new ReceiveSummary();
        receiveSummaryA.setDateReceived(LocalDateTime.now().plusDays(1));
        receiveSummaryA.setReceivingControlNumber("NewControlNumber");
        //baseDivisionNumber is not set originally in receiveSummaryA

        receiveSummaryA.merge(receiveSummaryB);
        Assert.assertEquals(receiveSummaryA.getReceivingControlNumber(), "NewControlNumber");
        Assert.assertTrue(receiveSummaryA.getBaseDivisionNumber() == 10);


    }

    @Test
    public void testMergeBAfterA() {

        ReceiveSummary receiveSummaryA = new ReceiveSummary();
        receiveSummaryA.setDateReceived(LocalDateTime.now());
        receiveSummaryA.setReceivingControlNumber("OriginalControlNumber");
        //baseDivisionNumber is not set originally in receiveSummaryA

        ReceiveSummary receiveSummaryB = new ReceiveSummary();
        receiveSummaryB.setDateReceived(LocalDateTime.now().plusDays(1));
        receiveSummaryB.setReceivingControlNumber("NewControlNumber");
        receiveSummaryB.setBaseDivisionNumber(10);

        receiveSummaryA.merge(receiveSummaryB);
        Assert.assertEquals(receiveSummaryA.getReceivingControlNumber(), "NewControlNumber");
        Assert.assertTrue(receiveSummaryA.getBaseDivisionNumber() == 10);


    }

}
