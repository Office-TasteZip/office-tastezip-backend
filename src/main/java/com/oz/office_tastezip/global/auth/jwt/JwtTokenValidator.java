package com.oz.office_tastezip.global.auth.jwt;

import com.oz.office_tastezip.global.util.RedisUtils;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Slf4j
@Component
public class JwtTokenValidator implements InitializingBean {

    @Getter
    private Key key;
    private final String secret;
    private final RedisUtils redisUtils;

    public JwtTokenValidator(@Value("${jwt.secret}") String secret, RedisUtils redisUtils) {
        this.secret = secret;
        this.redisUtils = redisUtils;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
}
