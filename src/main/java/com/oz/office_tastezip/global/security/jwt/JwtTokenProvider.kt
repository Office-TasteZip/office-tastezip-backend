package com.oz.office_tastezip.global.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oz.office_tastezip.global.exception.DataNotFoundException;
import com.oz.office_tastezip.global.exception.InvalidTokenException;
import com.oz.office_tastezip.global.security.dto.TokenDto;
import com.oz.office_tastezip.global.util.RedisUtils;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.oz.office_tastezip.global.constant.AuthConstants.Jwt.AUTHORITIES_KEY;
import static com.oz.office_tastezip.global.constant.AuthConstants.Jwt.SERIAL_KEY;
import static com.oz.office_tastezip.global.constant.AuthConstants.RedisKey.JWT_KEY_PREFIX;

@Slf4j
@Component
public class JwtTokenProvider {

    private final RedisUtils redisUtils;
    private final ObjectMapper objectMapper;
    private final JwtTokenValidator jwtTokenValidator;

    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;

    public JwtTokenProvider(
            RedisUtils redisUtils,
            ObjectMapper objectMapper,
            JwtTokenValidator jwtTokenValidator,
            @Value("${jwt.access-token-expiration}") long accessTokenValidityTime,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenValidityTime
    ) {
        this.redisUtils = redisUtils;
        this.objectMapper = objectMapper;
        this.jwtTokenValidator = jwtTokenValidator;
        this.accessTokenValidityTime = accessTokenValidityTime * 1000;
        this.refreshTokenValidityTime = refreshTokenValidityTime * 1000;
    }

    private String getRedisTokenKey(String email) {
        return JWT_KEY_PREFIX + email;
    }

    public TokenDto generateToken(String email, String authority) {
        String accessSerial = String.valueOf(UUID.randomUUID());
        String refreshSerial = String.valueOf(UUID.randomUUID());

        storeTokenSerialsToRedis(email, accessSerial, refreshSerial);

        return TokenDto.builder()
                .accessToken(buildAccessToken(email, authority, accessSerial))
                .refreshToken(buildRefreshToken(email, refreshSerial))
                .build();
    }

    private void storeTokenSerialsToRedis(String email, String accessSerial, String refreshSerial) {
        try {
            String redisTokenKey = JWT_KEY_PREFIX + email;
            String serialJson = objectMapper.writeValueAsString(new TokenDto.SerialDto(accessSerial, refreshSerial));

            redisUtils.set(redisTokenKey, serialJson);
            redisUtils.setExpiredTime(redisTokenKey, refreshTokenValidityTime, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize TokenDto.SerialDto to JSON", e);
            throw new IllegalStateException("Redis 저장 중 직렬화 실패", e);
        }
    }

    public String refreshTokenValidCheck(String refreshToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtTokenValidator.getKey())
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            String email = claims.getSubject();
            TokenDto.SerialDto serialDto = redisUtils.get(getRedisTokenKey(email), TokenDto.SerialDto.class)
                    .orElseThrow(() -> new DataNotFoundException("세션이 만료되었습니다. 로그인이 필요합니다."));

            if (serialDto == null || !String.valueOf(claims.get(SERIAL_KEY)).equals(serialDto.getRefreshSerial())) {
                throw new InvalidTokenException("Refresh Token의 Serial이 일치하지 않습니다.");
            }

            return email;

        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Refresh Token이 만료되었습니다.");
        } catch (JwtException e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            throw new InvalidTokenException("Refresh Token이 유효하지 않습니다.");
        }

    }

    private String buildAccessToken(String userId, String authority, String accessSerial) {
        return Jwts.builder()
                .setSubject(userId)
                .claim(SERIAL_KEY, accessSerial)
                .claim(AUTHORITIES_KEY, authority)
                .signWith(jwtTokenValidator.getKey(), SignatureAlgorithm.HS512)
                .setExpiration(getExpirationDate(accessTokenValidityTime))
                .compact();
    }

    private String buildRefreshToken(String userId, String refreshSerial) {
        return Jwts.builder()
                .setSubject(userId)
                .claim(SERIAL_KEY, refreshSerial)
                .signWith(jwtTokenValidator.getKey(), SignatureAlgorithm.HS512)
                .setExpiration(getExpirationDate(refreshTokenValidityTime))
                .compact();
    }

    private Date getExpirationDate(long validityTime) {
        return new Date((new Date()).getTime() + validityTime);
    }
}
