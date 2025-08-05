package com.oz.office_tastezip.global.aspect

import com.oz.office_tastezip.domain.auth.enums.UserRole
import com.oz.office_tastezip.global.exception.AccessDeniedException
import com.oz.office_tastezip.global.util.SecurityUtils.getAuthenticatedUserDetail
import mu.KotlinLogging
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component

@Aspect
@Component
class AdminOnlyAspect() {

    private val log = KotlinLogging.logger {}

    @Before("@annotation(com.oz.office_tastezip.global.aspect.AdminOnly)")
    fun checkAdminAccess(joinPoint: JoinPoint) {
        val userDetails = getAuthenticatedUserDetail()

        if (userDetails.role != UserRole.ROLE_ADMIN.name) {
            val methodName = joinPoint.signature.name
            val className = joinPoint.signature.declaringTypeName
            log.warn("관리자 권한 없이 [$className.$methodName] 접근 시도")

            throw AccessDeniedException("접근 권한이 없습니다.")
        }
    }

}
