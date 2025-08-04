package com.oz.office_tastezip.domain.domainrule.repository

import com.oz.office_tastezip.domain.domainrule.EmailDomainRule
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface EmailDomainRuleRepository: JpaRepository<EmailDomainRule, UUID> {
    fun findByDomain(domain: String): EmailDomainRule?
}
