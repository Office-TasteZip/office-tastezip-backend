package com.oz.office_tastezip.global.exception;

import com.oz.office_tastezip.global.response.ResponseCode;

import static com.oz.office_tastezip.global.response.ResponseCode.DATA_NOT_FOUND;
import static com.oz.office_tastezip.global.response.ResponseCode.USER_NOT_FOUND;

public class DataNotFoundException extends GlobalException {

    public DataNotFoundException() {
        super(DATA_NOT_FOUND);
    }

    public DataNotFoundException(String message) {
        super(DATA_NOT_FOUND, message);
    }

    public DataNotFoundException(ResponseCode responseCode, String message) {
        super(responseCode, message);
    }
}
