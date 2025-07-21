package com.oz.office_tastezip.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailVerificationCheckDto {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;
    private String code;

    public EmailVerificationCheckDto() {
    }

    public EmailVerificationCheckDto(String email, String code) {
        this.email = email;
        this.code = code;
    }
}
