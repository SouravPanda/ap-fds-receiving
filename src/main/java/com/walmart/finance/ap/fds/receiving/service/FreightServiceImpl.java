package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.common.ReceivingUtils;
import com.walmart.finance.ap.fds.receiving.converter.FreightResponseConverter;
import com.walmart.finance.ap.fds.receiving.dao.FreightDao;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import com.walmart.finance.ap.fds.receiving.exception.ReceivingErrors;
import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.model.Freight;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FreightServiceImpl implements FreightService {


    @Autowired
    private FreightDao freightDao;

    @Autowired
    private FreightResponseConverter freightResponseConverter;

    @Override
    public ReceivingResponse getFreightById(String id) {
        if (!ReceivingUtils.isNumeric(id)){
           throw new BadRequestException(ReceivingErrors.VALIDID.getParameterName() ,ReceivingErrors.FREIGHTIDDETAILS.getParameterName());
        }
        Freight freight = freightDao.getFrightById(Long.valueOf(id));
        if (freight==null){
            throw new NotFoundException(ReceivingErrors.CONTENTNOTFOUNDFREIGHT.getParameterName() +id);
        }
        ReceivingResponse successMessage = new ReceivingResponse();
        List<FreightResponse> freightResponseList =new ArrayList<>();
        freightResponseList.add(freightResponseConverter.convert(freight));
        successMessage.setData(freightResponseList);
        successMessage.setSuccess(true);
        successMessage.setTimestamp(LocalDateTime.now());
        return successMessage;

    }
}
