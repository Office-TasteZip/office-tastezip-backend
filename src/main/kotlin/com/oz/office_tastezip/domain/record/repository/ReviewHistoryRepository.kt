package com.oz.office_tastezip.domain.record.repository

import com.oz.office_tastezip.domain.record.ReviewHistory
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ReviewHistoryRepository : JpaRepository<ReviewHistory, UUID> {
}
