package com.oz.office_tastezip.domain.user.dto

import com.oz.office_tastezip.domain.user.User
import com.oz.office_tastezip.global.constant.TimeFormat.SEC
import java.util.UUID

data class UserResponseDto(
    val id: UUID? = null,
    val email: String? = null,
    val nickname: String? = null,
    val job: String? = null,
    val position: String? = null,
    val joinYear: String? = null,
    val marketingOptIn: Boolean = false,
    val profileImageUrl: String? = null,
    val lastLoginAt: String? = null,
    val createdAt: String? = null
) {
    companion object {
        fun of(user: User): UserResponseDto {
            return UserResponseDto(
                id = user.id,
                email = user.email,
                nickname = user.nickname,
                job = user.job.name,
                position = user.position.name,
                joinYear = user.joinYear,
                marketingOptIn = user.marketingOptIn,
                profileImageUrl = user.profileImageUrl,
                lastLoginAt = SEC.format(user.lastLoginAt),
                createdAt = SEC.format(user.createdAt)
            )
        }
    }
}
