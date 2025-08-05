package com.oz.office_tastezip.domain.auth.service

import com.oz.office_tastezip.domain.auth.enums.EmailVerificationPurpose
import com.oz.office_tastezip.domain.auth.enums.EmailVerificationPurpose.RESET_PASSWORD
import com.oz.office_tastezip.domain.auth.enums.EmailVerificationPurpose.SIGNUP
import com.oz.office_tastezip.domain.user.User
import com.oz.office_tastezip.domain.user.dto.EmailVerificationRequestDto
import com.oz.office_tastezip.domain.user.repository.UserRepository
import com.oz.office_tastezip.global.exception.DataExistsException
import com.oz.office_tastezip.global.exception.DataNotFoundException
import com.oz.office_tastezip.global.exception.UserNotFoundException
import com.oz.office_tastezip.global.exception.ValidationFailureException
import com.oz.office_tastezip.global.response.ResponseCode
import com.oz.office_tastezip.global.response.ResponseCode.USER_NOT_FOUND
import com.oz.office_tastezip.global.util.RedisUtils
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
@Transactional
class AuthService(
    private val redisUtils: RedisUtils,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    private val log = KotlinLogging.logger {}

    fun updateLastLoginAt(uuid: String) {
        userRepository.updateLastLoginAtByUserUUID(uuid)
    }

    @Transactional(readOnly = true)
    fun selectUser(email: String): User {
        return userRepository.findByEmail(email)
            ?: throw UserNotFoundException("${USER_NOT_FOUND.message}, email: $email")
    }

    @Transactional(readOnly = true)
    fun countByEmail(email: String): Boolean {
        log.info { "count by email, target: $email" }
        return userRepository.countByEmail(email) != 0
    }

    @Transactional
    fun resetPassword(email: String, password: String) {
        val userId = selectUser(email).id
        userRepository.resetPassword(userId, passwordEncoder.encode(password))
    }

    fun checkEmailForSendMail(
        request: HttpServletRequest,
        emailVerificationPurpose: EmailVerificationPurpose,
        emailVerificationRequestDto: EmailVerificationRequestDto
    ) {
        val remoteAddr = request.remoteAddr
        val requestUri = request.requestURI
        val email = emailVerificationRequestDto.email

        log.info { "$requestUri|$remoteAddr|이메일 인증 발송 요청: $email, purpose: $emailVerificationPurpose" }

        val isEmailRegistered = countByEmail(email)

        when (emailVerificationPurpose) {
            SIGNUP -> if (isEmailRegistered) throw DataExistsException(ResponseCode.DUPLICATED_EMAIL)
            RESET_PASSWORD -> if (!isEmailRegistered) throw DataNotFoundException("가입된 이메일이 아닙니다.")
        }
    }

    fun checkVerificationEmail(
        requestEmail: String,
        requestCode: String,
        emailVerificationPurpose: EmailVerificationPurpose,
        httpServletRequest: HttpServletRequest
    ) {
        val requestUri = httpServletRequest.requestURI
        val remoteAddr = httpServletRequest.remoteAddr
        log.info { "$requestUri|$remoteAddr|이메일 인증 확인 요청: $requestEmail" }

        val originCode = redisUtils.get("${emailVerificationPurpose.codeKeyPrefix}$requestEmail") as String?
            ?: throw DataNotFoundException("인증번호가 만료되었습니다. 다시 요청해주시기 바랍니다.")

        log.info { "email: $requestEmail, origin code: $originCode, request code: $requestCode" }

        if (originCode != requestCode) {
            throw ValidationFailureException("인증번호가 일치하지 않습니다.")
        }

        redisUtils.set("${emailVerificationPurpose.verifyKeyPrefix}$requestEmail", "complete", 30, TimeUnit.MINUTES)
    }
}
