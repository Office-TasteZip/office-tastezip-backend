package com.oz.office_tastezip.domain.auth.service

import com.oz.office_tastezip.domain.user.User
import com.oz.office_tastezip.domain.user.repository.UserRepository
import com.oz.office_tastezip.global.exception.UserNotFoundException
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class AuthService(
    private val userRepository: UserRepository
) {

    fun updateLastLoginAt(uuid: String) {
        userRepository.updateLastLoginAtByUserUUID(uuid)
    }

    fun selectUser(email: String): User {
        return userRepository.findByEmail(email) ?: throw UserNotFoundException()
    }
}
