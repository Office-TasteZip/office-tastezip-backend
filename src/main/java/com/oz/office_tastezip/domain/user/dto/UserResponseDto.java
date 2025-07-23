package com.oz.office_tastezip.domain.user.dto;

import com.oz.office_tastezip.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.oz.office_tastezip.global.constant.TimeFormat.SEC;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private UUID id;
    private String email;
    private String nickname;
    private String job;
    private String position;
    private String joinYear;
    private boolean marketingOptIn;
    private String profileImageUrl;
    private String lastLoginAt;
    private String createdAt;

    public static UserResponseDto of(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .job(user.getJob().name())
                .position(user.getPosition().name())
                .joinYear(user.getJoinYear())
                .marketingOptIn(user.isMarketingOptIn())
                .profileImageUrl(user.getProfileImageUrl())
                .lastLoginAt(SEC.getString(user.getLastLoginAt()))
                .createdAt(SEC.getString(user.getCreatedAt()))
                .build();
    }

    @Override
    public String toString() {
        return "UserResponseDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
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
