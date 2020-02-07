package com.walmart.finance.ap.fds.receiving.factory;

import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
public class BottleDepositWarehouseTest {

    @InjectMocks
    BottleDepositWarehouse bottleDepositWarehouse;

    @Test
    public void getBottleDepositAmountForWarehouse() {

        ReceivingLine receivingLine1 = new ReceivingLine();
        ReceivingLine receivingLine2 = new ReceivingLine();
        ReceivingLine receivingLine3 = new ReceivingLine();

        receivingLine1.setBottleDepositFlag("Y");
        receivingLine1.setCostAmount(12.2);
        receivingLine1.setQuantity(1);

        receivingLine2.setBottleDepositFlag("N");
        receivingLine2.setCostAmount(15.0);
        receivingLine2.setQuantity(1);

        receivingLine3.setBottleDepositFlag("Y");
        receivingLine3.setCostAmount(12.2);
        receivingLine3.setQuantity(0);

        List<ReceivingLine> receivingLines = new ArrayList<>();
        receivingLines.add(receivingLine1);
        receivingLines.add(receivingLine2);
        receivingLines.add(receivingLine3);

        Assert.assertEquals(bottleDepositWarehouse.getBottleDepositAmount(receivingLines), java.util.Optional.of(12.2).get());
    }
}