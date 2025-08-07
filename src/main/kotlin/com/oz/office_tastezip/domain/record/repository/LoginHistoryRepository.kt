package com.oz.office_tastezip.domain.record.repository

import com.oz.office_tastezip.domain.record.LoginHistory
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface LoginHistoryRepository : JpaRepository<LoginHistory, UUID> {
}
