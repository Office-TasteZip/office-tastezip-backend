package com.oz.office_tastezip.global.response

import org.springframework.http.HttpStatus
import org.springframework.util.StringUtils

class ResponseFail<T>(
    private val responseCode: ResponseCode = ResponseCode.FAIL,
    private val errorMessage: String? = null
) : Response<T>() {

    override fun resultCode(): String = responseCode.code

    override fun resultMessage(): String =
        if (StringUtils.hasText(errorMessage)) errorMessage!! else responseCode.message

    override fun resultHttpStatus(): HttpStatus = responseCode.httpStatus
}
