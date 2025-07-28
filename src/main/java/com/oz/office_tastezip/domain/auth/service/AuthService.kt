package com.oz.office_tastezip.domain.auth.service

import com.oz.office_tastezip.domain.user.User
import com.oz.office_tastezip.domain.user.repository.UserRepository
import com.oz.office_tastezip.global.exception.UserNotFoundException
import com.oz.office_tastezip.global.response.ResponseCode.USER_NOT_FOUND
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository
) {

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
        return userRepository.countByEmail(email) == 0
    }
}
