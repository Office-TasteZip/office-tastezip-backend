package com.oz.office_tastezip.domain.notice

import com.oz.office_tastezip.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "TBL_OTZ_NOTICE")
class Notice(

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    val content: String,

    @Column(name = "is_pinned")
    val isPinned: Boolean,  // 상단 고정 여부

    @Column(name = "view_count", columnDefinition = "int default 0")
    val viewCount: Int = 0

) : BaseEntity()
