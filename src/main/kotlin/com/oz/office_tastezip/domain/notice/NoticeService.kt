package com.oz.office_tastezip.domain.notice

import com.oz.office_tastezip.domain.notice.repository.NoticeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class NoticeService(
    private val noticeRepository: NoticeRepository
) {
}
