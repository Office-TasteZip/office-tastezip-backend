package com.oz.office_tastezip.global.util

import com.oz.office_tastezip.global.exception.InvalidTokenException
import com.oz.office_tastezip.global.exception.UserNotFoundException
import com.oz.office_tastezip.global.exception.ValidationFailureException
import com.oz.office_tastezip.global.security.dto.CustomUserDetails
import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtils {

    fun getAuthenticatedUserDetail(): CustomUserDetails {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw InvalidTokenException("인증 정보 없음")

        val userDetails = authentication.details
            ?: throw UserNotFoundException("인증 사용자 정보 없음")

        return userDetails as? CustomUserDetails
            ?: throw ValidationFailureException("CustomUserDetails 타입이 아님")
    }

    fun getCurrentUserOrNull(): CustomUserDetails? {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication?.principal is CustomUserDetails)
            authentication.principal as CustomUserDetails
        else null
    }

}
