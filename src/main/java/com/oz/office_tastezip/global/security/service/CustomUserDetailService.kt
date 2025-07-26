package com.oz.office_tastezip.global.security.service

import com.oz.office_tastezip.domain.user.repository.UserRepository
import com.oz.office_tastezip.global.exception.UserNotFoundException
import com.oz.office_tastezip.global.exception.ValidationFailureException
import com.oz.office_tastezip.global.response.ResponseCode
import com.oz.office_tastezip.global.security.dto.CustomUserDetails
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class CustomUserDetailService(
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun loadUserByEmail(email: String): CustomUserDetails {
        val user = userRepository.findByEmail(email)
            ?: throw UserNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다.")

        val loginFailLimitCnt = 10
        if (user.loginFailCount >= loginFailLimitCnt) {
            throw ValidationFailureException(
                ResponseCode.ACCOUNT_LOCK,
                "10회 이상 로그인 실패하여 계정 잠김 상태입니다."
            )
        }

        return CustomUserDetails(user)
    }
}
