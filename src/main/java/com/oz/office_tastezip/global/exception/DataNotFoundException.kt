package com.oz.office_tastezip.global.exception

import com.oz.office_tastezip.global.response.ResponseCode

class DataNotFoundException : GlobalException {
    constructor() : super(ResponseCode.DATA_NOT_FOUND)
    constructor(message: String) : super(ResponseCode.DATA_NOT_FOUND, message)
    constructor(responseCode: ResponseCode) : super(responseCode)
    constructor(responseCode: ResponseCode, message: String) : super(responseCode, message)
}
