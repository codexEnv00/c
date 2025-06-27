package customer.sicredi_regulatoria_cap.configs;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ObjectMapperConfig {

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ObjectMapper mapper() {
		return new ObjectMapper()
				.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
}
