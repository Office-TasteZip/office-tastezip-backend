package com.oz.office_tastezip.global.exception;

import com.oz.office_tastezip.global.response.ResponseCode;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
    private ResponseCode responseCode;

    public GlobalException() {
    }

    public GlobalException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }

    public GlobalException(ResponseCode responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }

    public GlobalException(ResponseCode responseCode, String message, Throwable throwable) {
        super(message, throwable);
        this.responseCode = responseCode;
    }
}
