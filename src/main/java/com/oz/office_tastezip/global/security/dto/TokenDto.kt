package com.oz.office_tastezip.global.security.dto

import java.time.LocalDateTime

data class TokenDto(
    val accessToken: String,
    val refreshToken: String,
    val email: String? = null,
    val nickname: String? = null,
    val organizationName: String? = null,
    val lastLoginAt: LocalDateTime? = null
) {

    data class SerialDto(
        val accessSerial: String = "",
        val refreshSerial: String = ""
    )

    data class TokenResponse(
        var accessToken: String,
        var email: String,
        var nickname: String? = null,
        var lastLoginAt: LocalDateTime? = null
    )
}
