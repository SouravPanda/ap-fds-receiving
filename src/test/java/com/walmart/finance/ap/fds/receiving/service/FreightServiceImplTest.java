package com.walmart.finance.ap.fds.receiving.service;
import com.walmart.finance.ap.fds.receiving.converter.FreightResponseConverter;
import com.walmart.finance.ap.fds.receiving.dao.FreightDaoImpl;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.mongodb.core.MongoTemplate;

@PrepareForTest(FreightServiceImpl.class)
@RunWith(PowerMockRunner.class)
public class FreightServiceImplTest {

    @Mock
    MongoTemplate mongoTemplate;


    @Mock
    FreightDaoImpl freightDaoImpl;

    @Mock
    FreightResponseConverter freightResponseConverter;

    @Mock
    FreightService freightService;


    @Test
    public void testGetFrightbyId(){
        String id ="1234";
        ReceivingResponse receivingResponse =freightService.getFreightById(id);
    }


}
