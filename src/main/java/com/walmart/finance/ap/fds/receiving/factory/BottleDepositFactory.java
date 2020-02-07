package com.walmart.finance.ap.fds.receiving.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BottleDepositFactory {

    @Autowired
    BottleDepositStore bottleDepositStore;

    @Autowired
    BottleDepositWarehouse bottleDepositWarehouse;

    public BottleDeposit getBottleDeposit(Character typeIndicator){
        if(typeIndicator.equals('S')){
            return bottleDepositStore;
        }else{
            return bottleDepositWarehouse;
        }
    }
}
