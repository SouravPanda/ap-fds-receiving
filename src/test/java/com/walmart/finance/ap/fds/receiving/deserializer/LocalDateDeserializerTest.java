package com.walmart.finance.ap.fds.receiving.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.time.Month;

public class LocalDateDeserializerTest {
    private ObjectMapper mapper;
    private  LocalDateDeserializer deserializer;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
        deserializer = new LocalDateDeserializer();
    }

    @Test
    public void testDeserializer() throws IOException {

        String json = String.format("{\"value\":%s}", "\"1986-04-08\"");
        LocalDate deserialisedDate = deserializeLocalDate(json);
        Assert.assertEquals(1986,deserialisedDate.getYear());
        Assert.assertEquals(Month.APRIL,deserialisedDate.getMonth());
        Assert.assertEquals(8,deserialisedDate.getDayOfMonth());


    }

    private LocalDate deserializeLocalDate(String json) throws IOException {

        JsonParser parser = mapper.getFactory().createParser(json);
        DeserializationContext ctxt = mapper.getDeserializationContext();
        parser.nextToken();
        parser.nextToken();
        parser.nextToken();

        return deserializer.deserialize(parser, ctxt);
    }
}
