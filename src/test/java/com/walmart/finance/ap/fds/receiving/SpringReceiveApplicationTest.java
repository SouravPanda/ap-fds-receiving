package com.walmart.finance.ap.fds.receiving;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

public class SpringReceiveApplicationTest {

    @BeforeClass
    public static void setSystemProperty() {
        Properties properties = System.getProperties();
        properties.setProperty("spring.profiles.active", "dev-us");
    }

    @AfterClass
    public static void removeSystemProperty() {
        System.clearProperty("spring.profiles.active");
    }

    @Test
    public void main() {
        SpringReceiveApplication.main(new String[]{});
        Assert.assertTrue(true);
    }
}