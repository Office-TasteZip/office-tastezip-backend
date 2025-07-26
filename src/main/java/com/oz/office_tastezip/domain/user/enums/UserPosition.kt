package com.oz.office_tastezip.domain.user.enums

import com.oz.office_tastezip.global.exception.DataNotFoundException

enum class UserPosition(val displayName: String) {
    INTERN("인턴"),
    JUNIOR("주니어/사원"),
    SENIOR("선임/주임/대리"),
    LEAD("책임/과장"),
    PRINCIPAL("수석/차장"),
    HEAD("전임/부장/팀장"),
    EXECUTIVE("임원"),
    CEO("대표"),

    FREELANCER("프리랜서"),
    ETC("기타");

    companion object {
        fun fromPositionName(position: String): UserPosition {
            return entries.firstOrNull { it.name.equals(position, ignoreCase = true) }
                ?: throw DataNotFoundException("[$position]은 존재하지 않는 직급입니다.")
        }
    }
}
