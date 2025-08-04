package com.oz.office_tastezip.domain.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

object UserRequestDto {

    data class UserInsertRequest(
        @field:NotBlank(message = "이메일은 필수 입력값입니다.")
        @field:Email(message = "이메일 형식이 아닙니다.")
        val email: String,

        @field:NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @field:Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        val password: String,

        @field:NotBlank(message = "비밀번호 확인 값은 필수 입력값입니다.")
        @field:Size(min = 8, max = 20, message = "비밀번호 확인 값은 8자 이상 20자 이하로 입력해주세요.")
        val confirmPassword: String,

        @field:NotBlank(message = "닉네임은 필수 입력값입니다.")
        @field:Size(max = 20, message = "닉네임은 20자 이하로 입력해주세요.")
        val nickname: String,

        @field:NotBlank(message = "직무는 필수입니다.")
        val job: String,

        @field:NotBlank(message = "직책은 필수입니다.")
        val position: String,

        @field:NotBlank(message = "입사년도는 필수입니다.")
        @field:Pattern(regexp = "^[0-9]{4}$", message = "입사년도는 4자리 숫자여야 합니다.")
        val joinYear: String,

        @field:NotBlank(message = "기업명은 필수입니다.")
        val organizationName: String,

        val marketingAgree: Boolean = false,
        val privacyAgree: Boolean = false,  // 개인정보 수집 및 이용 동의 여부(필수)
        val termsAgree: Boolean = false,    // 서비스 이용약관 동의 여부(필수)
    )

    data class UserUpdateRequest(
        @field:NotBlank(message = "사용자 고유 ID는 필수 입력값입니다.")
        val id: String?,

        @field:NotBlank(message = "닉네임은 필수 입력값입니다.")
        @field:Size(max = 20, message = "닉네임은 20자 이하로 입력해주세요.")
        val nickname: String,

        @field:NotBlank(message = "직무는 필수입니다.")
        val job: String,

        @field:NotBlank(message = "직책은 필수입니다.")
        val position: String,

        @field:NotBlank(message = "입사년도는 필수입니다.")
        @field:Pattern(regexp = "^[0-9]{4}$", message = "입사년도는 4자리 숫자여야 합니다.")
        val joinYear: String,

        val marketingAgree: Boolean = false
    )
}
