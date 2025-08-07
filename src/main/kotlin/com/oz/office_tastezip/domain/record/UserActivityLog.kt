package com.oz.office_tastezip.domain.record

import com.oz.office_tastezip.domain.record.enums.ActionType
import com.oz.office_tastezip.domain.record.enums.TargetType
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "TBL_OTZ_ACTIVITY_LOG",
    indexes = [
        Index(name = "IDX_OTZ_ACT_USER", columnList = "user_id"),
        Index(name = "IDX_OTZ_ACT_ACTION", columnList = "action_type")
    ]
)
class UserActivityLog(

    @Id
    @GeneratedValue
    val id: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "action_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val actionType: ActionType,

    @Column(name = "target_type")
    @Enumerated(EnumType.STRING)
    val targetType: TargetType,

    @Column(name = "metadata", columnDefinition = "TEXT")
    val metadata: String? = null,

    @CreatedDate
    @Column(name = "occurred_at", updatable = false)
    val occurredAt: LocalDateTime

)
