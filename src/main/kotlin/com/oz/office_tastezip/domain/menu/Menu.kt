package com.oz.office_tastezip.domain.menu

import com.oz.office_tastezip.domain.BaseEntity
import com.oz.office_tastezip.domain.restaurant.Restaurant
import jakarta.persistence.*

@Entity
@Table(name = "TBL_OTZ_MENU")
class Menu(

    @Column(name = "menu", nullable = false, length = 100)
    val name: String,

    @Column(name = "price")
    val price: Int,

    @Column(name = "image_url")
    val imageUrl: String? = null,

    @Column(name = "description")
    val description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    var restaurant: Restaurant,
) : BaseEntity()
