package com.oz.office_tastezip.global.exception

import com.oz.office_tastezip.global.response.ResponseCode
import com.oz.office_tastezip.global.response.ResponseCode.FORBIDDEN

class AccessDeniedException : GlobalException {
    constructor() : super(FORBIDDEN)
    constructor(message: String) : super(FORBIDDEN, message)
    constructor(responseCode: ResponseCode, message: String) : super(responseCode, message)
}
