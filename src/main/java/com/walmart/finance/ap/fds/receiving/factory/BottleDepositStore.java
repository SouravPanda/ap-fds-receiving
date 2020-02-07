package com.walmart.finance.ap.fds.receiving.factory;

import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class BottleDepositStore implements BottleDeposit{

    @Override
    public Double getBottleDepositAmount(List<ReceivingLine> receivingLines){

        BigDecimal bottleDepositAmount = BigDecimal.valueOf(0.0);
        for (ReceivingLine receivingLine : receivingLines) {
            if(receivingLine.getBottleDepositFlag().equals("Y") && receivingLine.getCostAmount() != null && receivingLine.getCostMultiple() != null && !receivingLine.getCostMultiple().equals(0)) {
                Double costAmount = receivingLine.getCostAmount();
                Integer costMultiplier = receivingLine.getCostMultiple();
                bottleDepositAmount = bottleDepositAmount.add(BigDecimal.valueOf((costAmount / Double.valueOf(costMultiplier))));
            }
        }
        return bottleDepositAmount.doubleValue();

    }
}
