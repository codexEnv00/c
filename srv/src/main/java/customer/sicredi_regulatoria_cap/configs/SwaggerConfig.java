package customer.sicredi_regulatoria_cap.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@SecurityScheme(//
        type = SecuritySchemeType.OAUTH2, //
        flows = @OAuthFlows(//
                password = @OAuthFlow(//
                        tokenUrl = "https://sap-sicredi-hom.authentication.br10.hana.ondemand.com/oauth/token"//
                )//
        )//
)
public class SwaggerConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger/**")
                .addResourceLocations("classpath:/swagger/");
    }
}
