package challenge.order.entrypoint.http.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenApiCustomizer openApiInfoCustomizer() {
        return (openApi) -> openApi
            .info(new Info()
                      .title("Order Service Management"));
    }

    @Bean
    @Primary
    public SwaggerUiConfigProperties swaggerUiConfig(SwaggerUiConfigProperties config) {
        config.setDisplayRequestDuration(true);
        config.setShowCommonExtensions(true);
        config.setFilter("true");
        config.setTryItOutEnabled(true);
        return config;
    }

}
