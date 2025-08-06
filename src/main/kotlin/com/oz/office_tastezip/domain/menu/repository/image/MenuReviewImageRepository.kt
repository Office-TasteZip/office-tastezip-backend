package com.oz.office_tastezip.domain.menu.repository.image

import com.oz.office_tastezip.domain.menu.MenuReviewImage
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MenuReviewImageRepository : JpaRepository<MenuReviewImage, UUID> {
}
