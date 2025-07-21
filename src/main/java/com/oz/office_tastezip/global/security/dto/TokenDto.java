package com.oz.office_tastezip.global.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
