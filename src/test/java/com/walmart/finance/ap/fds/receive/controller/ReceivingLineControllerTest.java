/*

package com.walmart.finance.ap.fds.receive.controller;

import com.walmart.finance.ap.fds.receiving.controller.ReceivingLineController;
import com.walmart.finance.ap.fds.receiving.request.ReceiveLineSearch;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineService;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.powermock.configuration.ConfigurationType.PowerMock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ReceivingLineController.class })
public class ReceivingLineControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReceiveLineServiceImpl receiveLineServiceImpl;

    @Before public void setup(){
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void getReceiveLineTest() throws Exception{
        ReceivingLineResponse receivingLineResponse = new ReceivingLineResponse(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,5,null,3590,11);
        ReceiveLineSearch receiveLineSearch = new ReceiveLineSearch(1145L, 1124L, 11, "HHLL", 3590, 113, 0);
       // Mockito.when(receiveLineServiceImpl.getReceiveLineSearch(receiveLineSearch,1,1,"creationDate")).thenReturn()
        PowerMockito.spy(ReceiveLineServiceImpl.class);
        final ReceiveLineServiceImpl receiveLineServiceImpl = Mockito.mock (ReceiveLineServiceImpl.class);
    */
/*    ReceiveLineServiceImpl receiveLineServiceImplOne = PowerMock
        CustomObject object1 = PowerMock.createNiceMock(CustomObject.class);
        CustomObject object1 = new CustomObject("input1");
        CustomObject object2 = new CustomObject("input2");
        Mockito.when (myClass.methodB(object1, object2)).thenReturn (true);
        Mockito.when (myClass.methodA(object1, object2)).thenCallRealMethod ();*//*


    }
}

*/
