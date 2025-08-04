package com.oz.office_tastezip.global.util

import mu.KotlinLogging
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisUtils(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val valueOperations: ValueOperations<String, Any> = redisTemplate.opsForValue()

    fun set(key: String, value: Any) {
        valueOperations.set(key, value)
    }

    fun set(key: String, value: Any, timeout: Long, timeUnit: TimeUnit) {
        valueOperations.set(key, value, timeout, timeUnit)
    }

    fun get(key: String): Any? {
        return valueOperations.get(key)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, type: Class<T>): T? {
        val value = valueOperations.get(key) ?: return null

        if (value is String) {
            return try {
                JsonUtils.getObject(value, type)
            } catch (e: Exception) {
                logger.error(e) { "Redis value deserialization failed. key=[$key], type=[${type.name}]" }
                throw IllegalStateException("Redis deserialization error", e)
            }
        }

        if (type.isInstance(value)) {
            return value as T
        }

        logger.error { "Stored value type [${value.javaClass.name}] cannot be cast to [${type.name}]" }
        throw ClassCastException("Stored value is not of type ${type.name}")
    }

    fun getOrNull(key: String): Any? {
        return valueOperations.get(key)
    }

    fun setExpiredTime(key: String, tokenValidityTime: Long, timeUnit: TimeUnit) {
        redisTemplate.expire(key, tokenValidityTime, timeUnit)
    }

    fun getExpire(key: String, timeunit: TimeUnit): Long {
        return redisTemplate.getExpire(key, timeunit)
    }

    fun delete(key: String) {
        redisTemplate.delete(key)
    }
}
