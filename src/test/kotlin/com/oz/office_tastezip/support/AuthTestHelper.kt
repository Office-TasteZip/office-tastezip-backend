package com.oz.office_tastezip.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.oz.office_tastezip.domain.auth.dto.LoginDto
import com.oz.office_tastezip.global.util.RSAUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component

@Component
class AuthTestHelper @Autowired constructor(
    private val objectMapper: ObjectMapper
) {

    private lateinit var restTemplate: TestRestTemplate
    private lateinit var host: String
    private var port: Int = 0
    private lateinit var publicKey: String

    fun setHostAndPort(host: String, port: Int, publicKey: String, restTemplate: TestRestTemplate) {
        this.host = host
        this.port = port
        this.publicKey = publicKey
        this.restTemplate = restTemplate
    }

    fun loginAndGetTokens(email: String, password: String): AuthToken {
        val loginDto = LoginDto(email, RSAUtils.encrypt(password, publicKey))

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val request = HttpEntity(loginDto, headers)
        val loginRes = restTemplate.postForEntity(
            "$host$port/api/v1/otz/auth/login",
            request,
            String::class.java
        )

        val responseJson = objectMapper.readTree(loginRes.body)
        val accessToken = responseJson["data"]["accessToken"].asText()
        val refreshToken = extractRefreshTokenFromSetCookie(
            loginRes.headers["Set-Cookie"]
                ?: throw IllegalStateException("Set-Cookie 헤더 없음")
        )

        return AuthToken(accessToken, refreshToken)
    }

    private fun extractRefreshTokenFromSetCookie(setCookies: List<String>): String {
        return setCookies.firstOrNull { it.startsWith("refreshToken=") }
            ?.substringAfter("refreshToken=")
            ?.substringBefore(";")
            ?: throw IllegalStateException("refreshToken 쿠키 없음")
    }
}
