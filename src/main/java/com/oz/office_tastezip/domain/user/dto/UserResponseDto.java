package com.oz.office_tastezip.domain.user.dto;

import com.oz.office_tastezip.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private String email;
    private String nickname;
    private String job;
    private String position;
    private String joinYear;
    private boolean marketingOptIn;
    private String profileImageUrl;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;

    public static UserResponseDto of(User user) {
        return UserResponseDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .job(user.getJob().name())
                .position(user.getPosition().name())
                .marketingOptIn(user.isMarketingOptIn())
                .profileImageUrl(user.getProfileImageUrl())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public String toString() {
        return "UserResponseDto{" +
                "email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", job='" + job + '\'' +
                ", position='" + position + '\'' +
                ", joinYear='" + joinYear + '\'' +
                ", marketingOptIn=" + marketingOptIn +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", lastLoginAt=" + lastLoginAt +
                ", createdAt=" + createdAt +
                '}';
    }
}
