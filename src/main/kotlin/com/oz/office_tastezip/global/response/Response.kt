package com.oz.office_tastezip.global.response

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

abstract class Response<T> {

    data class Body<T>(
        val code: String,
        val message: String,
        val data: T?
    )

    protected abstract fun resultCode(): String
    protected abstract fun resultMessage(): String
    protected abstract fun resultHttpStatus(): HttpStatus

    private fun getBody(data: T?): Body<T> =
        Body(resultCode(), resultMessage(), data)

    /**
     * 메시지만 가진 성공 응답을 반환
     */
    fun success(): ResponseEntity<Body<T>> =
        ResponseEntity(getBody(null), HttpStatus.OK)

    /**
     * 메시지와 데이터를 포함한 성공 응답 반환
     */
    fun success(data: T): ResponseEntity<Body<T>> =
        ResponseEntity(getBody(data), HttpStatus.OK)

    /**
     * 메시지와 데이터, Header를 포함한 성공 응답 반환
     */
    fun success(headers: HttpHeaders, data: T): ResponseEntity<Body<T>> =
        ResponseEntity.ok().headers(headers).body(getBody(data))

    /**
     * 메시지만 가진 실패 응답 반환
     */
    fun fail(): ResponseEntity<Body<T>> =
        ResponseEntity(getBody(null), resultHttpStatus())
}
