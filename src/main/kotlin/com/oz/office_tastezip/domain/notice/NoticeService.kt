package com.oz.office_tastezip.domain.notice

import com.oz.office_tastezip.domain.notice.dto.NoticeUpdateDto
import com.oz.office_tastezip.domain.notice.enums.SearchType
import com.oz.office_tastezip.domain.notice.repository.NoticeRepository
import mu.KotlinLogging
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
    private val log = KotlinLogging.logger {}

    fun searchNotices(searchType: SearchType, searchContent: String, pageable: Pageable): Page<Notice> {
        return noticeRepository.searchNotices(searchType, searchContent, pageable)
    }

    fun searchNotice(id: UUID): Notice {
        val result = noticeRepository.searchNoticeById(id)
        noticeRepository.updateViewCount(id, result.viewCount)
        return result
    }

    fun insertNotice(notice: Notice) {
        val result = noticeRepository.save(notice)
        log.info { "공지 등록 완료 - id: ${result.id}, title: ${result.title}" }
    }

    fun updateNotice(id: UUID, author: String, noticeUpdateDto: NoticeUpdateDto) {
        noticeRepository.updateNotice(id, author, noticeUpdateDto)
    }

    fun deleteNotice(id: UUID) {
        noticeRepository.deleteById(id)
    }

    fun updatePinnedStatus(id: UUID, isPinned: Boolean) {
        noticeRepository.updatePinnedStatus(id, isPinned)
    }
}
