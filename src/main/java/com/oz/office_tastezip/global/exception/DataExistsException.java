package com.oz.office_tastezip.global.exception;

import com.oz.office_tastezip.global.response.ResponseCode;

import static com.oz.office_tastezip.global.response.ResponseCode.DUPLICATED_EMAIL;

public class DataExistsException extends GlobalException {

    public DataExistsException() {
        super(DUPLICATED_EMAIL);
    }

    public DataExistsException(String message) {
        super(DUPLICATED_EMAIL, message);
    }

    public DataExistsException(ResponseCode responseCode, String message) {
        super(responseCode, message);
    }
}
