package com.walmart.store.receive.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import springfox.documentation.spring.web.plugins.Docket;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;


@RunWith(MockitoJUnitRunner.class)
public class SwaggerConfigTest {


    @InjectMocks
    private SwaggerConfig swaggerConfig;


    @Test
    public void testapi() {

        assertTrue(swaggerConfig.api() instanceof Docket);

    }
}