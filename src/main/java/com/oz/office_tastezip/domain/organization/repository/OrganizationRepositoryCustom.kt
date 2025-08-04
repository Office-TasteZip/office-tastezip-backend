package com.oz.office_tastezip.domain.organization.repository

interface OrganizationRepositoryCustom {
    fun findOrganizationByName(name: String): List<String>
}
