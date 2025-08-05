package com.oz.office_tastezip.domain.notice

import com.oz.office_tastezip.domain.notice.enums.SearchType
import com.oz.office_tastezip.domain.notice.repository.NoticeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class NoticeService(
    private val noticeRepository: NoticeRepository
) {

    fun searchNotices(searchType: SearchType, searchContent: String, pageable: Pageable): Page<Notice> {
        return noticeRepository.searchNotices(searchType, searchContent, pageable)
    }

    fun searchNotice(id: UUID): Notice {
        val result = noticeRepository.searchNoticeById(id)
        noticeRepository.updateViewCount(id, result.viewCount)
        return result
    }

}
