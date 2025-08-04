package com.oz.office_tastezip.global.security.filter

import com.oz.office_tastezip.global.exception.ValidationFailureException
import com.oz.office_tastezip.global.response.ResponseCode
import com.oz.office_tastezip.global.security.dto.CustomUserDetails
import com.oz.office_tastezip.global.security.service.CustomUserDetailService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder

class CustomAuthenticationProvider(
    private val passwordEncoder: PasswordEncoder,
    private val customUserDetailService: CustomUserDetailService
) : AuthenticationProvider {

    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        val email = authentication.name
        val password: String = authentication.credentials.toString()

        val customUserDetails: CustomUserDetails = customUserDetailService.loadUserByEmail(email)

        if (!passwordEncoder.matches(password, customUserDetails.passwordHash)) {
            throw ValidationFailureException(ResponseCode.INVALID_PASSWORD, "아이디 또는 비밀번호가 일치하지 않습니다.")
        }

        val authorities = customUserDetails.authorities
        customUserDetails.authorities = emptyList()
        return UsernamePasswordAuthenticationToken(customUserDetails, null, authorities)
    }
}
