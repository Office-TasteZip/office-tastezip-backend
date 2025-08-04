package com.oz.office_tastezip.domain.organization.repository

import com.oz.office_tastezip.domain.organization.Organization
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OrganizationRepository : JpaRepository<Organization, UUID> {
    fun findByDomain(domain: String): Organization?
}
