package customer.sicredi_regulatoria_cap.services;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsReadEventContext;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ParameterReadContextService {

	public void put(String name, CdsReadEventContext context) {
		put(name, context, (v) -> v, true);
	}

	public void put(String name, CdsReadEventContext context, boolean isRequired) {
		put(name, context, v -> v, isRequired);
	}

	public void put(String name, CdsReadEventContext context, Function<String, Object> function) {
		put(name, context, function, true);
	}

	public void put(String name, CdsReadEventContext context, Function<String, Object> function, boolean isRequired) {
		Optional<String> opt = Optional.ofNullable(context.getParameterInfo().getQueryParameter(name));

		if (opt.isEmpty() && isRequired) {
			log.info("{} parameter not present", name);
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "{} parameters is required", name);
		}

		context.put(name, function.apply(opt.orElse(null)));
	}

	public <T> T get(CdsReadEventContext context, String key, Class<T> clazz) {
		return clazz.cast(context.get(key));
	}
}
