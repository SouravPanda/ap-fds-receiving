package com.walmart.finance.ap.fds.receiving.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

	@Override
	public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException {
		String localDateString = p.readValueAs(String.class);
		return LocalDate.parse(localDateString, formatter);
	}

}
