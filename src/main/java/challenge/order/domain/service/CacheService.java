package challenge.order.domain.service;

import java.time.Duration;

public interface CacheService {

  boolean putIfAbsent(String key, Object value, Duration ttl);
  void evict(String key);

}
