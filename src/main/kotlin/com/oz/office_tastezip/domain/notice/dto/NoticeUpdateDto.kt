package com.oz.office_tastezip.domain.notice.dto

import jakarta.validation.constraints.NotBlank

data class NoticeUpdateDto(
    @field:NotBlank(message = "제목은 필수 입력값입니다.")
    val title: String,

    @field:NotBlank(message = "본문 내용은 필수 입력값입니다.")
    val content: String,
)
