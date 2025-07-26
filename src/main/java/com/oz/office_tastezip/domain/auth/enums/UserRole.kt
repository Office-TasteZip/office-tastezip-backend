package com.oz.office_tastezip.domain.auth.enums

enum class UserRole(val label: String) {
    ROLE_ADMIN("관리자"),
    ROLE_USER("사용자");

    companion object {
        fun fromLabel(label: String): UserRole =
            entries.firstOrNull { it.label == label }
                ?: throw IllegalArgumentException("Invalid label: $label")
    }
}
