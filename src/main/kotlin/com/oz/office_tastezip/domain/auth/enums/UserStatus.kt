package com.oz.office_tastezip.domain.auth.enums

enum class UserStatus(val displayName: String) {
    ACTIVE("활성"),
    SUSPENDED("정지"),
    WITHDRAWN("탈퇴");

    companion object {
        fun fromDisplayName(displayName: String): UserStatus {
            return entries.firstOrNull { it.displayName == displayName }
                ?: throw IllegalArgumentException("Invalid display name: $displayName")
        }
    }
}
