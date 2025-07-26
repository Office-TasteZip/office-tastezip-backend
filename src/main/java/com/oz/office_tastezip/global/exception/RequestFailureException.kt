package com.oz.office_tastezip.global.exception;

import com.oz.office_tastezip.global.response.ResponseCode;

import static com.oz.office_tastezip.global.response.ResponseCode.DUPLICATED_EMAIL;
import static com.oz.office_tastezip.global.response.ResponseCode.FAIL;

public class RequestFailureException extends GlobalException {

    public RequestFailureException() {
        super(FAIL);
    }

    public RequestFailureException(String message) {
        super(FAIL, message);
    }

    public RequestFailureException(ResponseCode responseCode, String message) {
        super(responseCode, message);
    }
}
