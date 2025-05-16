package com.oz.office_tastezip.domain.user.repository

import com.oz.office_tastezip.domain.user.User

interface UserRepositoryCustom {
    fun findByName(name: String): List<User>
}
