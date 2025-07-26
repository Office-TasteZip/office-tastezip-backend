package com.oz.office_tastezip.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum UserPosition {

    INTERN("인턴"),
    JUNIOR("주니어/사원"),
    SENIOR("선임/주임/대리"),
    LEAD("책임/과장"),
    PRINCIPAL("수석/차장"),
    HEAD("전임/부장/팀장"),
    EXECUTIVE("임원"),
    CEO("대표"),

    FREELANCER("프리랜서"),
    ETC("기타");

    private final String displayName;

    public static UserPosition fromPositionName(String position) {
        return Arrays.stream(values())
                .filter(userPosition -> userPosition.name().equalsIgnoreCase(position))
                .findFirst()
                .orElseThrow();
    }
}
