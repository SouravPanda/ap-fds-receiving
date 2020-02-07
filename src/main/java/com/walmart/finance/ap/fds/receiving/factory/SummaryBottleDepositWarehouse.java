package com.walmart.finance.ap.fds.receiving.factory;

import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class SummaryBottleDepositWarehouse implements SummaryBottleDeposit {

    @Override
    public Double getBottleDepositAmount(List<ReceivingLine> receivingLines){

        BigDecimal bottleDepositAmount = BigDecimal.valueOf(0.0);
        for (ReceivingLine receivingLine : receivingLines) {
            // Flag logic needs to be changed
            if(receivingLine.getBottleDepositFlag().equals("Y") && receivingLine.getCostAmount() != null && receivingLine.getQuantity() != null && !receivingLine.getQuantity().equals(0)) {
                Double costAmount = receivingLine.getCostAmount();
                Integer vendorPackQuantity = receivingLine.getQuantity();
                bottleDepositAmount = bottleDepositAmount.add(BigDecimal.valueOf((costAmount / Double.valueOf(vendorPackQuantity))));
            }
        }
        return bottleDepositAmount.doubleValue();

    }
}
