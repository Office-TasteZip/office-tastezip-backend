package com.oz.office_tastezip.domain.user.repository

import com.oz.office_tastezip.domain.user.User
import com.oz.office_tastezip.domain.user.dto.UserRequestDto
import java.util.UUID

interface UserRepositoryCustom {
    fun findByUserUUID(uuid: String): User?
    fun countByEmail(email: String): Int
    fun deleteByUserUUID(uuid: String)
    fun updateByUserUUID(userUpdateRequest: UserRequestDto.UserUpdateRequest)
    fun updateLastLoginAtByUserUUID(uuid: String)
    fun resetPassword(uuid : UUID, passwordHash: String)
}
