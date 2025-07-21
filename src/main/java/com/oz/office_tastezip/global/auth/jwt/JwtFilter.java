package com.oz.office_tastezip.global.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

import static com.oz.office_tastezip.global.constant.AuthConstants.Header.AUTHORIZATION_HEADER;
import static com.oz.office_tastezip.global.constant.AuthConstants.Header.JWT_TOKEN_PREFIX;

@Slf4j
public class JwtFilter extends GenericFilterBean {

    private final JwtTokenValidator jwtTokenValidator;

    private static final int SUBSTRING_BEARER_INDEX = 7;

    public JwtFilter(JwtTokenValidator jwtTokenValidator) {
        this.jwtTokenValidator = jwtTokenValidator;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String jwt = resolveToken(httpServletRequest);
        String requestUri = httpServletRequest.getRequestURI();

        log.info("[JwtFilter] Request uri: {}, Request user ip: {}", requestUri, httpServletRequest.getRemoteAddr());
        if (StringUtils.hasText(jwt) && jwtTokenValidator.validateToken(jwt)) {
            Authentication authentication = jwtTokenValidator.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestUri);
        } else {
            log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestUri);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith(JWT_TOKEN_PREFIX)) return null;
        return bearerToken.substring(SUBSTRING_BEARER_INDEX);
    }
}
