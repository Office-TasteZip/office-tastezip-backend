package com.oz.office_tastezip.domain.restaurant

import com.oz.office_tastezip.domain.BaseEntity
import com.oz.office_tastezip.domain.organization.Organization
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "TBL_OTZ_RESTAURANT")
class Restaurant(

    @Column(name = "name", nullable = false, length = 100)
    val name: String,

    @Column(name = "address", nullable = false)
    val address: String,

    @Column(name = "latitude")
    val latitude: Double,

    @Column(name = "longitude")
    val longitude: Double,

    @Column(name = "createdBy", nullable = false)
    val createdBy: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    var organization: Organization

) : BaseEntity()
