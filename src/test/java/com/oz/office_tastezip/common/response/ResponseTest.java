package com.oz.office_tastezip.common.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseTest {

    @Test
    @DisplayName("성공 응답 - 메시지만 있는 경우")
    void successWithoutData() {
        ResponseSuccess<Void> response = new ResponseSuccess<>();
        ResponseEntity<Response.Body<Void>> result = response.success();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().code()).isEqualTo(ResponseCode.SUCCESS.getCode());
        assertThat(result.getBody().message()).isEqualTo(ResponseCode.SUCCESS.getMessage());
        assertThat(result.getBody().data()).isNull();
    }

    @Test
    @DisplayName("성공 응답 - 데이터 포함")
    void successWithData() {
        String data = "Hello, world!";
        ResponseSuccess<String> response = new ResponseSuccess<>();
        ResponseEntity<Response.Body<String>> result = response.success(data);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().data()).isEqualTo(data);
    }

    @Test
    @DisplayName("성공 응답 - 커스텀 메시지 포함")
    void successWithCustomMessage() {
        String customMessage = "사용자 생성 완료";
        ResponseEntity<Response.Body<Void>> result = new ResponseSuccess<Void>(customMessage).success();

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().message()).isEqualTo(customMessage);
    }

    @Test
    @DisplayName("실패 응답 - 기본 메시지 사용")
    void failWithDefaultMessage() {
        ResponseEntity<Response.Body<Void>> result = new ResponseFail<Void>(ResponseCode.INTERNAL_ERROR).fail();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().code()).isEqualTo(ResponseCode.INTERNAL_ERROR.getCode());
        assertThat(result.getBody().message()).isEqualTo(ResponseCode.INTERNAL_ERROR.getMessage());
    }

    @Test
    @DisplayName("실패 응답 - 커스텀 에러 메시지")
    void failWithCustomMessage() {
        ResponseEntity<Response.Body<Void>> result = new ResponseFail<Void>(ResponseCode.TOKEN_EXPIRED, "토큰 인증 실패").fail();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().code()).isEqualTo(ResponseCode.TOKEN_EXPIRED.getCode());
        assertThat(result.getBody().message()).isEqualTo("토큰 인증 실패");
    }
}
