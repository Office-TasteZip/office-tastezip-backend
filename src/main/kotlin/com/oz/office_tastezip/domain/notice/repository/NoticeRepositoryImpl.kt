package com.oz.office_tastezip.domain.notice.repository

import com.oz.office_tastezip.domain.notice.Notice
import com.oz.office_tastezip.domain.notice.QNotice
import com.oz.office_tastezip.domain.notice.enums.SearchType
import com.oz.office_tastezip.global.support.toOrderSpecifiers
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class NoticeRepositoryImpl(private val queryFactory: JPAQueryFactory) : NoticeRepositoryCustom {

    private val notice = QNotice.notice

    override fun searchNotices(searchType: SearchType, searchContent: String, pageable: Pageable): Page<Notice> {
        val predicate = searchCondition(searchType, searchContent)

        val orderSpecifiers = pageable.toOrderSpecifiers { property ->
            when (property) {
                "updatedAt" -> notice.createdAt
                "title" -> notice.title
                else -> null
            }
        }

        val content = queryFactory
            .selectFrom(notice)
            .where(predicate)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*orderSpecifiers.toTypedArray())
            .fetch()

        val total = queryFactory
            .select(notice.count())
            .from(notice)
            .where(predicate)
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
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
