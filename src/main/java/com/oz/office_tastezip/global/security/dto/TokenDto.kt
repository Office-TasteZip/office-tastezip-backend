package com.oz.office_tastezip.global.security.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {

    private String accessToken;
    private String refreshToken;
    private String email;
    private String nickname;
    private String organizationName;
    private LocalDateTime lastLoginAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SerialDto {
        private String accessSerial;
        private String refreshSerial;

        @Override
        public String toString() {
            return "SerialDto{" +
                    "accessSerial='" + accessSerial + '\'' +
                    ", refreshSerial='" + refreshSerial + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String email;
        private String nickname;
        private LocalDateTime lastLoginAt;

        public TokenResponse(String accessToken, String email) {
            this.accessToken = accessToken;
            this.email = email;
        }
    }
}
