package com.walmart.finance.ap.fds.receiving.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/*
 * This class extends the JsonDeserializer to parse incoming payload to support
 * 'HH:mm:ss' timestamp format for timestamp fields like 'invTimeValue'. It is referred by
 *
 * using the annotation @JsonDeserialize on property fields in classes like
 * {@link InvoiceDateTime}
 */
public class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
        String localTimeString = null;
        if(Objects.nonNull(p)) {
            localTimeString = p.readValueAs(String.class);
        }
        return LocalTime.parse(localTimeString, formatter);
    }
}
