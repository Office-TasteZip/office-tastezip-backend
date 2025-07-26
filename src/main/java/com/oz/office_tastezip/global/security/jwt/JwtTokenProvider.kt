package com.oz.office_tastezip.global.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.oz.office_tastezip.global.exception.DataNotFoundException
import com.oz.office_tastezip.global.exception.InvalidTokenException
import com.oz.office_tastezip.global.security.dto.TokenDto
import com.oz.office_tastezip.global.util.RedisUtils
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit

import com.oz.office_tastezip.global.constant.AuthConstants.Jwt.AUTHORITIES_KEY
import com.oz.office_tastezip.global.constant.AuthConstants.Jwt.SERIAL_KEY
import com.oz.office_tastezip.global.constant.AuthConstants.RedisKey.JWT_KEY_PREFIX

@Component
class JwtTokenProvider(
    private val redisUtils: RedisUtils,
    private val objectMapper: ObjectMapper,
    private val jwtTokenValidator: JwtTokenValidator,
    @Value("\${jwt.access-token-expiration}") accessTokenValidityTime: Long,
    @Value("\${jwt.refresh-token-expiration}") refreshTokenValidityTime: Long
) {

    private val accessTokenValidityTimeMs = accessTokenValidityTime * 1000
    private val refreshTokenValidityTimeMs = refreshTokenValidityTime * 1000

    private val log = LoggerFactory.getLogger(javaClass)

    private fun getRedisTokenKey(email: String) = "$JWT_KEY_PREFIX$email"

    fun generateToken(email: String, authority: String): TokenDto {
        val accessSerial = UUID.randomUUID().toString()
        val refreshSerial = UUID.randomUUID().toString()

        storeTokenSerialsToRedis(email, accessSerial, refreshSerial)

        return TokenDto(
            accessToken = buildAccessToken(email, authority, accessSerial),
            refreshToken = buildRefreshToken(email, refreshSerial)
        )
    }

    private fun storeTokenSerialsToRedis(email: String, accessSerial: String, refreshSerial: String) {
        try {
            val serialJson = objectMapper.writeValueAsString(TokenDto.SerialDto(accessSerial, refreshSerial))
            val redisKey = getRedisTokenKey(email)
            redisUtils.set(redisKey, serialJson)
            redisUtils.setExpiredTime(redisKey, refreshTokenValidityTimeMs, TimeUnit.SECONDS)
        } catch (e: Exception) {
            log.error("Failed to serialize TokenDto.SerialDto to JSON", e)
            throw IllegalStateException("Redis 저장 중 직렬화 실패", e)
        }
    }

    fun refreshTokenValidCheck(refreshToken: String): String {
        try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(jwtTokenValidator.key)
                .build()
                .parseClaimsJws(refreshToken)
                .body

            val email = claims.subject
            val serialDto = redisUtils.get(getRedisTokenKey(email), TokenDto.SerialDto::class.java)
                ?: throw DataNotFoundException("세션이 만료되었습니다. 로그인이 필요합니다.")

            serialDto.takeIf { claims[SERIAL_KEY].toString() == it.refreshSerial }
                ?: throw InvalidTokenException("Refresh Token의 Serial이 일치하지 않습니다.")

            return email
        } catch (e: ExpiredJwtException) {
            log.warn("Token expired: ${e.message}")
            throw InvalidTokenException("Refresh Token이 만료되었습니다.")
        } catch (e: JwtException) {
            log.warn("JWT 검증 실패: ${e.message}")
            throw InvalidTokenException("Refresh Token이 유효하지 않습니다.")
        }
    }

    private fun buildAccessToken(userId: String, authority: String, accessSerial: String): String =
        Jwts.builder()
            .setSubject(userId)
            .claim(SERIAL_KEY, accessSerial)
            .claim(AUTHORITIES_KEY, authority)
            .signWith(jwtTokenValidator.key, SignatureAlgorithm.HS512)
            .setExpiration(getExpirationDate(accessTokenValidityTimeMs))
            .compact()

    private fun buildRefreshToken(userId: String, refreshSerial: String): String =
        Jwts.builder()
            .setSubject(userId)
            .claim(SERIAL_KEY, refreshSerial)
            .signWith(jwtTokenValidator.key, SignatureAlgorithm.HS512)
            .setExpiration(getExpirationDate(refreshTokenValidityTimeMs))
            .compact()

    private fun getExpirationDate(validityTime: Long): Date = Date(System.currentTimeMillis() + validityTime)
}
