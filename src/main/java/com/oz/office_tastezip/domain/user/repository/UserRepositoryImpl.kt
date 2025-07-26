package com.oz.office_tastezip.domain.user.repository

import com.oz.office_tastezip.domain.auth.enums.UserStatus
import com.oz.office_tastezip.domain.user.QUser
import com.oz.office_tastezip.domain.user.User
import com.oz.office_tastezip.domain.user.dto.UserRequestDto
import com.oz.office_tastezip.domain.user.enums.UserJob
import com.oz.office_tastezip.domain.user.enums.UserPosition
import com.querydsl.jpa.impl.JPAQueryFactory
import mu.KotlinLogging
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

private val log = KotlinLogging.logger {}

@Repository
@Transactional
open class UserRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : UserRepositoryCustom {

    private val user = QUser.user

    @Transactional(readOnly = true)
    override fun findByUserUUID(uuid: String): User? {
        return queryFactory.selectFrom(user)
            .where(user.deletedAt.isNull.and(user.id.eq(UUID.fromString(uuid))))
            .fetchOne()
    }

    @Transactional(readOnly = true)
    override fun countByEmail(email: String): Int {
        return queryFactory.selectFrom(user)
            .where(user.email.eq(email))
            .fetch()
            .size
    }

    override fun deleteByUserUUID(uuid: String) {
        queryFactory.update(user)
            .set(user.deletedAt, LocalDateTime.now())
            .set(user.status, UserStatus.WITHDRAWN)
            .where(user.id.eq(UUID.fromString(uuid)))
            .execute()
    }

    override fun updateByUserUUID(userUpdateRequest: UserRequestDto.UserUpdateRequest) {
        log.info { "Update user info: $userUpdateRequest" }

        val updated = queryFactory.update(user)
            .set(user.nickname, userUpdateRequest.nickname)
            .set(user.job, UserJob.fromJobName(userUpdateRequest.job))
            .set(user.position, UserPosition.fromPositionName(userUpdateRequest.position))
            .set(user.joinYear, userUpdateRequest.joinYear)
            .set(user.marketingOptIn, userUpdateRequest.marketingOptIn)
            .where(user.deletedAt.isNull.and(user.id.eq(UUID.fromString(userUpdateRequest.id))))
            .execute()

        log.info { "Updated rows: $updated" }
    }

    override fun updateLastLoginAtByUserUUID(uuid: String) {
        log.info { "Update last login at by user uuid: $uuid" }

        queryFactory.update(user)
            .set(user.lastLoginAt, LocalDateTime.now())
            .where(user.deletedAt.isNull.and(user.id.eq(UUID.fromString(uuid))))
            .execute()
    }
}
