package com.oz.office_tastezip.domain.menu.repository.review

import com.oz.office_tastezip.domain.menu.MenuReview
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MenuReviewRepository : JpaRepository<MenuReview, UUID> {
}
