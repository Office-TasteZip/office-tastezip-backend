package com.oz.office_tastezip.global.security.jwt;

import com.oz.office_tastezip.global.security.dto.CustomUserDetails;
import com.oz.office_tastezip.global.security.service.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

import static com.oz.office_tastezip.global.constant.AuthConstants.Header.AUTHORIZATION_HEADER;
import static com.oz.office_tastezip.global.constant.AuthConstants.Header.JWT_TOKEN_PREFIX;

@Slf4j
public class JwtFilter extends GenericFilterBean {

    private final CustomUserDetailService customUserDetailService;
    private final JwtTokenValidator jwtTokenValidator;

    public JwtFilter(CustomUserDetailService customUserDetailService, JwtTokenValidator jwtTokenValidator) {
        this.customUserDetailService = customUserDetailService;
        this.jwtTokenValidator = jwtTokenValidator;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        jwtValidationCheck(request, resolveBearerToken(request.getHeader(AUTHORIZATION_HEADER)));

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String resolveBearerToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(JWT_TOKEN_PREFIX)) {
            return null;
        }

        return authorizationHeader.substring(JWT_TOKEN_PREFIX.length());
    }

    private void jwtValidationCheck(HttpServletRequest httpServletRequest, String resolvedToken) {
        String requestUri = httpServletRequest.getRequestURI();
        String remoteAddr = httpServletRequest.getRemoteAddr();
        log.info("{}|Request uri: {}", remoteAddr, requestUri);

        if (!StringUtils.hasText(resolvedToken) || !jwtTokenValidator.validateToken(resolvedToken)) {
            log.debug("{}|유효한 JWT 토큰이 없습니다, uri: {}", remoteAddr, requestUri);
            return;
        }

        Authentication authentication = jwtTokenValidator.getAuthentication(resolvedToken);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(), authentication.getCredentials(), authentication.getAuthorities());

        CustomUserDetails customUserDetails = customUserDetailService.loadUserByEmail(authentication.getName());
        customUserDetails.setRemoteUserIpAddress(remoteAddr);

        authenticationToken.setDetails(customUserDetails);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        log.debug("{}|Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", remoteAddr, authentication.getName(), requestUri);
    }
}
