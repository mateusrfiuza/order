package challenge.order.entrypoint.http.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Allows access to:
 * <ul>
 * <li>the Swagger documentation via the {@code /api} endpoint
 * </ul>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addRedirectViewController("/api", "/swagger-ui/index.html");
  }
}
