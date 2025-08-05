package com.oz.office_tastezip.global.security.jwt

import com.oz.office_tastezip.global.security.service.CustomUserDetailService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean

class JwtFilter(
    private val customUserDetailService: CustomUserDetailService,
    private val jwtTokenValidator: JwtTokenValidator
) : GenericFilterBean() {

    private val log = KotlinLogging.logger {}

    companion object {
        private const val JWT_TOKEN_PREFIX = "Bearer "
        private const val AUTHORIZATION_HEADER = "Authorization"
    }

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        val request = servletRequest as HttpServletRequest
        val token = resolveBearerToken(request.getHeader(AUTHORIZATION_HEADER))

        jwtValidationCheck(request, token)

        filterChain.doFilter(servletRequest, servletResponse)
    }

    private fun resolveBearerToken(authorizationHeader: String?): String? {
        return if (!authorizationHeader.isNullOrBlank() && authorizationHeader.startsWith(JWT_TOKEN_PREFIX)) {
            authorizationHeader.substring(JWT_TOKEN_PREFIX.length)
        } else {
            null
        }
    }

    private fun jwtValidationCheck(request: HttpServletRequest, resolvedToken: String?) {
        val uri = request.requestURI
        val ip = request.remoteAddr
        log.info { "$ip|Request uri: $uri" }

        if (resolvedToken.isNullOrBlank() || !jwtTokenValidator.validateToken(resolvedToken)) {
            log.debug { "$ip|유효한 JWT 토큰이 없습니다, uri: $uri" }
            return
        }

        val authentication = jwtTokenValidator.getAuthentication(resolvedToken)

        val authToken = UsernamePasswordAuthenticationToken(
            authentication.principal,
            authentication.credentials,
            authentication.authorities
        )

        val userDetails = customUserDetailService.loadUserByEmail(authentication.name)
        userDetails.remoteUserIpAddress = ip

        authToken.details = userDetails
        SecurityContextHolder.getContext().authentication = authToken

        log.debug { "$ip|Security Context에 '${authentication.name}' 인증 정보를 저장했습니다, uri: $uri" }
    }
}
