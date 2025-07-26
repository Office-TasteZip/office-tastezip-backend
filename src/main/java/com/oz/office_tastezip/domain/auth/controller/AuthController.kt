package com.oz.office_tastezip.domain.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.oz.office_tastezip.domain.auth.dto.LoginDto
import com.oz.office_tastezip.domain.auth.service.AuthService
import com.oz.office_tastezip.global.constant.AuthConstants.RedisKey
import com.oz.office_tastezip.global.exception.DataNotFoundException
import com.oz.office_tastezip.global.exception.InvalidTokenException
import com.oz.office_tastezip.global.response.Response
import com.oz.office_tastezip.global.response.ResponseCode
import com.oz.office_tastezip.global.response.ResponseSuccess
import com.oz.office_tastezip.global.security.dto.CustomUserDetails
import com.oz.office_tastezip.global.security.dto.TokenDto.TokenResponse
import com.oz.office_tastezip.global.security.jwt.JwtTokenProvider
import com.oz.office_tastezip.global.util.RSAUtils
import com.oz.office_tastezip.global.util.RedisUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*

private val log = KotlinLogging.logger {}

@Tag(name = "인증 컨트롤러", description = "AUTH CONTROLLER")
@RestController
@RequestMapping("/api/v1/otz/auth")
class AuthController(
    private val redisUtils: RedisUtils,
    private val authService: AuthService,
    private val objectMapper: ObjectMapper,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder
) {

    private var privateKey: String? = null

    @Operation(summary = "Generate RSA public key")
    @GetMapping("/rsa")
    fun generateRsaKeyMap(): ResponseEntity<Response.Body<String>> {
        val keyMap = RSAUtils.generateRsaKeyMap()
        privateKey = keyMap["privateKey"]
        return ResponseSuccess<String>().success(keyMap["publicKey"]!!)
    }

    @Operation(summary = "로그인(토큰 발급)")
    @PostMapping("/login")
    fun authorize(
        @RequestBody loginDto: @Valid LoginDto,
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse
    ): ResponseEntity<Response.Body<TokenResponse>> {
        val remoteAddr = httpServletRequest.remoteAddr
        log.info("{}|Input login payload: {}", remoteAddr, loginDto)
        loginDto.password = RSAUtils.decrypt(loginDto.password, privateKey!!)

        val authenticationToken =
            UsernamePasswordAuthenticationToken(loginDto.email, loginDto.password)

        val authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken)
        SecurityContextHolder.getContext().authentication = authentication

        val customUserDetails = objectMapper.convertValue(authentication.principal, CustomUserDetails::class.java)
        val tokenResponse = getTokenResponse(customUserDetails.email, customUserDetails.role, httpServletResponse)
        tokenResponse.nickname = customUserDetails.nickname

        authService.updateLastLoginAt(customUserDetails.uuid)
        return ResponseSuccess<TokenResponse>().success(tokenResponse)
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/reissue")
    fun reissueToken(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<Response.Body<TokenResponse>> {
        val refreshToken: String = extractRefreshTokenFromCookie(request)

        val email = jwtTokenProvider.refreshTokenValidCheck(refreshToken)
        val user = authService.selectUser(email)
        val tokenResponse = getTokenResponse(user.email, user.role.name, response)
        tokenResponse.nickname = user.nickname

        return ResponseSuccess<TokenResponse>().success(tokenResponse)
    }

    private fun getTokenResponse(email: String, role: String, response: HttpServletResponse): TokenResponse {
        val tokenDto = jwtTokenProvider.generateToken(email, role)
        response.setHeader("Set-Cookie", "refreshToken=${tokenDto.refreshToken}; HttpOnly; Path=/")
        return TokenResponse(tokenDto.accessToken, email)
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Response.Body<String>> {
        val refreshToken: String = extractRefreshTokenFromCookie(request)

        val email: String
        try {
            email = jwtTokenProvider.refreshTokenValidCheck(refreshToken)
        } catch (e: InvalidTokenException) {
            log.warn("Invalid refresh token during logout: {}", e.message)
            throw InvalidTokenException()
        }

        val redisKey = RedisKey.JWT_KEY_PREFIX + email
        if (redisUtils.getOrNull(redisKey) == null) {
            throw InvalidTokenException("이미 로그아웃된 사용자이거나 세션이 만료되었습니다.")
        }

        redisUtils.delete(redisKey)
        response.setHeader("Set-Cookie", "refreshToken=; Path=/; HttpOnly; Max-Age=0")
        return ResponseSuccess<String>().success("로그아웃 되었습니다.")
    }

    private fun extractRefreshTokenFromCookie(request: HttpServletRequest): String {
        return request.cookies
            ?.firstOrNull { it.name == "refreshToken" }
            ?.value
            ?: throw DataNotFoundException(ResponseCode.TOKEN_DOES_NOT_EXIST, "Refresh Token이 없습니다.")
    }


}
