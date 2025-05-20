package com.oz.office_tastezip.domain.user;

import com.oz.office_tastezip.domain.BaseEntity;
import com.oz.office_tastezip.domain.user.dto.UserRequestDto;
import com.oz.office_tastezip.domain.user.enums.UserJob;
import com.oz.office_tastezip.domain.user.enums.UserPosition;
import com.oz.office_tastezip.enums.UserRole;
import com.oz.office_tastezip.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "TBL_OTZ_USER")
public class User extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "password_updated_at", nullable = false)
    private LocalDateTime passwordUpdatedAt;

    @Column(name = "nickname", nullable = false, length = 100)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "job", nullable = false, length = 50)
    private UserJob job;

    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false, length = 50)
    private UserPosition position;

    @Column(name = "join_year", nullable = false, length = 4)
    private String joinYear;

    @Column(name = "marketing_opt_in")
    private boolean marketingOptIn;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_failed_login_at")
    private LocalDateTime lastFailedLoginAt;

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;

    // TODO OrganizationID

    protected User() {
    }

    public static User create(UserRequestDto.UserInsertRequest userInsertRequest) {
        return User.builder()
                .email(userInsertRequest.getEmail())
                .passwordHash(userInsertRequest.getPassword())  // TODO BCrypt
                .passwordUpdatedAt(LocalDateTime.now())
                .nickname(userInsertRequest.getNickname())
                .job(UserJob.fromJobName(userInsertRequest.getJob()))
                .position(UserPosition.fromPositionName(userInsertRequest.getPosition()))
                .joinYear(userInsertRequest.getJoinYear())
                .marketingOptIn(userInsertRequest.isMarketingOptIn())
                .role(UserRole.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .profileImageUrl(userInsertRequest.getProfileImageUrl())
                .build();
    }

}
