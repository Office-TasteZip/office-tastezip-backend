package com.oz.office_tastezip.domain.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.oz.office_tastezip.domain.auth.dto.LoginDto
import com.oz.office_tastezip.domain.auth.dto.ResetPasswordDto
import com.oz.office_tastezip.domain.auth.enums.EmailVerificationPurpose
import com.oz.office_tastezip.domain.auth.enums.EmailVerificationPurpose.RESET_PASSWORD
import com.oz.office_tastezip.domain.auth.enums.EmailVerificationPurpose.SIGNUP
import com.oz.office_tastezip.domain.auth.service.AuthService
import com.oz.office_tastezip.domain.auth.service.MailService
import com.oz.office_tastezip.domain.user.dto.EmailVerificationCheckDto
import com.oz.office_tastezip.domain.user.dto.EmailVerificationRequestDto
import com.oz.office_tastezip.global.constant.AuthConstants.RedisKey.JWT_KEY_PREFIX
import com.oz.office_tastezip.global.exception.DataExistsException
import com.oz.office_tastezip.global.exception.DataNotFoundException
import com.oz.office_tastezip.global.exception.InvalidTokenException
import com.oz.office_tastezip.global.exception.ValidationFailureException
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
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

@Tag(name = "인증 컨트롤러", description = "AUTH CONTROLLER")
@RestController
@RequestMapping("/api/v1/otz/auth")
class AuthController(
    private val redisUtils: RedisUtils,
    private val authService: AuthService,
    private val mailService: MailService,
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
        log.info { "${httpServletRequest.remoteAddr}|Input login payload: $loginDto" }
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

        val redisKey = "$JWT_KEY_PREFIX$email"
        if (redisUtils.getOrNull(redisKey) == null) {
            throw InvalidTokenException("이미 로그아웃된 사용자이거나 세션이 만료되었습니다.")
        }

        redisUtils.delete(redisKey)
        response.setHeader("Set-Cookie", "refreshToken=; Path=/; HttpOnly; Max-Age=0")
        return ResponseSuccess<String>().success("로그아웃 되었습니다.")
    }

    @Operation(summary = "이메일 인증번호 발송")
    @PostMapping("/email/verify")
    fun sendSignupVerificationEmail(
        @RequestBody @Valid emailVerificationRequestDto: EmailVerificationRequestDto,
        request: HttpServletRequest
    ): ResponseEntity<Response.Body<String>> {
        return sendVerificationEmail(request, SIGNUP, emailVerificationRequestDto)
    }

    @Operation(summary = "이메일 인증 확인")
    @PostMapping("/email/verify/check")
    fun checkEmailVerification(
        @RequestBody @Valid emailVerificationCheckDto: EmailVerificationCheckDto,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<Response.Body<String>> {
        return checkVerificationEmail(emailVerificationCheckDto, SIGNUP, httpServletRequest)
    }

    @Operation(summary = "비밀번호 초기화 인증 메일 발송")
    @PostMapping("/reset-password/email/verify")
    fun sendResetPasswordVerificationEmail(
        @RequestBody @Valid emailVerificationRequestDto: EmailVerificationRequestDto,
        request: HttpServletRequest
    ): ResponseEntity<Response.Body<String>> {
        return sendVerificationEmail(request, RESET_PASSWORD, emailVerificationRequestDto)
    }

    @Operation(summary = "비밀번호 초기화 인증 확인")
    @PostMapping("/reset-password/email/verify/check")
    fun checkResetPasswordEmailVerification(
        @RequestBody @Valid emailVerificationCheckDto: EmailVerificationCheckDto,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<Response.Body<String>> {
        return checkVerificationEmail(emailVerificationCheckDto, RESET_PASSWORD, httpServletRequest)
    }

    @Operation(summary = "비밀번호 초기화")
    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestBody @Valid resetPasswordDto: ResetPasswordDto,
        request: HttpServletRequest
    ): ResponseEntity<Response.Body<String>> {
        log.info { "${request.remoteAddr}|ResetPassword, target: $resetPasswordDto" }

        val email = resetPasswordDto.email
        require(
            redisUtils.get("${RESET_PASSWORD.verifyKeyPrefix}$email") as? String == "complete"
        ) { throw ValidationFailureException("이메일 인증이 완료되지 않았습니다.") }

        authService.resetPassword(email, resetPasswordDto.password)
        return ResponseSuccess<String>().success("비밀번호가 변경되었습니다.");
    }

    private fun sendVerificationEmail(
        request: HttpServletRequest,
        emailVerificationPurpose: EmailVerificationPurpose,
        emailVerificationRequestDto: EmailVerificationRequestDto
    ): ResponseEntity<Response.Body<String>> {
        val remoteAddr = request.remoteAddr
        val requestUri = request.requestURI
        val email = emailVerificationRequestDto.email

        log.info { "$requestUri|$remoteAddr|이메일 인증 발송 요청: $email, purpose: $emailVerificationPurpose" }

        val isEmailRegistered = authService.countByEmail(email)

        when (emailVerificationPurpose) {
            SIGNUP -> if (isEmailRegistered) throw DataExistsException(ResponseCode.DUPLICATED_EMAIL)
            RESET_PASSWORD -> if (!isEmailRegistered) throw DataNotFoundException("가입된 이메일이 아닙니다.")
        }

        mailService.sendVerificationEmail(email, emailVerificationPurpose, requestUri)
        return ResponseSuccess<String>().success("인증번호가 전송되었습니다. 5분 이내에 인증을 완료하여 주십시오.")
    }

    private fun checkVerificationEmail(
        emailVerificationCheckDto: EmailVerificationCheckDto,
        emailVerificationPurpose: EmailVerificationPurpose,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<Response.Body<String>> {
        val requestEmail = emailVerificationCheckDto.email
        val requestUri = httpServletRequest.requestURI
        val remoteAddr = httpServletRequest.remoteAddr
        log.info { "$requestUri|$remoteAddr|이메일 인증 확인 요청: $requestEmail" }

        val originCode = redisUtils.get("${emailVerificationPurpose.codeKeyPrefix}$requestEmail") as String?
            ?: throw DataNotFoundException("인증번호가 만료되었습니다. 다시 요청해주시기 바랍니다.")

        val requestCode = emailVerificationCheckDto.code
        log.info { "email: $requestEmail, origin code: $originCode, request code: $requestCode" }

        if (originCode != requestCode) {
            throw ValidationFailureException("인증번호가 일치하지 않습니다.")
        }

        redisUtils.set("${emailVerificationPurpose.verifyKeyPrefix}$requestEmail", "complete", 30, TimeUnit.MINUTES)
        return ResponseSuccess<String>().success("인증되었습니다.")
    }

    private fun extractRefreshTokenFromCookie(request: HttpServletRequest): String {
        return request.cookies
            ?.firstOrNull { it.name == "refreshToken" }
            ?.value
            ?: throw DataNotFoundException(ResponseCode.TOKEN_DOES_NOT_EXIST, "Refresh Token이 없습니다.")
    }

}
