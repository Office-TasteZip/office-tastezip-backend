package com.oz.office_tastezip.domain.user

import com.oz.office_tastezip.domain.user.dto.UserRequestDto
import com.oz.office_tastezip.domain.user.repository.UserRepository
import com.oz.office_tastezip.global.exception.UserNotFoundException
import com.oz.office_tastezip.global.response.ResponseCode.USER_NOT_FOUND
import mu.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    private val log = KotlinLogging.logger {}

    fun register(userInsertRequest: UserRequestDto.UserInsertRequest) {
        val user = User.create(userInsertRequest, passwordEncoder)
        log.info { "Insert user info: $user" }
        userRepository.save(user)
    }

    @Transactional(readOnly = true)
    fun findByUserUUID(id: String): User {
        return userRepository.findByUserUUID(id)
            ?: throw UserNotFoundException("${USER_NOT_FOUND.message}, id: $id")
    }

    fun withdraw(id: String) {
        log.info { "withdraw user, id: $id" }
        userRepository.deleteByUserUUID(id)
    }

    fun update(userUpdateRequest: UserRequestDto.UserUpdateRequest) {
        log.info { "update user info: $userUpdateRequest" }
        userRepository.updateByUserUUID(userUpdateRequest)
    }
}
