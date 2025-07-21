package com.oz.office_tastezip.global.auth.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final JwtTokenValidator jwtTokenValidator;

    public JwtSecurityConfig(JwtTokenValidator jwtTokenValidator) {
        this.jwtTokenValidator = jwtTokenValidator;
    }

    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(new JwtFilter(jwtTokenValidator), UsernamePasswordAuthenticationFilter.class);
    }
}
