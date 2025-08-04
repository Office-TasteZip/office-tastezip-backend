package com.oz.office_tastezip.global.security.jwt

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.io.IOException

private val log = KotlinLogging.logger {}

@Component
class JwtAccessDeniedHandler : AccessDeniedHandler {
    @Throws(IOException::class)
    override fun handle(request: HttpServletRequest?, response: HttpServletResponse?, ex: AccessDeniedException) {
        log.error("Responding with access denied error. Message: ${ex.message}")
    }
}
