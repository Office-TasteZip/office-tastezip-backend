package com.oz.office_tastezip.domain.notice.repository

import com.oz.office_tastezip.domain.notice.Notice
import com.oz.office_tastezip.domain.notice.QNotice
import com.oz.office_tastezip.domain.notice.dto.NoticeUpdateDto
import com.oz.office_tastezip.domain.notice.enums.SearchType
import com.oz.office_tastezip.global.exception.DataNotFoundException
import com.oz.office_tastezip.global.support.toOrderSpecifiers
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
class NoticeRepositoryImpl(private val queryFactory: JPAQueryFactory) : NoticeRepositoryCustom {
    private val log = KotlinLogging.logger {}

    private val notice = QNotice.notice

    override fun searchNotices(searchType: SearchType, searchContent: String, pageable: Pageable): Page<Notice> {
        val predicate = searchCondition(searchType, searchContent)

        val orderSpecifiers = pageable.toOrderSpecifiers { property ->
            when (property) {
                "updatedAt" -> notice.updatedAt
                "title" -> notice.title
                else -> null
            }
        }

        val content = queryFactory
            .selectFrom(notice)
            .where(predicate)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(
                notice.isPinned.desc(),
                *orderSpecifiers.toTypedArray()
            )
            .fetch()

        val total = queryFactory
            .select(notice.count())
            .from(notice)
            .where(predicate)
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    override fun searchNoticeById(id: UUID): Notice {
        return queryFactory
            .selectFrom(notice)
            .where(notice.id.eq(id))
            .fetchOne() ?: throw DataNotFoundException()
    }

    override fun updateViewCount(id: UUID, viewCount: Int) {
        val updatedRows = queryFactory
            .update(notice)
            .set(notice.viewCount, viewCount + 1)
            .where(notice.id.eq(id))
            .execute()

        if (updatedRows == 0L) {
            log.warn("조회수 증가 실패 - 존재하지 않는 ID: $id")
        }
    }

    override fun updateNotice(id: UUID, author: String, noticeUpdateDto: NoticeUpdateDto) {
        val updatedRows = queryFactory
            .update(notice)
            .set(notice.title, noticeUpdateDto.title)
            .set(notice.content, noticeUpdateDto.content)
            .set(notice.isPinned, noticeUpdateDto.isPinned)
            .set(notice.author, author)
            .set(notice.updatedAt, LocalDateTime.now())
            .where(notice.id.eq(id))
            .execute()

        if (updatedRows == 0L) {
            log.warn("공지 업데이트 실패 - 존재하지 않는 ID: $id")
        }
    }

    override fun updatePinnedStatus(id: UUID, isPinned: Boolean) {
        val updatedRows = queryFactory
            .update(notice)
            .set(notice.isPinned, isPinned)
            .where(notice.id.eq(id))
            .execute()

        if (updatedRows == 0L) {
            log.warn("공지 상단 고정 실패 - 존재하지 않는 ID: $id")
        }
    }

    private fun searchCondition(searchType: SearchType, searchContent: String): BooleanExpression? {
        val keyword = searchContent
        return when (searchType) {
            SearchType.TITLE -> notice.title.containsIgnoreCase(keyword)
            SearchType.CONTENT -> notice.content.containsIgnoreCase(keyword)
            SearchType.AUTHOR -> notice.author.containsIgnoreCase(keyword)
            else -> notice.title.containsIgnoreCase(keyword)
                .or(notice.content.containsIgnoreCase(keyword))
        }
    }

}
