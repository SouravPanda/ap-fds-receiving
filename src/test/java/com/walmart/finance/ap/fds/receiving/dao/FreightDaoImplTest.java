package com.walmart.finance.ap.fds.receiving.dao;

import com.walmart.finance.ap.fds.receiving.model.Freight;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;

@RunWith(MockitoJUnitRunner.class)
public class FreightDaoImplTest {

    @InjectMocks
    FreightDaoImpl freightDao;

    @Mock
    MongoTemplate mongoTemplate;

    @Test
    public void getFrightById() {

        Freight freight = new Freight();

        freight.set_id(123456L);
        freight.setBillCostAmt(89.8);
        freight.setCarrierCode("Z");
        freight.setBillQty(877L);
        freight.setBillNbr("SKFLJSLJ983");
        freight.setPymtStatCode(5);

        Mockito.when(mongoTemplate.findById(123456L, Freight.class,null)).thenReturn(freight);

        Assert.assertEquals("Z", freightDao.getFrightById(123456L).getCarrierCode());
        Assert.assertEquals("SKFLJSLJ983", freightDao.getFrightById(123456L).getBillNbr());
        Assert.assertEquals(java.util.Optional.of(5).get(), freightDao.getFrightById(123456L).getPymtStatCode());
        Assert.assertEquals(java.util.Optional.of(877L).get(), freightDao.getFrightById(123456L).getBillQty());
        Assert.assertEquals(java.util.Optional.of(89.8).get(), freightDao.getFrightById(123456L).getBillCostAmt());
    }
}