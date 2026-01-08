package com.aaseya.AIS.utility;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Component
@Converter
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(JsonNode attribute) {
		try {
			return attribute == null ? null : objectMapper.writeValueAsString(attribute);
		} catch (Exception e) {
			throw new RuntimeException("Could not convert JSON to String", e);
		}
	}

	@Override
	public JsonNode convertToEntityAttribute(String dbData) {
		try {
			return dbData == null ? null : objectMapper.readTree(dbData);
		} catch (Exception e) {
			throw new RuntimeException("Could not convert String to JSON", e);
		}
	}
}
