package com.oz.office_tastezip.global.security.jwt

import com.oz.office_tastezip.global.security.service.CustomUserDetailService
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class JwtSecurityConfig(
    private val customUserDetailService: CustomUserDetailService,
    private val jwtTokenValidator: JwtTokenValidator
) : SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    override fun configure(http: HttpSecurity) {
        http.addFilterBefore(
            JwtFilter(customUserDetailService, jwtTokenValidator),
            UsernamePasswordAuthenticationFilter::class.java
        )
    }
}
