package techlab.backend.service.auth;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ConfirmationTokenService {

    private final RedisTemplate<String, Long> redisTemplate;

    public ConfirmationTokenService(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeToken(String token, Long data, long ttl, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(token, data, ttl, timeUnit);
    }

    public Long getByTokenAndDelete(String token) {
        return redisTemplate.opsForValue().getAndDelete(token);
    }

    public Long getByToken(String token) {
        return redisTemplate.opsForValue().get(token);
    }
}
