package com.oz.office_tastezip.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailVerificationRequestDto {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

    public EmailVerificationRequestDto() {
    }

    public EmailVerificationRequestDto(String email) {
        this.email = email;
    }
}
