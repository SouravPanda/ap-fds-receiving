package com.walmart.finance.ap.fds.receiving.service;
import com.walmart.finance.ap.fds.receiving.converter.FreightResponseConverter;
import com.walmart.finance.ap.fds.receiving.dao.FreightDaoImpl;

import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.model.Freight;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class FreightServiceImplTest {

    @InjectMocks
    FreightServiceImpl freightService;

    @Mock
    FreightDaoImpl freightDaoImpl;

    @Mock
    FreightResponseConverter freightResponseConverter;

    @Test
    public void testGetFrightbyId(){

        Freight freight = new Freight();
        freight.set_id(123456L);
        freight.setBillCostAmt(89.8);
        freight.setCarrierCode("Z");
        freight.setBillQty(877L);
        freight.setBillNbr("SKFLJSLJ983");
        freight.setPymtStatCode(5);

        FreightResponse freightResponse = new FreightResponse();
        freightResponse.setFreightId(123456L);
        freightResponse.setBillCostAmt(89.8);
        freightResponse.setCarrierCode("Z");
        freightResponse.setBillQty(877L);
        freightResponse.setBillNbr("SKFLJSLJ983");
        freightResponse.setPymtStatCode(5);
        freightResponse.setTrailerNbr("0");

        Mockito.when(freightDaoImpl.getFrightById(123456L)).thenReturn(freight);
        Mockito.when(freightResponseConverter.convert(freight)).thenReturn(freightResponse);

        Assert.assertEquals(true,freightService.getFreightById("123456").isSuccess());
        Assert.assertEquals(freightResponse,freightService.getFreightById("123456").getData().get(0));
    }

}
