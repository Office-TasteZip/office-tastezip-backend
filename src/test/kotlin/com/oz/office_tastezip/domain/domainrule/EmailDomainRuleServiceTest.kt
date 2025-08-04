package com.oz.office_tastezip.domain.domainrule

import com.oz.office_tastezip.domain.domainrule.enums.DomainType
import com.oz.office_tastezip.domain.domainrule.repository.EmailDomainRuleRepository
import com.oz.office_tastezip.global.exception.RequestFailureException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@Rollback
@Transactional
@SpringBootTest
class EmailDomainRuleServiceTest(
    @Autowired private val emailDomainRuleService: EmailDomainRuleService,
    @Autowired private val emailDomainRuleRepository: EmailDomainRuleRepository
) : FunSpec({

    context("도메인 유효성 검사") {
        test("차단된 도메인일 경우 예외 발생") {
            val saved = emailDomainRuleRepository.save(
                EmailDomainRule(
                    domain = "example.com",
                    domainType = DomainType.BLACKLIST,
                    enabled = true,
                    memo = ""
                )
            )

            val exception = shouldThrow<RequestFailureException> {
                emailDomainRuleService.checkDomainAvailable(saved.domain)
            }

            exception.message shouldBe "[example.com]은(는) 차단 대상 도메인입니다."
        }

        test("비활성화된 도메인일 경우 예외 발생") {
            val saved = emailDomainRuleRepository.save(
                EmailDomainRule(
                    domain = "inactive.com",
                    domainType = DomainType.WHITELIST,
                    enabled = false,
                    memo = ""
                )
            )

            val exception = shouldThrow<RequestFailureException> {
                emailDomainRuleService.checkDomainAvailable(saved.domain)
            }

            exception.message shouldBe "[inactive.com]은(는) 비활성화된 도메인입니다."
        }

        test("허용된 도메인은 정상 통과") {
            val saved = emailDomainRuleRepository.save(
                EmailDomainRule(
                    domain = "officezip.co.kr",
                    domainType = DomainType.WHITELIST,
                    enabled = true,
                    memo = ""
                )
            )

            emailDomainRuleService.checkDomainAvailable(saved.domain)
        }
    }
})
