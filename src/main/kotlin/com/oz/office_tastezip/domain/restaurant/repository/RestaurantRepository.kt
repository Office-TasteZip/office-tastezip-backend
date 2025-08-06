package com.oz.office_tastezip.domain.restaurant.repository

import com.oz.office_tastezip.domain.restaurant.Restaurant
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RestaurantRepository : JpaRepository<Restaurant, UUID> {
}
