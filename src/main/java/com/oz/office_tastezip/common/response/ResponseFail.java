package com.oz.office_tastezip.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

public class ResponseFail<T> extends Response<T> {

    private ResponseCode responseCode;
    private String errorMessage;

    public ResponseFail() {
    }

    public ResponseFail(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public ResponseFail(ResponseCode responseCode, String errorMessage) {
        this.responseCode = responseCode;
        this.errorMessage = errorMessage;
    }

    @Override
    protected String resultCode() {
        return responseCode.getCode();
    }

    @Override
    protected String resultMessage() {
        return StringUtils.hasText(errorMessage) ? errorMessage : responseCode.getMessage();
    }

    @Override
    protected HttpStatus resultHttpStatus() {
        return responseCode.getHttpStatus();
    }

}
