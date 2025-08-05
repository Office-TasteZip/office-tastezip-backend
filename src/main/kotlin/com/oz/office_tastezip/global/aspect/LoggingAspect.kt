package com.oz.office_tastezip.global.aspect

import com.oz.office_tastezip.global.util.SecurityUtils.getCurrentUserOrNull
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggingAspect(
    private val request: HttpServletRequest
) {

    private val log = KotlinLogging.logger {}

    @Before("execution(* com.oz.office_tastezip..controller..*(..))")
    fun logRequest(joinPoint: JoinPoint) {
        val methodSignature = joinPoint.signature as MethodSignature
        val className = methodSignature.declaringType.simpleName
        val methodName = methodSignature.name
        val featureName = "$className.$methodName"

        val uri = request.requestURI
        val httpMethod = request.method
        val ip = request.remoteAddr

        val paramNames = methodSignature.parameterNames
        val args = joinPoint.args

        // 불필요한 파라미터 제외
        val paramMap = paramNames.zip(args)
            .filterNot { (_, value) ->
                value == null || listOf(
                    HttpServletRequest::class.java,
                    HttpServletResponse::class.java
                ).any { it.isAssignableFrom(value.javaClass) }
            }
            .toMap()

        val userDetails = getCurrentUserOrNull()
        val userInfo = userDetails?.let { "email: ${it.username}" }

        val logMessage = buildLog(ip, httpMethod, uri, featureName, userInfo, paramMap)
        log.info { logMessage }
    }

    private fun buildLog(
        ip: String,
        httpMethod: String,
        uri: String,
        featureName: String,
        userInfo: String?,
        paramMap: Map<String, Any?>
    ): String {
        return if (userInfo != null) {
            "$ip | $httpMethod $uri | $featureName | $userInfo | params: $paramMap"
        } else {
            "$ip | $httpMethod $uri | $featureName | params: $paramMap"
        }
    }
}
