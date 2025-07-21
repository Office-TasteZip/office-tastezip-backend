package com.oz.office_tastezip.global.security.dto;

import com.oz.office_tastezip.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {
    private String uuid;
    private String email;
    private String passwordHash;
    private String userIpAddress;
    private String remoteUserIpAddress;
    private String nickname;
    private String job;
    private String position;
    private String joinYear;
    private String status;
    private String profileImageUrl;
    private boolean marketingOptIn;
    private LocalDateTime lastLoginAt;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetails(User user, String remoteIpAddress) {
        this.uuid = user.getId().toString();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.userIpAddress = user.getLastLoginIp();
        this.remoteUserIpAddress = remoteIpAddress;
        this.nickname = user.getNickname();
        this.job = user.getJob().name();
        this.position = user.getPosition().name();
        this.joinYear = user.getJoinYear();
        this.status = user.getStatus().name();
        this.profileImageUrl = user.getProfileImageUrl();
        this.marketingOptIn = user.isMarketingOptIn();
        this.lastLoginAt = user.getLastLoginAt();
        this.authorities = Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "uuid='" + uuid + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", userIpAddress='" + userIpAddress + '\'' +
                ", remoteUserIpAddress='" + remoteUserIpAddress + '\'' +
                ", nickname='" + nickname + '\'' +
                ", job='" + job + '\'' +
                ", position='" + position + '\'' +
                ", joinYear='" + joinYear + '\'' +
                ", status='" + status + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", marketingOptIn=" + marketingOptIn +
                ", lastLoginAt=" + lastLoginAt +
                ", authorities=" + authorities +
                '}';
    }
}
