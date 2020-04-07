package com.walmart.finance.ap.fds.receiving.converter;

import com.walmart.finance.ap.fds.receiving.config.DefaultValuesConfigProperties;
import com.walmart.finance.ap.fds.receiving.model.Freight;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;;


@RunWith(MockitoJUnitRunner.class)
public class FreightResponseConverterTest {

    @InjectMocks
    FreightResponseConverter freightResponseConverter;

    @Mock
    DefaultValuesConfigProperties defaultValuesConfigProperties;

    @Test
    public void TestConvert() {
        Freight freight = new Freight();
        freight.set_id(3092402L);
        freight.setBillCostAmt(89.8);
        freight.setCarrierCode("Z");
        freight.setBillQty(877L);
        freight.setBillNbr("SKFLJSLJ983");
        freight.setPymtStatCode(5);

        Mockito.when(defaultValuesConfigProperties.getTrailerNbr()).thenReturn("0");

        Assert.assertEquals("Z", freightResponseConverter.convert(freight).getCarrierCode());
        Assert.assertEquals("SKFLJSLJ983", freightResponseConverter.convert(freight).getBillNbr());
        Assert.assertEquals(java.util.Optional.of(5).get(), freightResponseConverter.convert(freight).getPymtStatCode());
        Assert.assertEquals("0", freightResponseConverter.convert(freight).getTrailerNbr());
        Assert.assertEquals(java.util.Optional.of(3092402L).get(), freightResponseConverter.convert(freight).getFreightId());
        Assert.assertEquals(java.util.Optional.of(89.8).get(), freightResponseConverter.convert(freight).getBillCostAmt());

    }
}