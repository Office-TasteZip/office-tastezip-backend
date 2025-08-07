package com.oz.office_tastezip.domain.user

import com.oz.office_tastezip.domain.BaseEntity
import com.oz.office_tastezip.domain.auth.enums.UserRole
import com.oz.office_tastezip.domain.auth.enums.UserStatus
import com.oz.office_tastezip.domain.organization.Organization
import com.oz.office_tastezip.domain.user.dto.UserRequestDto.UserInsertRequest
import com.oz.office_tastezip.domain.user.enums.UserJob
import com.oz.office_tastezip.domain.user.enums.UserPosition
import jakarta.persistence.*
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime

@Entity
@Table(
    name = "TBL_OTZ_USER",
    indexes = [
        Index(name = "IDX_OTZ_USER_ORG", columnList = "organization_id"),
        Index(name = "IDX_OTZ_USER_DELETED_AT", columnList = "deleted_at")
    ]
)
class User(

    @Column(name = "email", nullable = false, unique = true, length = 100)
    val email: String,

    @Column(name = "password_hash", nullable = false)
    val passwordHash: String,

    @Column(name = "password_updated_at", nullable = false)
    val passwordUpdatedAt: LocalDateTime,

    @Column(name = "nickname", nullable = false, length = 100)
    val nickname: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "job", nullable = false, length = 50)
    val job: UserJob,

    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false, length = 50)
    val position: UserPosition,

    @Column(name = "join_year", nullable = false, length = 4)
    val joinYear: String,

    @Column(name = "terms_agree")
    val termsAgree: Boolean,    // 서비스 이용약관 동의 여부(필수)

    @Column(name = "privacy_agree")
    val privacyAgree: Boolean,  // 개인정보 수집 및 이용 동의 여부(필수)

    @Column(name = "marketing_agree")
    val marketingAgree: Boolean,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    val role: UserRole,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: UserStatus,

    @Column(name = "last_login_ip")
    val lastLoginIp: String? = null,

    @Column(name = "last_login_at")
    val lastLoginAt: LocalDateTime? = null,

    @Column(name = "last_failed_login_at")
    val lastFailedLoginAt: LocalDateTime? = null,

    @Column(name = "login_fail_count", columnDefinition = "int default 0")
    val loginFailCount: Int = 0,

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    val profileImageUrl: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    var organization: Organization

) : BaseEntity() {

    companion object {
        fun create(request: UserInsertRequest, passwordEncoder: PasswordEncoder, organization: Organization): User {
            return User(
                email = request.email,
                passwordHash = passwordEncoder.encode(request.password),
                passwordUpdatedAt = LocalDateTime.now(),
                nickname = request.nickname,
                job = UserJob.fromJobName(request.job),
                position = UserPosition.fromPositionName(request.position),
                joinYear = request.joinYear,
                termsAgree = request.termsAgree,
                privacyAgree = request.privacyAgree,
                marketingAgree = request.marketingAgree,
                role = UserRole.ROLE_USER,
                status = UserStatus.ACTIVE,
                organization = organization
            )
        }
    }
}
