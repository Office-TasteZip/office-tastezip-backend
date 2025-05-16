package com.oz.office_tastezip.domain.user.repository

import com.oz.office_tastezip.domain.user.QUser
import com.oz.office_tastezip.domain.user.User
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val em: EntityManager,
    private val queryFactory: JPAQueryFactory
) : UserRepositoryCustom {

    override fun findByName(name: String): List<User> {
        val user = QUser.user

        return queryFactory
            .selectFrom(user)
            .where(user.name.eq(name))
            .fetch()
    }
}
