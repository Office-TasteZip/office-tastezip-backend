package com.oz.office_tastezip.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum UserJob {

    DEVELOPMENT("개발"),
    PLANNING("기획"),
    DESIGN("디자인"),
    MARKETING("마케팅"),
    SALES("영업"),
    CS("고객지원"),
    HR("인사/총무"),
    FINANCE("재무/회계"),
    LEGAL("법무/감사"),
    EDUCATION("교육/연구"),
    RND("연구개발"),
    PRODUCTION("생산/제조"),
    LOGISTICS("물류/구매"),
    MEDICAL("의료/보건"),
    CONSULTING("컨설팅"),
    PUBLIC("공공/행정"),
    FREELANCER("프리랜서"),
    ETC("기타");

    private final String displayName;

    public static UserJob fromJobName(String job) {
        return Arrays.stream(values())
                .filter(userJob -> userJob.name().equalsIgnoreCase(job))
                .findFirst()
                .orElseThrow();
    }
}
