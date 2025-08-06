package com.oz.office_tastezip.domain.notice.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.oz.office_tastezip.domain.notice.Notice
import com.oz.office_tastezip.global.constant.TimeFormat

@JsonInclude(JsonInclude.Include.NON_NULL)
data class NoticeResponse(
    val id: String,
    val title: String,
    val content: String? = null,
    val author: String,
    val viewCount: Int,
    val createdAt: String? = null,
    val updatedAt: String,
    val isPinned: Boolean
) {
    companion object {
        fun summaryOf(notice: Notice): NoticeResponse {
            return NoticeResponse(
                id = notice.id.toString(),
                title = notice.title,
                author = notice.author,
                viewCount = notice.viewCount,
                updatedAt = TimeFormat.SEC.format(notice.updatedAt),
                isPinned = notice.isPinned
            )
        }

        fun detailOf(notice: Notice): NoticeResponse {
            return NoticeResponse(
                id = notice.id.toString(),
                title = notice.title,
                content = notice.content,
                author = notice.author,
                viewCount = notice.viewCount,
                createdAt = TimeFormat.SEC.format(notice.createdAt),
                updatedAt = TimeFormat.SEC.format(notice.updatedAt),
                isPinned = notice.isPinned
            )
        }
    }

}

