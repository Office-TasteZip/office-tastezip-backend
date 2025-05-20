package com.oz.office_tastezip.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

    ACTIVE("활성"),
    SUSPENDED("정지"),
    WITHDRAWN("탈퇴");

    private final String displayName;
}
