package com.oz.office_tastezip.domain.user.repository

import com.oz.office_tastezip.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, UUID>, UserRepositoryCustom

