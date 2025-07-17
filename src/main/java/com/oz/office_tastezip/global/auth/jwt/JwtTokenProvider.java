package com.oz.office_tastezip.global.auth.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oz.office_tastezip.common.util.JsonUtil;
import com.oz.office_tastezip.global.auth.dto.TokenDto;
import com.oz.office_tastezip.global.exception.InvalidTokenException;
import com.oz.office_tastezip.global.util.RedisUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.oz.office_tastezip.global.constant.AuthConstants.Jwt.AUTHORITIES_KEY;
import static com.oz.office_tastezip.global.constant.AuthConstants.Jwt.SERIAL_KEY;

@Slf4j
@Component
public class JwtTokenProvider {

    private final RedisUtils redisUtils;
    private final JwtTokenValidator jwtTokenValidator;

    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;

    public JwtTokenProvider(
            @Value("${jwt.access-token-expiration}") long accessTokenValidityTime,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenValidityTime,
            RedisUtils redisUtils,
            JwtTokenValidator jwtTokenValidator
    ) {
        this.accessTokenValidityTime = accessTokenValidityTime * 1000;
        this.refreshTokenValidityTime = refreshTokenValidityTime * 1000;
        this.redisUtils = redisUtils;
        this.jwtTokenValidator = jwtTokenValidator;
    }

    public TokenDto generateToken(String userId, String authority, String keyPrefix) throws Exception {
        String accessSerial = String.valueOf(UUID.randomUUID());
        String refreshSerial = String.valueOf(UUID.randomUUID());

        setSerialToRedis(accessSerial, refreshSerial, keyPrefix + userId);

        return TokenDto.builder()
                .accessToken(buildAccessToken(userId, authority, accessSerial))
                .refreshToken(buildRefreshToken(userId, refreshSerial))
                .build();
    }

    private void setSerialToRedis(String accessSerial, String refreshSerial, String redisTokenKey) throws JsonProcessingException {
        redisUtils.set(redisTokenKey, new ObjectMapper().writeValueAsString(new TokenDto.SerialDto(accessSerial, refreshSerial)));
        redisUtils.setExpiredTime(redisTokenKey, refreshTokenValidityTime, TimeUnit.SECONDS);
    }

    public String refreshTokenValidCheck(String refresh, String keyPrefix) {
        Claims claimsJws = getClaimsJws(refresh);

        String userId = String.valueOf(claimsJws.get("sub"));
        String serial = String.valueOf(claimsJws.get(SERIAL_KEY));
        Object serialData = redisUtils.get(keyPrefix + userId).orElseThrow(() ->
                new InvalidTokenException("Redis에 토큰 데이터가 없습니다. 로그인이 필요합니다."));
        TokenDto.SerialDto serialDto = JsonUtil.getObject(serialData.toString(), TokenDto.SerialDto.class);

        if (serialDto == null || !serial.equals(serialDto.getRefreshSerial()))
            throw new InvalidTokenException("Refresh Token의 Serial이 일치하지 않습니다.");

        return userId;
    }

    private Claims getClaimsJws(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtTokenValidator.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
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
