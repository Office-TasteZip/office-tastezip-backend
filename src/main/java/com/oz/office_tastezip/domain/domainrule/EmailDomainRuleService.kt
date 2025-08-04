package com.oz.office_tastezip.domain.domainrule

import com.oz.office_tastezip.domain.domainrule.enums.DomainType
import com.oz.office_tastezip.domain.domainrule.repository.EmailDomainRuleRepository
import com.oz.office_tastezip.global.exception.RequestFailureException
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EmailDomainRuleService(
    private val emailDomainRuleRepository: EmailDomainRuleRepository
) {

    private val log = KotlinLogging.logger {}

    @Transactional(readOnly = true)
    fun checkDomainAvailable(domain: String) {
        log.info { "check domain available, request domain: $domain" }

        emailDomainRuleRepository.findByDomain(domain)?.let { rule ->
            if (rule.domainType == DomainType.BLACKLIST) {
                throw RequestFailureException("[$domain]은(는) 차단 대상 도메인입니다.")
            }

            if (!rule.enabled) {
                throw RequestFailureException("[$domain]은(는) 비활성화된 도메인입니다.")
            }
        }
    }

}
