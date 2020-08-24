package com.walmart.finance.ap.fds.receiving.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

public class LocalDateTimeDeserializerTest {


    private ObjectMapper mapper;
    private  LocalDateTimeDeserializer deserializer;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
        deserializer = new LocalDateTimeDeserializer();
    }

    @Test
    public void testDeserializer() throws IOException {

        String json = String.format("{\"value\":%s}", "\"2007-12-03T10:15:30\"");
        LocalDateTime deserialisedDateTime = deserializeLocalDate(json);

        Assert.assertEquals(2007,deserialisedDateTime.getYear());
        Assert.assertEquals(Month.DECEMBER,deserialisedDateTime.getMonth());
        Assert.assertEquals(3,deserialisedDateTime.getDayOfMonth());
        Assert.assertEquals(10,deserialisedDateTime.getHour());
        Assert.assertEquals(15,deserialisedDateTime.getMinute());
        Assert.assertEquals(30,deserialisedDateTime.getSecond());


    }

    private LocalDateTime deserializeLocalDate(String json) throws IOException {

        JsonParser parser = mapper.getFactory().createParser(json);
        DeserializationContext ctxt = mapper.getDeserializationContext();
        parser.nextToken();
        parser.nextToken();
        parser.nextToken();

        return deserializer.deserialize(parser, ctxt);
    }
}
