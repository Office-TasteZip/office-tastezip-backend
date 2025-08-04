package com.oz.office_tastezip.domain.domainrule

import com.oz.office_tastezip.domain.BaseEntity
import com.oz.office_tastezip.domain.domainrule.enums.DomainType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "TBL_OTZ_EMAIL_DOMAIN_RULE")
class EmailDomainRule (

    @Column(name = "domain", nullable = false, unique = true)
    val domain: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "domain_type", nullable = false)
    val domainType: DomainType,

    @Column(name = "enabled", nullable = false)
    val enabled: Boolean,

    @Column(name = "memo", columnDefinition = "TEXT")
    val memo: String? = null
) : BaseEntity() {
}

