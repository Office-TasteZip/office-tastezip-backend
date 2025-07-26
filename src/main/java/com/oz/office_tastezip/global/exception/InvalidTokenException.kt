package com.oz.office_tastezip.global.exception

import com.oz.office_tastezip.global.response.ResponseCode
import com.oz.office_tastezip.global.response.ResponseCode.INVALID_TOKEN

class InvalidTokenException : GlobalException {
    constructor() : super(INVALID_TOKEN)
    constructor(message: String) : super(INVALID_TOKEN, message)
    constructor(responseCode: ResponseCode, message: String) : super(responseCode, message)
}
