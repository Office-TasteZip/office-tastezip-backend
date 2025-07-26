package com.oz.office_tastezip.domain.user.controller;

import com.oz.office_tastezip.domain.user.UserService;
import com.oz.office_tastezip.domain.user.dto.EmailVerificationCheckDto;
import com.oz.office_tastezip.domain.user.dto.EmailVerificationRequestDto;
import com.oz.office_tastezip.domain.user.dto.UserRequestDto.UserInsertRequest;
import com.oz.office_tastezip.domain.user.dto.UserRequestDto.UserUpdateRequest;
import com.oz.office_tastezip.domain.user.dto.UserResponseDto;
import com.oz.office_tastezip.global.exception.DataExistsException;
import com.oz.office_tastezip.global.exception.RequestFailureException;
import com.oz.office_tastezip.global.response.Response;
import com.oz.office_tastezip.global.response.ResponseCode;
import com.oz.office_tastezip.global.response.ResponseSuccess;
import com.oz.office_tastezip.global.security.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.oz.office_tastezip.global.util.SecurityUtils.getAuthenticatedUserDetail;

@Tag(name = "사용자 관련 컨트롤러", description = "USER CONTROLLER")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/otz/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "이메일 인증번호 발송")
    @PostMapping("/email/verify")
    public ResponseEntity<Response.Body<String>> sendSignupVerificationEmail(
            @RequestBody @Valid EmailVerificationRequestDto emailVerificationRequestDto,
            HttpServletRequest httpServletRequest
    ) {
        String remoteAddr = httpServletRequest.getRemoteAddr();
        log.info("{}|이메일 인증 발송 요청: {}", remoteAddr, emailVerificationRequestDto.getEmail());

        // 1. Email 중복 체크
        String requestEmail = emailVerificationRequestDto.getEmail();
        if (!userService.countByEmail(requestEmail)) {
            throw new DataExistsException(ResponseCode.DUPLICATED_EMAIL);
        }


        // TODO 2. 인증 번호 발송

        // TODO 3. 인증 번호 Redis 저장
        return new ResponseSuccess<String>().success("");
    }

    @Operation(summary = "이메일 인증 확인")
    @PostMapping("/email/verify/check")
    public ResponseEntity<Response.Body<String>> checkEmailVerification(
            @RequestBody @Valid EmailVerificationCheckDto emailVerificationCheckDto,
            HttpServletRequest httpServletRequest
    ) {
        // TODO 받은 인증 번호와 Redis 내 인증 번호 비교 검증
        return new ResponseSuccess<String>().success("");
    }

    @Operation(summary = "내정보 조회")
    @GetMapping("/my-info")
    public ResponseEntity<Response.Body<UserResponseDto>> getMyInfo(HttpServletRequest httpServletRequest) {
        CustomUserDetails userDetails = getAuthenticatedUserDetail();
        String uuid = userDetails.getUuid();
        log.info("{}|Select my-info, user uuid: {}, email: {}", httpServletRequest.getRemoteAddr(), uuid, userDetails.getEmail());
        return new ResponseSuccess<UserResponseDto>().success(UserResponseDto.of(userService.findByUserUUID(uuid)));
    }

    @Operation(summary = "회원 가입")
    @PostMapping("/register")
    public ResponseEntity<Response.Body<String>> register(@RequestBody @Valid UserInsertRequest userInsertRequest) {
        if (!userInsertRequest.getPassword().equals(userInsertRequest.getConfirmPassword())) {
            log.info("User register failed: password and confirmation do not match. password: {}, password confirm: {}",
                    userInsertRequest.getPassword(), userInsertRequest.getConfirmPassword());
            throw new RequestFailureException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        userService.register(userInsertRequest);
        return new ResponseSuccess<String>().success("회원 가입 되었습니다.");
    }

    @Operation(summary = "사용자 정보 수정")
    @PutMapping("/update")
    public ResponseEntity<Response.Body<String>> update(
            @RequestBody @Valid UserUpdateRequest userUpdateRequest,
            HttpServletRequest httpServletRequest
    ) {
        // TODO
        CustomUserDetails userDetails = getAuthenticatedUserDetail();
        log.info("{}|Update my-info, user uuid: {}, email: {}", httpServletRequest.getRemoteAddr(), userDetails.getUuid(), userDetails.getEmail());
        userService.update(userUpdateRequest);
        return new ResponseSuccess<String>().success("정보 수정 되었습니다.");
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/withdraw")
    public ResponseEntity<Response.Body<String>> withdraw(HttpServletRequest httpServletRequest) {
        CustomUserDetails userDetails = getAuthenticatedUserDetail();
        log.info("{}|Withdraw user uuid: {}, email: {}", httpServletRequest.getRemoteAddr(), userDetails.getUuid(), userDetails.getEmail());
        userService.withdraw("");   // TODO
        return new ResponseSuccess<String>().success("회원 탈퇴 되었습니다.");
    }
}
