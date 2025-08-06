package com.oz.office_tastezip.domain.menu

import com.oz.office_tastezip.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "TBL_OTZ_MENU_REVIEW_IMAGE")
class MenuReviewImage(

    @Column(name = "image_url", nullable = false)
    val imageUrl: String,

    @Column(name = "sequence", columnDefinition = "int default 1")
    val sequence: Int = 1,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    var menuReview: MenuReview

) : BaseEntity()
