package com.walmart.finance.ap.fds.receiving.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalTime;

public class LocalTimeDeserializerTest {

    private ObjectMapper mapper;
    private  LocalTimeDeserializer deserializer;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
        deserializer = new LocalTimeDeserializer();
    }

    @Test
    public void testDeserializer() throws IOException {

        String json = String.format("{\"value\":%s}", "\"12:00:01\"");
        LocalTime deserialisedTime = deserializeLocalDate(json);

        Assert.assertEquals(12,deserialisedTime.getHour());
        Assert.assertEquals(00,deserialisedTime.getMinute());
        Assert.assertEquals(01,deserialisedTime.getSecond());

    }

    private LocalTime deserializeLocalDate(String json) throws IOException {

        JsonParser parser = mapper.getFactory().createParser(json);
        DeserializationContext ctxt = mapper.getDeserializationContext();
        parser.nextToken();
        parser.nextToken();
        parser.nextToken();

        return deserializer.deserialize(parser, ctxt);
    }
}
