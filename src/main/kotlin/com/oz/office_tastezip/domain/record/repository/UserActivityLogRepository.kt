package com.oz.office_tastezip.domain.record.repository

import com.oz.office_tastezip.domain.record.UserActivityLog
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserActivityLogRepository : JpaRepository<UserActivityLog, UUID> {
}
