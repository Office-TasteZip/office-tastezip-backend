package com.oz.office_tastezip.domain.record

import com.oz.office_tastezip.domain.record.enums.ActionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "TBL_OTZ_REVIEW_HISTORY",
    indexes = [
        Index(name = "IDX_OTZ_REVIEW_HIST_REVIEW", columnList = "review_id"),
        Index(name = "IDX_OTZ_REVIEW_HIST_USER", columnList = "user_id")
    ]
)
class ReviewHistory(

    @Id
    @GeneratedValue
    val id: UUID,

    @Column(name = "review_id")
    val reviewId: UUID,

    @Column(name = "user_id")
    val userId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    val actionType: ActionType,

    @Column(name = "content_snapshot", columnDefinition = "TEXT")
    val contentSnapshot: String? = null,

    @Column(name = "rating_snapshot")
    val ratingSnapshot: Int? = null,

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime,

    @LastModifiedDate
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime? = null

)

