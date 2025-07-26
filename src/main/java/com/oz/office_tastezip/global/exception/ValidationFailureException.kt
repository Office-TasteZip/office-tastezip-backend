package com.oz.office_tastezip.global.exception

import com.oz.office_tastezip.global.response.ResponseCode
import com.oz.office_tastezip.global.response.ResponseCode.VALIDATION_ERROR

class ValidationFailureException : GlobalException {
    constructor() : super(VALIDATION_ERROR)
    constructor(message: String) : super(VALIDATION_ERROR, message)
    constructor(responseCode: ResponseCode) : super(responseCode)
    constructor(responseCode: ResponseCode, message: String) : super(responseCode, message)
}
