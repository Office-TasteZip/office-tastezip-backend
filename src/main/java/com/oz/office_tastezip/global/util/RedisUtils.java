package com.oz.office_tastezip.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOperations;

    public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }

    public void set(String key, Object value) {
        valueOperations.set(key, value);
    }

    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        valueOperations.set(key, value, timeout, timeUnit);
    }

    public Optional<Object> get(String key) {
        return Optional.ofNullable(valueOperations.get(key));
    }

    public void setExpiredTime(String key, long tokenValidityTime, TimeUnit timeUnit) {
        redisTemplate.expire(key, tokenValidityTime, timeUnit);
    }
}
