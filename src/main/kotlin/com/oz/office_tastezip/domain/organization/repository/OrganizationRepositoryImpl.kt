package com.oz.office_tastezip.domain.organization.repository

import com.oz.office_tastezip.domain.organization.QOrganization
import com.oz.office_tastezip.domain.organization.dto.SearchOrganizationNameDto
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class OrganizationRepositoryImpl(private val queryFactory: JPAQueryFactory) : OrganizationRepositoryCustom {

    private val organization = QOrganization.organization

    override fun findOrganizationByName(name: String): List<SearchOrganizationNameDto> {
        return queryFactory
            .select(
                Projections.constructor(
                    SearchOrganizationNameDto::class.java,
                    organization.id,
                    organization.organizationName.`as`("name")
                )
            )
            .from(organization)
            .where(
                organization.deletedAt.isNull,
                organization.organizationName.like("$name%")
            )
            .orderBy(organization.organizationName.asc())
            .fetch()
    }

}
