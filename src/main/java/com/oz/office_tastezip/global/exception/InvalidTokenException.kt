package com.oz.office_tastezip.global.exception;

import com.oz.office_tastezip.global.response.ResponseCode;

import static com.oz.office_tastezip.global.response.ResponseCode.INVALID_TOKEN;

public class InvalidTokenException extends GlobalException {

    public InvalidTokenException() {
        super(INVALID_TOKEN);
    }

    public InvalidTokenException(String message) {
        super(INVALID_TOKEN, message);
    }

    public InvalidTokenException(ResponseCode responseCode, String message) {
        super(responseCode, message);
    }
}
