package com.oz.office_tastezip.global.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*

@Component
class GlobalRequestInterceptor : HandlerInterceptor {
    companion object {
        const val REQUEST_UUID = "x-request-id"
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val requestId = request.getHeader(REQUEST_UUID)?.takeIf { it.isNotBlank() }
            ?: UUID.randomUUID().toString()
        MDC.put(REQUEST_UUID, requestId)
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        MDC.remove(REQUEST_UUID)
    }
}
