package com.oz.office_tastezip.domain.notice.dto

import com.oz.office_tastezip.domain.notice.Notice
import com.oz.office_tastezip.domain.notice.enums.SearchType
import com.oz.office_tastezip.global.constant.TimeFormat
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

object NoticeDto {

    data class NoticeRequest(
        val searchType: SearchType = SearchType.TOTAL,
        @field:NotBlank val searchContent: String = "",
        @field:Min(1) val page: Int = 1,
        @field:Min(1) val limit: Int = 10
    )

    data class NoticeResponse(
        val sequence: Int,
        val id: String,
        val title: String,
        val content: String,
        val author: String,
        val viewCount: Int,
        val createdAt: String,
        val updatedAt: String
    ) {
        companion object {
            fun of(notice: Notice, sequence: Int): NoticeResponse {
                return NoticeResponse(
                    sequence = sequence,
                    id = notice.id.toString(),
                    title = notice.title,
                    content = notice.content,
                    author = notice.author,
                    viewCount = notice.viewCount,
                    createdAt = TimeFormat.SEC.format(notice.createdAt),
                    updatedAt = TimeFormat.SEC.format(notice.updatedAt)
                )
            }
        }
    }
}
