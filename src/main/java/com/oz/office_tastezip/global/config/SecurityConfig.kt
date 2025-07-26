package com.oz.office_tastezip.global.config

import com.oz.office_tastezip.global.security.filter.CustomAuthenticationProvider
import com.oz.office_tastezip.global.security.jwt.JwtAccessDeniedHandler
import com.oz.office_tastezip.global.security.jwt.JwtAuthenticationEntryPoint
import com.oz.office_tastezip.global.security.jwt.JwtSecurityConfig
import com.oz.office_tastezip.global.security.jwt.JwtTokenValidator
import com.oz.office_tastezip.global.security.service.CustomUserDetailService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
open class SecurityConfig(
    private val jwtTokenValidator: JwtTokenValidator,
    private val jwtAccessDeniedHandler: JwtAccessDeniedHandler,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val customUserDetailService: CustomUserDetailService,
    private val corsConfigProperties: CorsConfigProperties
) {

    companion object {
        private val SYSTEM_WHITE_LIST_URL = arrayOf(
            "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**", "/configuration/ui", "/configuration/security",
            "/webjars/**", "/error", "/static/**", "/customError.css", "/errorIcon.svg", "/favicon.ico",
            "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources", "/swagger-resources/**"
        )

        private val OTZ_WHITE_LIST_URI = arrayOf(
            "/api/v1/otz/auth/login", "/api/v1/otz/auth/rsa",
            "/api/v1/otz/users/register", "/api/v1/otz/users/email/verify", "/api/v1/otz/users/email/verify/check"
        )

        private val ALLOWED_HEADERS = arrayOf(
            "Origin", "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method", "x-request-id",
            "Access-Control-Request-Headers", "Authorization", "Access-Control-Allow-Origin", "Content-Disposition"
        )
    }

    @Bean
    open fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    open fun customAuthenticationProvider(): AuthenticationProvider =
        CustomAuthenticationProvider(passwordEncoder(), customUserDetailService)

    @Bean
    @Throws(Exception::class)
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val builder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        builder.authenticationProvider(customAuthenticationProvider())
        val authenticationManager: AuthenticationManager = builder.build()

        return http
            .httpBasic { obj: HttpBasicConfigurer<HttpSecurity> -> obj.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { obj: AbstractHttpConfigurer<*, *> -> obj.disable() }
            .formLogin { obj: AbstractHttpConfigurer<*, *> -> obj.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.HEAD).denyAll()
                    .requestMatchers(HttpMethod.OPTIONS).denyAll()
                    .requestMatchers(*SYSTEM_WHITE_LIST_URL).permitAll()
                    .requestMatchers(*OTZ_WHITE_LIST_URI).permitAll()
                    .anyRequest().permitAll() // TODO 향후 수정
            }
            .authenticationManager(authenticationManager)
            .exceptionHandling {
                it.accessDeniedHandler(jwtAccessDeniedHandler)
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            }
            .headers {
                it.frameOptions { headers: HeadersConfigurer<HttpSecurity>.FrameOptionsConfig -> headers.sameOrigin() }
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .with(JwtSecurityConfig(customUserDetailService, jwtTokenValidator)) {}
            .build()
    }

    @Bean
    open fun corsConfigurationSource(): CorsConfigurationSource {
        return CorsConfigurationSource {
            CorsConfiguration().apply {
                allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
                allowedHeaders = ALLOWED_HEADERS.toList()
                allowedOrigins = corsConfigProperties.allowedOriginPattern
                allowCredentials = true
            }
        }
    }
}
