package com.oz.office_tastezip.domain.notice

import com.oz.office_tastezip.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "TBL_OTZ_NOTICE")
class Notice(

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    val content: String,

    @Column(name = "target_org_id")
    val targetOrgId: UUID? = null,  // NULL이면 전체 대상, 특정 조직 전용 가능

    @Column(name = "is_pinned")
    val isPinned: Boolean,

    @Column(name = "view_count", columnDefinition = "int default 0")
    val viewCount: Int = 0

) : BaseEntity()
