package com.oz.office_tastezip.global.config;

import com.oz.office_tastezip.global.security.filter.CustomAuthenticationProvider;
import com.oz.office_tastezip.global.security.jwt.JwtAccessDeniedHandler;
import com.oz.office_tastezip.global.security.jwt.JwtAuthenticationEntryPoint;
import com.oz.office_tastezip.global.security.jwt.JwtSecurityConfig;
import com.oz.office_tastezip.global.security.jwt.JwtTokenValidator;
import com.oz.office_tastezip.global.security.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private static final String[] SYSTEM_WHITE_LIST_URL = {
            "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**", "/configuration/ui", "/configuration/security",
            "/webjars/**", "/error", "/static/**", "/customError.css", "/errorIcon.svg", "/favicon.ico",
            "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources", "/swagger-resources/**"
    };
    private static final String[] OTZ_WHITE_LIST_URI = {
            "/api/v1/otz/auth/login", "/api/v1/otz/auth/rsa",
            "/api/v1/otz/users/register", "/api/v1/otz/users/email/verify", "/api/v1/otz/users/email/verify/check"
    };
    private final String[] ALLOWED_HEADERS = {
            "Origin", "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method", "x-request-id",
            "Access-Control-Request-Headers", "Authorization", "Access-Control-Allow-Origin", "Content-Disposition"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(passwordEncoder(), customUserDetailService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder sharedObject = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        sharedObject.authenticationProvider(customAuthenticationProvider());
        AuthenticationManager authenticationManager = sharedObject.build();

        return httpSecurity
                .httpBasic(HttpBasicConfigurer::disable)
                .cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequest ->
                        authorizeRequest
                                .requestMatchers(HttpMethod.HEAD).denyAll()
                                .requestMatchers(HttpMethod.OPTIONS).denyAll()
                                .requestMatchers(SYSTEM_WHITE_LIST_URL).permitAll()
                                .requestMatchers(OTZ_WHITE_LIST_URI).permitAll()
                                .anyRequest().permitAll() // TODO 향후 수정
                )
                .authenticationManager(authenticationManager)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .headers(headersConfigurer ->
                        headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .with(new JwtSecurityConfig(customUserDetailService, jwtTokenValidator), customizer -> {
                })
                .build();
    }

    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedMethods(List.of(new String[]{"GET", "POST", "PUT", "DELETE"}));
            config.setAllowedHeaders(List.of(ALLOWED_HEADERS));
            config.setAllowedOrigins(Collections.singletonList("*"));
            config.setAllowCredentials(true);
            return config;
        };
    }
}
