package com.oz.office_tastezip.global.exception;

import com.oz.office_tastezip.global.response.ResponseCode;

import static com.oz.office_tastezip.global.response.ResponseCode.VALIDATION_ERROR;

public class ValidationFailureException extends GlobalException {

    public ValidationFailureException() {
        super(VALIDATION_ERROR);
    }

    public ValidationFailureException(String message) {
        super(VALIDATION_ERROR, message);
    }

    public ValidationFailureException(ResponseCode responseCode) {
        super(responseCode);
    }

    public ValidationFailureException(ResponseCode responseCode, String message) {
        super(responseCode, message);
    }
}
