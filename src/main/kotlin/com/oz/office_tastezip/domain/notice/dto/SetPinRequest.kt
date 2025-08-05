package com.oz.office_tastezip.domain.notice.dto

data class SetPinRequest(
    val noticeId: String,
    val isPinned: Boolean
)
