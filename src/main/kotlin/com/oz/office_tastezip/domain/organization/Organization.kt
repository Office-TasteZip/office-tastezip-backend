package com.oz.office_tastezip.domain.organization

import com.oz.office_tastezip.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "TBL_OTZ_ORGANIZATION",
    indexes = [Index(name = "IDX_OTZ_ORG_DOMAIN", columnList = "domain")]
)
class Organization(

    @Column(name = "domain", nullable = false, length = 100)
    val domain: String,

    @Column(name = "organization_name", nullable = false, length = 100)
    val organizationName: String,

) : BaseEntity()
