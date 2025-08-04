package com.oz.office_tastezip.domain.organization.repository

import com.oz.office_tastezip.domain.organization.QOrganization
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class OrganizationRepositoryImpl(private val queryFactory: JPAQueryFactory) : OrganizationRepositoryCustom {

    private val organization = QOrganization.organization

    @Transactional(readOnly = true)
    override fun findOrganizationByName(name: String): List<String> {
        return queryFactory
            .select(organization.organizationName)
            .from(organization)
            .where(
                organization.deletedAt.isNull,
                organization.organizationName.like("$name%")
            )
            .orderBy(organization.organizationName.asc())
            .fetch()
    }

}
