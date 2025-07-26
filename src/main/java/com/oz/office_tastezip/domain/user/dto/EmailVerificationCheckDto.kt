package com.oz.office_tastezip.domain.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EmailVerificationCheckDto(

    @field:NotBlank(message = "이메일은 필수 입력값입니다.")
    @field:Email(message = "이메일 형식이 아닙니다.")
    val email: String,

    @field:NotBlank(message = "인증 코드는 필수 입력값입니다.")
    val code: String
)
