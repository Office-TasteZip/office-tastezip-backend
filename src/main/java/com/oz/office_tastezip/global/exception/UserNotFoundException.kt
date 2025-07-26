package com.oz.office_tastezip.global.exception

import com.oz.office_tastezip.global.response.ResponseCode
import com.oz.office_tastezip.global.response.ResponseCode.USER_NOT_FOUND

class UserNotFoundException : GlobalException {
    constructor() : super(USER_NOT_FOUND)
    constructor(message: String) : super(USER_NOT_FOUND, message)
    constructor(responseCode: ResponseCode, message: String) : super(responseCode, message)
}
