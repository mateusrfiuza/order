package challenge.order.dataprovider.database.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import challenge.order.domain.service.CacheService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RedisCacheService implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void evict(String key) {
        redisTemplate.opsForValue().getAndDelete(key);
    }

    @Override
    public boolean putIfAbsent(String key, Object value, Duration ttl) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, ttl));
    }
}
