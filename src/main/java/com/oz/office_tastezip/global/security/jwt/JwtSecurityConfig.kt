package com.oz.office_tastezip.global.security.jwt;

import com.oz.office_tastezip.global.security.service.CustomUserDetailService;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final CustomUserDetailService customUserDetailService;
    private final JwtTokenValidator jwtTokenValidator;

    public JwtSecurityConfig(CustomUserDetailService customUserDetailService, JwtTokenValidator jwtTokenValidator) {
        this.customUserDetailService = customUserDetailService;
        this.jwtTokenValidator = jwtTokenValidator;
    }

    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(new JwtFilter(customUserDetailService, jwtTokenValidator), UsernamePasswordAuthenticationFilter.class);
    }
}
