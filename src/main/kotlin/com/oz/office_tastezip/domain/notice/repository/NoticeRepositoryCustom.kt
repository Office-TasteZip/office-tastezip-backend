package com.oz.office_tastezip.domain.notice.repository

import com.oz.office_tastezip.domain.notice.Notice
import com.oz.office_tastezip.domain.notice.enums.SearchType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface NoticeRepositoryCustom {
    fun searchNotices(searchType: SearchType, searchContent: String, pageable: Pageable): Page<Notice>
}
