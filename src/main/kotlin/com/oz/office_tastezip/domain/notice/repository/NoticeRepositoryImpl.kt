package com.oz.office_tastezip.domain.notice.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import mu.KotlinLogging
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class NoticeRepositoryImpl(private val queryFactory: JPAQueryFactory): NoticeRepositoryCustom {

    private val log = KotlinLogging.logger {}
}
