package com.oz.office_tastezip.global.handler;

import com.oz.office_tastezip.global.exception.GlobalException;
import com.oz.office_tastezip.global.response.Response;
import com.oz.office_tastezip.global.response.ResponseCode;
import com.oz.office_tastezip.global.response.ResponseFail;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.MDC;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.oz.office_tastezip.global.interceptor.GlobalRequestInterceptor.REQUEST_UUID;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * http status: 500 AND result: FAIL
     * <p>
     * 시스템 예외 상황
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response.Body<Object>> onException(Exception e) {
        String errMsg = NestedExceptionUtils.getMostSpecificCause(e).getMessage();
        log.error("[Exception] eventId : {}, errMsg : {}, cause : {} stackTrace : {}",
                MDC.get(REQUEST_UUID),
                errMsg,
                NestedExceptionUtils.getMostSpecificCause(e).getStackTrace()[0],
                ExceptionUtils.getStackTrace(NestedExceptionUtils.getMostSpecificCause(e)));

        return new ResponseFail<>(ResponseCode.FAIL).fail();
    }

    /**
     * http status: 200 AND result: FAIL
     * <p>
     * 시스템은 이슈 없고, 비즈니스 로직 처리에서 에러 발생
     */
    @ResponseBody
    @ExceptionHandler(value = GlobalException.class)
    public ResponseEntity<Response.Body<Object>> onGlobalException(GlobalException e, HttpServletRequest request) {
        String eventId = MDC.get(REQUEST_UUID);
        String errMsg = NestedExceptionUtils.getMostSpecificCause(e).getMessage();
        log.error("[BaseException] eventId : {}, cause : {}, errMsg : {}, request uri : {}, stackTrace : {}",
                eventId,
                NestedExceptionUtils.getMostSpecificCause(e).getStackTrace()[0],
                errMsg,
                request.getRequestURI(),
                ExceptionUtils.getStackTrace(NestedExceptionUtils.getMostSpecificCause(e)));
        return new ResponseFail<>(e.getResponseCode(), errMsg).fail();
    }

    /**
     * http status: 403
     * <p>
     * 권한 없는 API 요청 시 에러
     */
    @ResponseBody
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Response.Body<Object>> onSecurityException(SecurityException e) {
        Throwable mostSpecificCause = NestedExceptionUtils.getMostSpecificCause(e);
        log.error("[Exception] reason: {}, errMsg: {}, cause: {}, stackTrace: {}",
                "권한 없는 API 요청 또는 인증되지 않은 사용자",
                mostSpecificCause.getMessage(),
                mostSpecificCause.getStackTrace()[0],
                ExceptionUtils.getStackTrace(mostSpecificCause));

        return new ResponseFail<>(ResponseCode.FORBIDDEN, e.getMessage()).fail();
    }

    /**
     * http status: 400
     * <p>
     * Validation Error
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response.Body<String>> onValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();

        log.error("[onValidationException] eventId : {}, errMsg : {}, cause : {} stackTrace : {}",
                MDC.get(REQUEST_UUID),
                errorMessage,
                NestedExceptionUtils.getMostSpecificCause(ex).getStackTrace()[0],
                ExceptionUtils.getStackTrace(NestedExceptionUtils.getMostSpecificCause(ex)));

        return new ResponseFail<String>(ResponseCode.VALIDATION_ERROR, errorMessage).fail();
    }
}
