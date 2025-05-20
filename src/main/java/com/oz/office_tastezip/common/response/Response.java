package com.oz.office_tastezip.common.response;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class Response<T> {

    public record Body<T>(String code, String message, T data) {}

    protected abstract String resultCode();
    protected abstract String resultMessage();
    protected abstract HttpStatus resultHttpStatus();

    private Body<T> getBody(T data) {
        return new Body<>(resultCode(), resultMessage(), data);
    }

    /**
     * <p> 메세지만 가진 성공 응답을 반환</p>
     * <pre>
     *     {
     *         "code" : 0000,
     *         "message" : Success,
     *         "data" : []
     *     }
     * </pre>
     */
    public ResponseEntity<Body<T>> success() {
        return new ResponseEntity<>(getBody(null), HttpStatus.OK);
    }

    /**
     * <p> 메세지와 데이터를 포함한 성공 응답을 반환</p>
     * <pre>
     *     {
     *         "code" : 0000,
     *         "message" : Success,
     *         "data" : [{data1}, {data2}...]
     *     }
     * </pre>
     */
    public ResponseEntity<Body<T>> success(T data) {
        return new ResponseEntity<>(getBody(data), HttpStatus.OK);
    }

    /**
     * <p> 메세지와 데이터, Header를 포함한 성공 응답을 반환</p>
     */
    public ResponseEntity<Body<T>> success(HttpHeaders headers, T data) {
        return ResponseEntity.ok().headers(headers).body(getBody(data));
    }

    /**
     * <p> 메세지만 가진 실패 응답을 반환한다.</p>
     * <pre>
     *     {
     *         "code" : 0001,
     *         "message" : Request failed to process.,
     *         "data" : []
     *     }
     * </pre>
     */
    public ResponseEntity<Body<T>> fail() {
        return new ResponseEntity<>(getBody(null), resultHttpStatus());
    }
}
