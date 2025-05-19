package com.oz.office_tastezip.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInsertRequest {
        private String email;
        private String password;
        private String nickname;
        private String job;
        private String position;
        private String joinYear;
        private boolean marketingOptIn;
        private String profileImageUrl;
    }
}
