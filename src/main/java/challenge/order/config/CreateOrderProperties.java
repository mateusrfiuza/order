package challenge.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import lombok.AccessLevel;
import lombok.Getter;

@Configuration
@Getter
public class CreateOrderProperties {

  @Value("${order.creation.deduplication.ttl.hours}")
  @Getter(AccessLevel.PRIVATE)
  private Long deduplicationHoursTTL;

  private final String deduplicationKeyPattern = "create-order:%s-%s";

  public Duration getDeduplicationTtl() {
    return Duration.ofHours(deduplicationHoursTTL);
  }


}
