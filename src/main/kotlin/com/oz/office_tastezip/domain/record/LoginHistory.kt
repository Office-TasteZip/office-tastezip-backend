package com.oz.office_tastezip.domain.record

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "TBL_OTZ_USER_LOGIN_HISTORY",
    indexes = [Index(name = "IDX_OTZ_LOGIN_USER", columnList = "user_id")]
)
class LoginHistory(

    @Id
    @GeneratedValue
    val id: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "login_at", nullable = false)
    val loginAt: LocalDateTime,

    @Column(name = "ip_address", nullable = false)
    val ipAddress: String,          // 로그인 요청의 클라이언트 IP (IPv6 대응 포함)

    @Column(name = "user_agent", nullable = false, columnDefinition = "TEXT")
    val userAgent: String,          // 브라우저/OS 정보 등 User-Agent 헤더

    @Column(name = "login_result", nullable = false)
    val loginResult: String,

    @Column(name = "fail_reason")
    val failReason: String? = null

)
