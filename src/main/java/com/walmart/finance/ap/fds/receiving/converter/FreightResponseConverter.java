package com.walmart.finance.ap.fds.receiving.converter;

import com.walmart.finance.ap.fds.receiving.config.DefaultValuesConfigProperties;
import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.model.Freight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FreightResponseConverter implements Converter<Freight, FreightResponse> {
    @Autowired
    private DefaultValuesConfigProperties defaultValuesConfigProperties;
    @Override
    public FreightResponse convert(Freight freight) {
        FreightResponse freightResponse =new FreightResponse();
        freightResponse.setFreightId(freight.get_id());
        freightResponse.setCarrierCode(freight.getCarrierCode()!=null ? freight.getCarrierCode(): defaultValuesConfigProperties.getCarrierCode());
        freightResponse.setTrailerNbr(freight.getTrailerNbr()!=null ? freight.getTrailerNbr(): defaultValuesConfigProperties.getTrailerNbr());
        freightResponse.setVendorNbr(freight.getVendorNbr());
        freightResponse.setBillNbr(freight.getBillNbr()!=null ? freight.getBillNbr(): defaultValuesConfigProperties.getBillNbr());
        freightResponse.setBillCostAmt(freight.getBillCostAmt());
        freightResponse.setBillDate(freight.getBillDate());
        freightResponse.setBillQty(freight.getBillQty());
        freightResponse.setBillWght(freight.getBillWght());
        freightResponse.setCostUomCode(freight.getCostUomCode());
        freightResponse.setPymtStatCode(freight.getPymtStatCode() !=null ? freight.getPymtStatCode(): defaultValuesConfigProperties.getPymtStatCode());
        freightResponse.setQtyUomCode(freight.getQtyUomCode());
        freightResponse.setWghtUomCode(freight.getWghtUomCode());
        return freightResponse;

    }
}
