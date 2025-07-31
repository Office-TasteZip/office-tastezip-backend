package com.oz.office_tastezip.domain.auth.enums

enum class EmailVerificationPurpose(
    val subject: String,
    val codeKeyPrefix: String,
    val attemptKeyPrefix: String,
    val verifyKeyPrefix: String
) {
    SIGNUP(
        subject = "[오피스 맛집] 회원가입 인증 메일입니다.",
        codeKeyPrefix = "signup-email-code:",
        attemptKeyPrefix = "signup-email-attempt:",
        verifyKeyPrefix = "signup-email-verify:"
    ),
    RESET_PASSWORD(
        subject = "[오피스 맛집] 비밀번호 초기화 인증 메일입니다.",
        codeKeyPrefix = "reset-password-code:",
        attemptKeyPrefix = "reset-password-attempt:",
        verifyKeyPrefix = "reset-password-verify:"
    )
}
