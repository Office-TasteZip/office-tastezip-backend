package com.oz.office_tastezip.domain.user.controller

import com.oz.office_tastezip.domain.auth.enums.EmailVerificationPurpose.SIGNUP
import com.oz.office_tastezip.domain.user.UserService
import com.oz.office_tastezip.domain.user.dto.UserRequestDto.UserInsertRequest
import com.oz.office_tastezip.domain.user.dto.UserRequestDto.UserUpdateRequest
import com.oz.office_tastezip.domain.user.dto.UserResponseDto
import com.oz.office_tastezip.global.exception.RequestFailureException
import com.oz.office_tastezip.global.response.Response
import com.oz.office_tastezip.global.response.ResponseSuccess
import com.oz.office_tastezip.global.util.RedisUtils
import com.oz.office_tastezip.global.util.SecurityUtils.getAuthenticatedUserDetail
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val log = KotlinLogging.logger {}

@Tag(name = "사용자 관련 컨트롤러", description = "USER CONTROLLER")
@RestController
@RequestMapping("/api/v1/otz/users")
class UserController(
    private val userService: UserService,
    private val redisUtils: RedisUtils
) {

    @Operation(summary = "회원 가입")
    @PostMapping("/register")
    fun register(@RequestBody @Valid userInsertRequest: UserInsertRequest): ResponseEntity<Response.Body<String>> {
        if (userInsertRequest.password != userInsertRequest.confirmPassword) {
            log.info {
                "User register failed: password and confirmation do not match. " +
                        "password: ${userInsertRequest.password}, password confirm: ${userInsertRequest.confirmPassword}"
            }
            throw RequestFailureException("비밀번호와 비밀번호 확인이 일치하지 않습니다.")
        }

        require(
            redisUtils.get("${SIGNUP.verifyKeyPrefix}${userInsertRequest.email}") as? String == "complete"
        ) { throw RequestFailureException("이메일 인증이 완료되지 않았습니다.") }

        userService.register(userInsertRequest)
        return ResponseSuccess<String>().success("회원가입 되었습니다.")
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
        userService.withdraw(userDetails.uuid)
        return ResponseSuccess<String>().success("회원 탈퇴 되었습니다.")
    }
}
