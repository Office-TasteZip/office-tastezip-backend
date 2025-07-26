package com.oz.office_tastezip.global.exception

import com.oz.office_tastezip.global.response.ResponseCode
import com.oz.office_tastezip.global.response.ResponseCode.FAIL

class RequestFailureException : GlobalException {
    constructor() : super(FAIL)
    constructor(message: String) : super(FAIL, message)
    constructor(responseCode: ResponseCode, message: String) : super(responseCode, message)
}
