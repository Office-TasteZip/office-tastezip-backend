package com.oz.office_tastezip.global.security.jwt

import com.oz.office_tastezip.global.constant.AuthConstants.Jwt.AUTHORITIES_KEY
import com.oz.office_tastezip.global.constant.AuthConstants.Jwt.SERIAL_KEY
import com.oz.office_tastezip.global.constant.AuthConstants.RedisKey.JWT_KEY_PREFIX
import com.oz.office_tastezip.global.exception.DataNotFoundException
import com.oz.office_tastezip.global.exception.InvalidTokenException
import com.oz.office_tastezip.global.response.ResponseCode
import com.oz.office_tastezip.global.security.dto.TokenDto
import com.oz.office_tastezip.global.util.RedisUtils
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.security.Key

@Component
class JwtTokenValidator(
    @Value("\${jwt.secret}") private val secret: String,
    private val redisUtils: RedisUtils
) : InitializingBean {

    lateinit var key: Key
    private val log = LoggerFactory.getLogger(javaClass)

    override fun afterPropertiesSet() {
        val keyBytes = Decoders.BASE64.decode(secret)
        key = Keys.hmacShaKeyFor(keyBytes)
    }

    fun getAuthentication(token: String): Authentication {
        val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body

        val authorities: Collection<GrantedAuthority> =
            claims[AUTHORITIES_KEY].toString()
                .split(",")
                .map { SimpleGrantedAuthority(it) }

        val principal = User(claims.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            serialCodeValidCheck(claimsJws.body.subject, claimsJws)
            true
        } catch (e: SecurityException) {
            log.info("잘못된 JWT 서명입니다.")
            false
        } catch (e: MalformedJwtException) {
            log.info("잘못된 JWT 토큰입니다.")
            false
        } catch (e: ExpiredJwtException) {
            log.info("만료된 JWT 토큰입니다.")
            false
        } catch (e: UnsupportedJwtException) {
            log.info("지원되지 않는 JWT 토큰입니다.")
            false
        } catch (e: IllegalArgumentException) {
            log.info("JWT 토큰이 잘못되었습니다.")
            false
        }
    }

    private fun serialCodeValidCheck(userId: String, claimsJws: Jws<Claims>) {
        val redisKey = JWT_KEY_PREFIX + userId
        val serialDto = redisUtils.get(redisKey, TokenDto.SerialDto::class.java)
            ?: throw DataNotFoundException(ResponseCode.UNAUTHORIZED, "로그인하지 않은 사용자입니다.")

        serialDto.takeIf { serialDto.accessSerial != claimsJws.body[SERIAL_KEY] }
            ?: throw InvalidTokenException(ResponseCode.INVALID_TOKEN, "시리얼 번호가 일치하지 않습니다.")
    }
}
