package com.oz.office_tastezip.global.handler

import com.oz.office_tastezip.global.exception.GlobalException
import com.oz.office_tastezip.global.interceptor.GlobalRequestInterceptor.Companion.REQUEST_UUID
import com.oz.office_tastezip.global.response.Response
import com.oz.office_tastezip.global.response.ResponseCode
import com.oz.office_tastezip.global.response.ResponseFail
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.MDC
import org.springframework.core.NestedExceptionUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = KotlinLogging.logger {}

    /**
     * 시스템 예외 처리 (500)
     */
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun onException(e: Exception): ResponseEntity<Response.Body<Any>> {
        val mostSpecificCause = NestedExceptionUtils.getMostSpecificCause(e)
        val errMsg = mostSpecificCause.message

        log.error {
            "[Exception] eventId: ${MDC.get(REQUEST_UUID)}, errMsg: $errMsg, cause: ${mostSpecificCause.stackTrace.firstOrNull()}, stackTrace: ${
                ExceptionUtils.getStackTrace(
                    mostSpecificCause
                )
            }"
        }

        return ResponseFail<Any>(ResponseCode.FAIL).fail()
    }

    /**
     * 커스텀 예외 처리 (200 + FAIL)
     */
    @ExceptionHandler(GlobalException::class)
    fun onGlobalException(e: GlobalException, request: HttpServletRequest): ResponseEntity<Response.Body<Any>> {
        val eventId = MDC.get(REQUEST_UUID)
        val cause = NestedExceptionUtils.getMostSpecificCause(e)
        val errMsg = cause.message

        log.error {
            "[BaseException] eventId: $eventId, cause: ${cause.stackTrace.firstOrNull()}, errMsg: $errMsg, request uri: ${request.requestURI}, stackTrace: ${
                ExceptionUtils.getStackTrace(
                    cause
                )
            }"
        }

        return ResponseFail<Any>(e.responseCode, errMsg).fail()
    }

    /**
     * 보안 예외 처리 (403)
     */
    @ExceptionHandler(SecurityException::class)
    fun onSecurityException(e: SecurityException): ResponseEntity<Response.Body<Any>> {
        val cause = NestedExceptionUtils.getMostSpecificCause(e)

        log.error {
            "[Exception] reason: 권한 없는 API 요청 또는 인증되지 않은 사용자, errMsg: ${cause.message}, cause: ${cause.stackTrace.firstOrNull()}, stackTrace: ${
                ExceptionUtils.getStackTrace(
                    cause
                )
            }"
        }

        return ResponseFail<Any>(ResponseCode.FORBIDDEN, e.message).fail()
    }

    /**
     * 유효성 검사 실패 처리 (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun onValidationException(ex: MethodArgumentNotValidException): ResponseEntity<Response.Body<String>> {
        val bindingResult: BindingResult = ex.bindingResult
        val errorMessage = bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "잘못된 요청입니다."
        val cause = NestedExceptionUtils.getMostSpecificCause(ex)

        log.error {
            "[onValidationException] eventId: ${MDC.get(REQUEST_UUID)}, errMsg: $errorMessage, cause: ${cause.stackTrace.firstOrNull()}, stackTrace: ${
                ExceptionUtils.getStackTrace(
                    cause
                )
            }"
        }

        return ResponseFail<String>(ResponseCode.VALIDATION_ERROR, errorMessage).fail()
    }
}
