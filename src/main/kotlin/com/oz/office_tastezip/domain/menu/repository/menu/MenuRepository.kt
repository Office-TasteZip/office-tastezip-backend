package com.oz.office_tastezip.domain.menu.repository.menu

import com.oz.office_tastezip.domain.menu.Menu
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MenuRepository : JpaRepository<Menu, UUID> {
}
