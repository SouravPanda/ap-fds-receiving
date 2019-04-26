package com.walmart.store.receive.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/*
 * This class extends the JsonSerializer to parse outgoing payload from Adapter
 * Service to 'HH:mm:ss' timestamp format for timestamp fields like
 * 'INVOICE_TIME_VALUE'. It is referred by using the annotation @JsonSerialize on
 * property fields in classes like {@link InvoiceDateTime}
 */
public class LocalTimeSerializer extends JsonSerializer<LocalTime> {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if(Objects.nonNull(value)) {
            gen.writeString(value.format(formatter));
        }

    }
}
