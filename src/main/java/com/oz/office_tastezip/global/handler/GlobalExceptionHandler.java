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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @ResponseStatus(HttpStatus.OK)
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
     * http status: 200 AND result: FAIL
     * <p>
     * Validation error
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Response.Body<Object>> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String eventId = MDC.get(REQUEST_UUID);
        String errMsg = NestedExceptionUtils.getMostSpecificCause(e).getMessage();

        List<String> defaultMessages = new ArrayList<>();
        Pattern pattern = Pattern.compile("default message \\[(.*?)]");
        Matcher matcher = pattern.matcher(errMsg);
        while (matcher.find()) {
            defaultMessages.add(matcher.group(1));
        }

        List<String> messages = new ArrayList<>();
        for (int i = 1; i < defaultMessages.size(); i += 2) {
            messages.add(defaultMessages.get(i));
        }

        log.error("[MethodArgumentNotValidException] eventId = {}, cause = {}, errMsg = {}",
                eventId,
                NestedExceptionUtils.getMostSpecificCause(e).getStackTrace()[0],
                messages
        );

        return new ResponseFail<>(ResponseCode.FAIL, !messages.isEmpty() ? messages.toString() : "Invalid parameters").fail();
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

}
