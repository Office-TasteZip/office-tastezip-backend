package com.oz.office_tastezip.global.interceptor

import com.oz.office_tastezip.global.util.SecurityUtils.getCurrentUserOrNull
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*

@Component
class GlobalRequestInterceptor : HandlerInterceptor {

    private val log = KotlinLogging.logger {}

    companion object {
        const val REQUEST_UUID = "x-request-id"
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val requestId = request.getHeader(REQUEST_UUID)?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
        MDC.put(REQUEST_UUID, requestId)

        val uri = request.requestURI

        if (uri.contains("/api")) {
            // 기능명 (컨트롤러.메서드명)
            val featureName = if (handler is HandlerMethod) {
                val className = handler.beanType.simpleName
                val methodName = handler.method.name
                "$className.$methodName"
            } else "UnknownHandler"

            val userDetails = getCurrentUserOrNull()
            val userInfo = userDetails?.let { "uuid: ${it.uuid}, email: ${it.email}" } ?: "anonymous"

            log.info("${request.remoteAddr} | ${request.method} $uri | $featureName | $userInfo")
        }

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
