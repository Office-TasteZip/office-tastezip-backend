package com.oz.office_tastezip.domain.menu

import com.oz.office_tastezip.domain.BaseEntity
import com.oz.office_tastezip.domain.organization.Organization
import com.oz.office_tastezip.domain.restaurant.Restaurant
import com.oz.office_tastezip.domain.user.User
import jakarta.persistence.*

@Entity
@Table(
    name = "TBL_OTZ_MENU_REVIEW", indexes = [
        Index(name = "IDX_OTZ_REVIEW_MENU", columnList = "menu_id"),
        Index(name = "IDX_OTZ_REVIEW_REST", columnList = "restaurant_id"),
        Index(name = "IDX_OTZ_REVIEW_USER", columnList = "user_id"),
        Index(name = "IDX_OTZ_REVIEW_ORG", columnList = "organization_id"),
    ]
)
class MenuReview(

    @Column(name = "content")
    val content: String,

    @Column(name = "rating", nullable = false)
    val rating: Int,

    @Column(name = "is_private")
    val isPrivate: Boolean,

    @Column(name = "has_image")
    val hasImage: Boolean,

    @Column(name = "like_count", columnDefinition = "int default 0")
    val likeCount: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    var organization: Organization,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    var restaurant: Restaurant,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    var menu: Menu

) : BaseEntity()
