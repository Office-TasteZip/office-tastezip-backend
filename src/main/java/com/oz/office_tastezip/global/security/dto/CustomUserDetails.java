package com.oz.office_tastezip.global.security.dto;

import com.oz.office_tastezip.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private String uuid;
    private String email;
    private String passwordHash;
    private String userIpAddress;
    @Setter
    private String remoteUserIpAddress;
    private String nickname;
    private String job;
    private String position;
    private String joinYear;
    private String role;
    private String status;
    private String profileImageUrl;
    private boolean marketingOptIn;
    private LocalDateTime lastLoginAt;
    @Setter
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.uuid = user.getId().toString();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.userIpAddress = user.getLastLoginIp();
        this.nickname = user.getNickname();
        this.job = user.getJob().name();
        this.position = user.getPosition().name();
        this.joinYear = user.getJoinYear();
        this.role = user.getRole().name();
        this.status = user.getStatus().name();
        this.profileImageUrl = user.getProfileImageUrl();
        this.marketingOptIn = user.isMarketingOptIn();
        this.lastLoginAt = user.getLastLoginAt();
        this.authorities = Collections.singleton(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String toString() {
        return "CustomUserDetail{" +
                "uuid='" + uuid + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", userIpAddress='" + userIpAddress + '\'' +
                ", remoteUserIpAddress='" + remoteUserIpAddress + '\'' +
                ", nickname='" + nickname + '\'' +
                ", job='" + job + '\'' +
                ", position='" + position + '\'' +
                ", joinYear='" + joinYear + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", marketingOptIn=" + marketingOptIn +
                ", lastLoginAt=" + lastLoginAt +
                ", authorities=" + authorities +
                '}';
    }
}
