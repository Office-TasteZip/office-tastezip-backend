package com.oz.office_tastezip.domain.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ResetPasswordDto(
    @field:NotBlank(message = "이메일은 필수 입력값입니다.")
    @field:Email(message = "이메일 형식이 아닙니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @field:Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    val password: String,

    @field:NotBlank(message = "비밀번호 확인 값은 필수 입력값입니다.")
    @field:Size(min = 8, max = 20, message = "비밀번호 확인 값은 8자 이상 20자 이하로 입력해주세요.")
    val confirmPassword: String,
)
