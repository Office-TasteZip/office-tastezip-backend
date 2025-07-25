package com.oz.office_tastezip.domain.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.oz.office_tastezip.domain.auth.dto.LoginDto
import com.oz.office_tastezip.global.response.ResponseCode.*
import com.oz.office_tastezip.global.util.RSAUtils
import com.oz.office_tastezip.support.AuthTestHelper
import com.oz.office_tastezip.support.AuthToken
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest @Autowired constructor(
    private val authTestHelper: AuthTestHelper,
    private val restTemplate: TestRestTemplate,
    private val objectMapper: ObjectMapper,
    @LocalServerPort private val port: Int,
) : FunSpec({

    lateinit var loginDto: LoginDto
    lateinit var tokens: AuthToken
    lateinit var requestBaseUri: String
    lateinit var rsaPublicKey: String

    beforeTest {
        requestBaseUri = "http://localhost:$port/api/v1/otz/auth"
        val response = restTemplate.getForEntity("$requestBaseUri/rsa", String::class.java)
        rsaPublicKey = objectMapper.readTree(response.body)["data"].asText()
        authTestHelper.setHostAndPort("http://localhost:", port, rsaPublicKey, restTemplate)
        tokens = authTestHelper.loginAndGetTokens("tester@example.com", "password123!")
    }

    context("로그인") {
        test("로그인 요청 성공") {
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }
            loginDto = LoginDto("tester@example.com", RSAUtils.encrypt("password123!", rsaPublicKey))
            val request = HttpEntity(loginDto, headers)

            val response = restTemplate.postForEntity("$requestBaseUri/login", request, String::class.java)
            response.statusCode shouldBe SUCCESS.httpStatus
            objectMapper.readTree(response.body)["data"]["accessToken"].asText().isNotBlank() shouldBe true
        }

        test("로그인 실패 - 존재하지 않는 사용자") {
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }
            loginDto = LoginDto("tester@test.com", RSAUtils.encrypt("password123!", rsaPublicKey))
            val request = HttpEntity(loginDto, headers)

            val response = restTemplate.postForEntity("$requestBaseUri/login", request, String::class.java)
            response.statusCode shouldBe USER_NOT_FOUND.httpStatus
            response.body?.contains(USER_NOT_FOUND.code) shouldBe true
        }

        test("로그인 실패 - 비밀번호 오류") {
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }
            loginDto = LoginDto("tester@example.com", RSAUtils.encrypt("wrongpassword", rsaPublicKey))
            val request = HttpEntity(loginDto, headers)

            val response = restTemplate.postForEntity("$requestBaseUri/login", request, String::class.java)
            response.statusCode shouldBe INVALID_PASSWORD.httpStatus
            response.body?.contains(INVALID_PASSWORD.code) shouldBe true
        }
    }

    context("리프레시 토큰") {
        test("토큰 재발급 성공") {
            val headers = HttpHeaders().apply {
                add(HttpHeaders.COOKIE, "refreshToken=${tokens.refreshToken}")
            }
            val request = HttpEntity<Void>(headers)

            val response = restTemplate.postForEntity("$requestBaseUri/reissue", request, String::class.java)
            response.statusCode shouldBe SUCCESS.httpStatus
            response.body?.contains("accessToken") shouldBe true
        }

        test("토큰 재발급 실패 - 토큰 없음") {
            val request = HttpEntity<Void>(HttpHeaders())
            val response = restTemplate.postForEntity("$requestBaseUri/reissue", request, String::class.java)
            response.statusCode shouldBe TOKEN_DOES_NOT_EXIST.httpStatus
        }
    }

    context("로그아웃") {
        test("로그아웃 성공") {
            val headers = HttpHeaders().apply {
                add(HttpHeaders.COOKIE, "refreshToken=${tokens.refreshToken}")
            }
            val request = HttpEntity<Void>(headers)

            val response = restTemplate.postForEntity("$requestBaseUri/logout", request, String::class.java)
            response.statusCode shouldBe SUCCESS.httpStatus
        }

        test("로그아웃 실패 - 토큰 없음") {
            val request = HttpEntity<Void>(HttpHeaders())
            val response = restTemplate.postForEntity("$requestBaseUri/logout", request, String::class.java)
            response.statusCode shouldBe TOKEN_DOES_NOT_EXIST.httpStatus
        }
    }

}) {
    override fun extensions() = listOf(SpringExtension)
}
