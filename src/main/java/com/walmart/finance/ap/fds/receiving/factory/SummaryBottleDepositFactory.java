package com.walmart.finance.ap.fds.receiving.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SummaryBottleDepositFactory {

    @Autowired
    SummaryBottleDepositStore bottleDepositStore;

    @Autowired
    SummaryBottleDepositWarehouse bottleDepositWarehouse;

    public SummaryBottleDeposit getBottleDeposit(String typeIndicator){
        if(typeIndicator.equals("S")){
            return bottleDepositStore;
        }else{
            return bottleDepositWarehouse;
        }
    }
}
