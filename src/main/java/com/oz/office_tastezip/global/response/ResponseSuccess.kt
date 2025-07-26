package com.oz.office_tastezip.global.response;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

public class ResponseSuccess<T> extends Response<T> {

    private String resultMessage;

    public ResponseSuccess() {
    }

    public ResponseSuccess(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    @Override
    protected String resultCode() {
        return ResponseCode.SUCCESS.getCode();
    }

    @Override
    protected String resultMessage() {
        return StringUtils.hasText(resultMessage) ? resultMessage : ResponseCode.SUCCESS.getMessage();
    }

    @Override
    protected HttpStatus resultHttpStatus() {
        return HttpStatus.OK;
    }

}
