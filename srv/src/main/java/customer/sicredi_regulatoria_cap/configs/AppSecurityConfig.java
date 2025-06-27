package customer.sicredi_regulatoria_cap.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Order(1)
public class AppSecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			.securityMatchers(
				matchers -> {
					matchers.requestMatchers("/swagger/**", "/actuator/**");
				})
			.csrf(c -> c.disable())
			.authorizeHttpRequests(r -> r.anyRequest().permitAll())
			.build();
	}
}
