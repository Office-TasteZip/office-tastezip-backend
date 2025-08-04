package com.oz.office_tastezip.global.response

import org.springframework.http.HttpStatus
import org.springframework.util.StringUtils

class ResponseSuccess<T>(
    private val resultMessage: String? = null
) : Response<T>() {

    override fun resultCode(): String {
        return ResponseCode.SUCCESS.code
    }

    override fun resultMessage(): String {
        return if (StringUtils.hasText(resultMessage)) resultMessage!! else ResponseCode.SUCCESS.message
    }

    override fun resultHttpStatus(): HttpStatus {
        return HttpStatus.OK
    }
}
