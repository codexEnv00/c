package customer.sicredi_regulatoria_cap.configs;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;

@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager("balances", "cadoc4500", "companies");
		cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS));
		return cacheManager;
	}

	@Bean("balanceKeyGenerator")
	public KeyGenerator keyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object o, Method method, Object... params) {
				StringBuilder sb = new StringBuilder();
				sb.append(o.getClass().getName())
				  .append("_")
				  .append(method.getName());
				
				for (Object param : params) {
					sb.append("_");
					if (param instanceof BalanceFilterDTO) {
						BalanceFilterDTO filterDTO = (BalanceFilterDTO) ((Object[]) params)[0];
						sb.append(filterDTO.hashCodeKeyCache());
					} else {
						sb.append(param.hashCode());
					}
				}
				return sb.toString();
			}
		};
	}
}
