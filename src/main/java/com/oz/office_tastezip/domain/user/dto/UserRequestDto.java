package com.oz.office_tastezip.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequestDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInsertRequest {

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        private String password;

        @NotBlank(message = "비밀번호 확인 값은 필수 입력값입니다.")
        @Size(min = 8, max = 20, message = "비밀번호 확인 값은 8자 이상 20자 이하로 입력해주세요.")
        private String confirmPassword;

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        @Size(max = 20, message = "닉네임은 20자 이하로 입력해주세요.")
        private String nickname;

        @NotBlank(message = "직무는 필수입니다.")
        private String job;

        @NotBlank(message = "직책은 필수입니다.")
        private String position;

        @NotBlank(message = "입사년도는 필수입니다.")
        @Pattern(regexp = "^[0-9]{4}$", message = "입사년도는 4자리 숫자여야 합니다.")
        private String joinYear;

        private boolean marketingOptIn;

        private String profileImageUrl;

        public void passwordHashing() {
            // TODO BCrypt
        }

        @Override
        public String toString() {
            return "UserInsertRequest{" +
                    "email='" + email + '\'' +
                    ", password='" + password + '\'' +
                    ", confirmPassword='" + confirmPassword + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", job='" + job + '\'' +
                    ", position='" + position + '\'' +
                    ", joinYear='" + joinYear + '\'' +
                    ", marketingOptIn=" + marketingOptIn +
                    ", profileImageUrl='" + profileImageUrl + '\'' +
                    '}';
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserUpdateRequest {

        @NotBlank(message = "사용자 고유 ID는 필수 입력값입니다.")
        private String id;

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        @Size(max = 20, message = "닉네임은 20자 이하로 입력해주세요.")
        private String nickname;

        @NotBlank(message = "직무는 필수입니다.")
        private String job;

        @NotBlank(message = "직책은 필수입니다.")
        private String position;

        @NotBlank(message = "입사년도는 필수입니다.")
        @Pattern(regexp = "^[0-9]{4}$", message = "입사년도는 4자리 숫자여야 합니다.")
        private String joinYear;

        private boolean marketingOptIn;

        private String profileImageUrl;

        @Override
        public String toString() {
            return "UserUpdateRequest{" +
                    "id='" + id + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", job='" + job + '\'' +
                    ", position='" + position + '\'' +
                    ", joinYear='" + joinYear + '\'' +
                    ", marketingOptIn=" + marketingOptIn +
                    ", profileImageUrl='" + profileImageUrl + '\'' +
                    '}';
        }
    }
}
