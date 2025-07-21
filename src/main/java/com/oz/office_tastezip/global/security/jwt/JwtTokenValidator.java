package com.oz.office_tastezip.global.security.jwt;

import com.oz.office_tastezip.support.util.JsonUtil;
import com.oz.office_tastezip.global.security.dto.TokenDto;
import com.oz.office_tastezip.global.exception.DataNotFoundException;
import com.oz.office_tastezip.global.exception.InvalidTokenException;
import com.oz.office_tastezip.global.response.ResponseCode;
import com.oz.office_tastezip.global.util.RedisUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.oz.office_tastezip.global.constant.AuthConstants.Jwt.AUTHORITIES_KEY;
import static com.oz.office_tastezip.global.constant.AuthConstants.Jwt.SERIAL_KEY;
import static com.oz.office_tastezip.global.constant.AuthConstants.RedisKey.JWT_KEY_PREFIX;

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

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            serialCodeValidCheck(claimsJws.getBody().getSubject(), claimsJws);

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    private void serialCodeValidCheck(String userId, Jws<Claims> claimsJws) {
        Object serial = redisUtils.get(JWT_KEY_PREFIX + userId).orElseThrow(() ->
                new DataNotFoundException(ResponseCode.UNAUTHORIZED, "로그인하지 않은 사용자입니다."));
        TokenDto.SerialDto serialDto = JsonUtil.getObject(serial.toString(), TokenDto.SerialDto.class);
        if (serialDto != null && !serialDto.getAccessSerial().equals(claimsJws.getBody().get(SERIAL_KEY))) {
            throw new InvalidTokenException(ResponseCode.INVALID_TOKEN, "시리얼 번호가 일치하지 않습니다.");
        }
    }
}
