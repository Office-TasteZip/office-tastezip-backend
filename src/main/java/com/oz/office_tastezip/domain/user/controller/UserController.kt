package com.oz.office_tastezip.domain.user.controller

import com.oz.office_tastezip.domain.user.UserService
import com.oz.office_tastezip.domain.user.dto.EmailVerificationCheckDto
import com.oz.office_tastezip.domain.user.dto.EmailVerificationRequestDto
import com.oz.office_tastezip.domain.user.dto.UserRequestDto.UserInsertRequest
import com.oz.office_tastezip.domain.user.dto.UserRequestDto.UserUpdateRequest
import com.oz.office_tastezip.domain.user.dto.UserResponseDto
import com.oz.office_tastezip.global.constant.AuthConstants.RedisKey.EMAIL_ATTEMPT_KEY_PREFIX
import com.oz.office_tastezip.global.constant.AuthConstants.RedisKey.EMAIL_KEY_PREFIX
import com.oz.office_tastezip.global.constant.AuthConstants.RedisKey.EMAIL_VERIFY_KEY_PREFIX
import com.oz.office_tastezip.global.exception.DataExistsException
import com.oz.office_tastezip.global.exception.DataNotFoundException
import com.oz.office_tastezip.global.exception.RequestFailureException
import com.oz.office_tastezip.global.exception.ValidationFailureException
import com.oz.office_tastezip.global.response.Response
import com.oz.office_tastezip.global.response.ResponseCode
import com.oz.office_tastezip.global.response.ResponseSuccess
import com.oz.office_tastezip.global.util.RedisUtils
import com.oz.office_tastezip.global.util.SecurityUtils.getAuthenticatedUserDetail
import com.oz.office_tastezip.infrastructure.mail.MailClient
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

@Tag(name = "사용자 관련 컨트롤러", description = "USER CONTROLLER")
@RestController
@RequestMapping("/api/v1/otz/users")
class UserController(
    private val userService: UserService,
    private val mailClient: MailClient,
    private val redisUtils: RedisUtils
) {

    @Operation(summary = "이메일 인증번호 발송")
    @PostMapping("/email/verify")
    fun sendSignupVerificationEmail(
        @RequestBody @Valid emailVerificationRequestDto: EmailVerificationRequestDto,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<Response.Body<String>> {
        val remoteAddr = httpServletRequest.remoteAddr
        log.info { "$remoteAddr|이메일 인증 발송 요청: ${emailVerificationRequestDto.email}" }

        val requestEmail = emailVerificationRequestDto.email
        if (!userService.countByEmail(requestEmail)) {
            throw DataExistsException(ResponseCode.DUPLICATED_EMAIL)
        }

        val key = "$EMAIL_ATTEMPT_KEY_PREFIX$requestEmail"
        val attempts = redisUtils.get(key)?.toString()?.toIntOrNull() ?: 0
        val ttl = redisUtils.getExpire(key, TimeUnit.SECONDS)

        log.info { "/email/verify: attempts: $attempts, ttl: $ttl" }
        if (attempts >= 3 && ttl > 0) {
            val minutes = (ttl + 59) / 60
            throw RequestFailureException("SMS 요청이 너무 많습니다. ${minutes}분 후 다시 시도해 주세요.")
        }

        val verificationCode = UUID.randomUUID().toString().replace("-", "")
        mailClient.sendMimeMail(requestEmail, "[오피스 맛집] 회원가입 이메일 인증 안내", verificationCode)

        redisUtils.set(key, (attempts + 1).toString(), 3, TimeUnit.MINUTES)
        redisUtils.set("$EMAIL_KEY_PREFIX$requestEmail", verificationCode, 5, TimeUnit.MINUTES)
        return ResponseSuccess<String>().success("인증번호가 전송되었습니다. 5분 이내에 인증을 완료하여 주십시오.")
    }

    @Operation(summary = "이메일 인증 확인")
    @PostMapping("/email/verify/check")
    fun checkEmailVerification(
        @RequestBody @Valid emailVerificationCheckDto: EmailVerificationCheckDto,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<Response.Body<String>> {
        val requestEmail = emailVerificationCheckDto.email
        log.info { "${httpServletRequest.remoteAddr}|이메일 인증 확인 요청: $requestEmail" }

        val originCode = redisUtils.get("$EMAIL_KEY_PREFIX$requestEmail") as String?
            ?: throw DataNotFoundException("인증번호가 만료되었습니다. 다시 요청해주시기 바랍니다.")

        val requestCode = emailVerificationCheckDto.code
        log.info { "email: $requestEmail, origin code: $originCode, request code: $requestCode" }

        if (originCode != requestCode) {
            throw ValidationFailureException("인증번호가 일치하지 않습니다.")
        }

        redisUtils.set("$EMAIL_VERIFY_KEY_PREFIX$requestEmail", "complete", 30, TimeUnit.MINUTES)
        return ResponseSuccess<String>().success("인증되었습니다.")
    }

    @Operation(summary = "회원 가입")
    @PostMapping("/register")
    fun register(@RequestBody @Valid userInsertRequest: UserInsertRequest): ResponseEntity<Response.Body<String>> {
        if (userInsertRequest.password != userInsertRequest.confirmPassword) {
            log.info { "User register failed: password and confirmation do not match. " +
                    "password: ${userInsertRequest.password}, password confirm: ${userInsertRequest.confirmPassword}" }
            throw RequestFailureException("비밀번호와 비밀번호 확인이 일치하지 않습니다.")
        }

        require(
            redisUtils.get("$EMAIL_VERIFY_KEY_PREFIX${userInsertRequest.email}") as? String == "complete"
        ) { throw RequestFailureException("이메일 인증이 완료되지 않았습니다.") }

        userService.register(userInsertRequest)
        return ResponseSuccess<String>().success("회원 가입 되었습니다.")
    }

    @Operation(summary = "내정보 조회")
    @GetMapping("/my-info")
    fun getMyInfo(httpServletRequest: HttpServletRequest): ResponseEntity<Response.Body<UserResponseDto>> {
        val userDetails = getAuthenticatedUserDetail()
        val uuid = userDetails.uuid
        log.info { "${httpServletRequest.remoteAddr}|Select my-info, user uuid: $uuid, email: ${userDetails.email}" }
        return ResponseSuccess<UserResponseDto>().success(UserResponseDto.of(userService.findByUserUUID(uuid)))
    }

    @Operation(summary = "사용자 정보 수정")
    @PutMapping("/update")
    fun update(
        @RequestBody @Valid userUpdateRequest: UserUpdateRequest,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<Response.Body<String>> {
        val userDetails = getAuthenticatedUserDetail()
        log.info { "${httpServletRequest.remoteAddr}|Update my-info, user uuid: ${userDetails.uuid}, email: ${userDetails.email}" }
        userService.update(userUpdateRequest)
        return ResponseSuccess<String>().success("정보 수정 되었습니다.")
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/withdraw")
    fun withdraw(httpServletRequest: HttpServletRequest): ResponseEntity<Response.Body<String>> {
        val userDetails = getAuthenticatedUserDetail()
        log.info { "${httpServletRequest.remoteAddr}|Withdraw user uuid: ${userDetails.uuid}, email: ${userDetails.email}" }
        userService.withdraw("") // TODO
        return ResponseSuccess<String>().success("회원 탈퇴 되었습니다.")
    }
}
