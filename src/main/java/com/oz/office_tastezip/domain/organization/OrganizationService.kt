package com.oz.office_tastezip.domain.organization

import com.oz.office_tastezip.domain.organization.dto.SearchOrganizationNameDto
import com.oz.office_tastezip.domain.organization.repository.OrganizationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrganizationService(
    private val organizationRepository: OrganizationRepository
) {

    fun findOrCreateOrganization(domain: String, name: String): Organization {
        return organizationRepository.findByDomain(domain)
            ?: organizationRepository.save(Organization(domain = domain, organizationName = name))
    }

    fun findOrganization(organization: Organization): Organization {
        return organizationRepository.findById(organization.id).orElse(null)
    }

    fun findOrganizationByName(name: String): List<SearchOrganizationNameDto> {
        return organizationRepository.findOrganizationByName(name)
    }
}
