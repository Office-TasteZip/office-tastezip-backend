package com.oz.office_tastezip.domain.organization.repository

import com.oz.office_tastezip.domain.organization.dto.SearchOrganizationNameDto

interface OrganizationRepositoryCustom {
    fun findOrganizationByName(name: String): List<SearchOrganizationNameDto>
}
