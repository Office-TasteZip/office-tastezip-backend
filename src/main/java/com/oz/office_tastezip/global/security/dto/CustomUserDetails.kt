package com.oz.office_tastezip.global.security.dto

import com.oz.office_tastezip.domain.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDateTime

class CustomUserDetails(
    val uuid: String,
    val email: String,
    val passwordHash: String,
    val userIpAddress: String?,
    var remoteUserIpAddress: String? = null,
    val nickname: String,
    val job: String,
    val position: String,
    val joinYear: String,
    val role: String,
    val status: String,
    val profileImageUrl: String?,
    val marketingAgree: Boolean,
    val lastLoginAt: LocalDateTime?,
    var authorities: Collection<GrantedAuthority> = listOf(SimpleGrantedAuthority(role))
) {
    constructor(user: User) : this(
        uuid = user.id.toString(),
        email = user.email,
        passwordHash = user.passwordHash,
        userIpAddress = user.lastLoginIp,
        nickname = user.nickname,
        job = user.job.name,
        position = user.position.name,
        joinYear = user.joinYear,
        role = user.role.name,
        status = user.status.name,
        profileImageUrl = user.profileImageUrl,
        marketingAgree = user.marketingAgree,
        lastLoginAt = user.lastLoginAt,
        authorities = listOf(SimpleGrantedAuthority(user.role.name))
    )
}
