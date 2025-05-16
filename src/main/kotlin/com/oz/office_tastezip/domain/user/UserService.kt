package com.oz.office_tastezip.domain.user

import com.oz.office_tastezip.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun searchByName(name: String): List<User> =
        userRepository.findByName(name)
}
