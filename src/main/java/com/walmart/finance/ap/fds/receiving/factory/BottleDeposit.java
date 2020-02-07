package com.walmart.finance.ap.fds.receiving.factory;

import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;

import java.util.List;

public interface BottleDeposit {
    public Double getBottleDepositAmount(List<ReceivingLine> receivingLines);
}
