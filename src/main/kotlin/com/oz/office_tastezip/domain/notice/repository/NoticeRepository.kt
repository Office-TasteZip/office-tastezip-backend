package com.oz.office_tastezip.domain.notice.repository

import com.oz.office_tastezip.domain.notice.Notice
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface NoticeRepository: JpaRepository<Notice, UUID>, NoticeRepositoryCustom {
}
