package com.oz.office_tastezip.global.exception

import com.oz.office_tastezip.global.response.ResponseCode

open class GlobalException : RuntimeException {
    val responseCode: ResponseCode

    constructor(responseCode: ResponseCode) : super(responseCode.message) {
        this.responseCode = responseCode
    }

    constructor(responseCode: ResponseCode, message: String) : super(message) {
        this.responseCode = responseCode
    }

    constructor(responseCode: ResponseCode, message: String, throwable: Throwable) : super(message, throwable) {
        this.responseCode = responseCode
    }
}
