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

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        Object value = valueOperations.get(key);
        if (value == null) return Optional.empty();

        if (value instanceof String str) {
            try {
                return Optional.ofNullable(JsonUtils.getObject(str, type));
            } catch (Exception e) {
                log.error("Redis value deserialization failed. key: [{}], type: [{}]", key, type.getName(), e);
                throw new IllegalStateException("Redis deserialization error", e);
            }
        }

        if (type.isInstance(value)) {
            return Optional.of((T) value);
        }

        log.error("Stored value type [{}] cannot be cast to [{}]", value.getClass().getName(), type.getName());
        throw new ClassCastException("Stored value is not of type " + type.getName());
    }

    public Object getOrNull(String key) {
        return valueOperations.get(key);
    }

    public void setExpiredTime(String key, long tokenValidityTime, TimeUnit timeUnit) {
        redisTemplate.expire(key, tokenValidityTime, timeUnit);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
