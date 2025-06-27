package customer.sicredi_regulatoria_cap.configs.deserializers;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CustomLocalDateTimeDeserializerConfig extends JsonDeserializer<LocalDate> {

	@Override
	public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		String value = p.getText();
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		
		long timestamp = Long.parseLong(value.replaceAll("[^0-9]", ""));
		return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
}