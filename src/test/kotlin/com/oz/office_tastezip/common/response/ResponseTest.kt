package com.oz.office_tastezip.common.response

import com.oz.office_tastezip.global.response.Response
import com.oz.office_tastezip.global.response.ResponseCode
import com.oz.office_tastezip.global.response.ResponseFail
import com.oz.office_tastezip.global.response.ResponseSuccess
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class ResponseTest : StringSpec({

    "성공 응답 - 메시지만 있는 경우" {
        val response = ResponseSuccess<Void>()
        val result: ResponseEntity<Response.Body<Void>> = response.success()

        result.statusCode shouldBe HttpStatus.OK
        result.body.shouldNotBeNull().let {
            it.code shouldBe ResponseCode.SUCCESS.code
            it.message shouldBe ResponseCode.SUCCESS.message
            it.data.shouldBeNull()
        }
    }

    "성공 응답 - 데이터 포함" {
        val data = "This is common response test !"
        val response = ResponseSuccess<String>()
        val result = response.success(data)

        result.statusCode shouldBe HttpStatus.OK
        result.body.shouldNotBeNull().data shouldBe data
    }

    "성공 응답 - 커스텀 메시지 포함" {
        val customMessage = "사용자 생성 완료"
        val result = ResponseSuccess<Void>(customMessage).success()

        result.body.shouldNotBeNull().message shouldBe customMessage
    }

    "실패 응답 - 기본 메시지 사용" {
        val result = ResponseFail<Void>(ResponseCode.INTERNAL_ERROR).fail()

        result.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
        result.body.shouldNotBeNull().let {
            it.code shouldBe ResponseCode.INTERNAL_ERROR.code
            it.message shouldBe ResponseCode.INTERNAL_ERROR.message
        }
    }

    "실패 응답 - 커스텀 에러 메시지" {
        val result = ResponseFail<Void>(ResponseCode.TOKEN_EXPIRED, "토큰 인증 실패").fail()

        result.statusCode shouldBe HttpStatus.UNAUTHORIZED
        result.body.shouldNotBeNull().let {
            it.code shouldBe ResponseCode.TOKEN_EXPIRED.code
            it.message shouldBe "토큰 인증 실패"
        }
    }
})
