package com.oz.office_tastezip.global.exception;

import com.oz.office_tastezip.global.response.ResponseCode;

import static com.oz.office_tastezip.global.response.ResponseCode.USER_NOT_FOUND;

public class UserNotFoundException extends GlobalException {

    public UserNotFoundException() {
        super(USER_NOT_FOUND);
    }

    public UserNotFoundException(String message) {
        super(USER_NOT_FOUND, message);
    }

    public UserNotFoundException(ResponseCode responseCode, String message) {
        super(responseCode, message);
    }
}
