package com.walmart.finance.ap.fds.receiving.factory;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
public class SummarySummaryBottleDepositFactoryTest {

    @InjectMocks
    SummaryBottleDepositFactory summaryBottleDepositFactory;

    @Mock
    SummaryBottleDepositStore bottleDepositStore;

    @Mock
    SummaryBottleDepositWarehouse bottleDepositWarehouse;

    @Test
    public void getBottleDepositFactoryTest() {
        Assert.assertTrue(summaryBottleDepositFactory.getBottleDeposit("S") instanceof SummaryBottleDepositStore);
        Assert.assertTrue(summaryBottleDepositFactory.getBottleDeposit("S") instanceof SummaryBottleDepositWarehouse);
    }
}