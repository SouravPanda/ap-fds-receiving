package com.walmart.finance.ap.fds.receiving.factory;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
public class BottleDepositFactoryTest {

    @InjectMocks
    BottleDepositFactory bottleDepositFactory;

    @Mock
    BottleDepositStore bottleDepositStore;

    @Mock
    BottleDepositWarehouse bottleDepositWarehouse;

    @Test
    public void getBottleDepositFactoryTest() {
        Assert.assertTrue(bottleDepositFactory.getBottleDeposit('S') instanceof BottleDepositStore);
        Assert.assertTrue(bottleDepositFactory.getBottleDeposit('W') instanceof BottleDepositWarehouse);
    }
}