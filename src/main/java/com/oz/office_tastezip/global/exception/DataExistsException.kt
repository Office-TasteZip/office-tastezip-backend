package com.oz.office_tastezip.global.exception

import com.oz.office_tastezip.global.response.ResponseCode

class DataExistsException : GlobalException {
    constructor() : super(ResponseCode.DATA_EXIST)
    constructor(message: String) : super(ResponseCode.DATA_EXIST, message)
    constructor(responseCode: ResponseCode) : super(responseCode)
    constructor(responseCode: ResponseCode, message: String) : super(responseCode, message)
}
