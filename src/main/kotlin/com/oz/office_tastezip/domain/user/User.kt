package com.oz.office_tastezip.domain.user

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "TBL_OTZ_USER")
class User(

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "UUID")
    val id: UUID? = null,

    @Column(name = "email", nullable = false, unique = true)
    val email: String,

    @Column(name = "name", nullable = false)
    val name: String
)
