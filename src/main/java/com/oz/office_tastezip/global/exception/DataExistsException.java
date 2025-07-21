package com.oz.office_tastezip.global.exception;

import com.oz.office_tastezip.global.response.ResponseCode;

import static com.oz.office_tastezip.global.response.ResponseCode.DATA_EXIST;

public class DataExistsException extends GlobalException {

    public DataExistsException() {
        super(DATA_EXIST);
    }

    public DataExistsException(String message) {
        super(DATA_EXIST, message);
    }

    public DataExistsException(ResponseCode responseCode) {
        super(responseCode);
    }

    public DataExistsException(ResponseCode responseCode, String message) {
        super(responseCode, message);
    }
}
