package com.oz.office_tastezip.domain.user.controller;

import com.oz.office_tastezip.domain.user.UserService;
import com.oz.office_tastezip.domain.user.dto.EmailVerificationCheckDto;
import com.oz.office_tastezip.domain.user.dto.EmailVerificationRequestDto;
import com.oz.office_tastezip.domain.user.dto.UserRequestDto.UserInsertRequest;
import com.oz.office_tastezip.domain.user.dto.UserRequestDto.UserUpdateRequest;
import com.oz.office_tastezip.domain.user.dto.UserResponseDto;
import com.oz.office_tastezip.global.exception.DataExistsException;
import com.oz.office_tastezip.global.response.Response;
import com.oz.office_tastezip.global.response.ResponseCode;
import com.oz.office_tastezip.global.response.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/otz/users")
public class UserController {

    private final UserService userService;

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

        return new ResponseSuccess<String>().success("");
    }

    @PostMapping("/email/verify/check")
    public ResponseEntity<Response.Body<String>> checkEmailVerification(
            @RequestBody @Valid EmailVerificationCheckDto emailVerificationCheckDto,
            HttpServletRequest httpServletRequest
    ) {

        return new ResponseSuccess<String>().success("");
    }

    @GetMapping("/my-info")
    public ResponseEntity<Response.Body<UserResponseDto>> getMyInfo(HttpServletRequest httpServletRequest) {
        // TODO
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        log.info("{}|Withdraw user: {}", httpServletRequest.getRemoteAddr(), details);
        return new ResponseSuccess<UserResponseDto>().success(UserResponseDto.of(userService.findByUserUUID("")));
    }

    @PostMapping("/register")
    public ResponseEntity<Response.Body<String>> register(@RequestBody @Valid UserInsertRequest userInsertRequest) {
        userService.register(userInsertRequest);
        return new ResponseSuccess<String>().success("회원 가입 되었습니다.");
    }

    @PutMapping("/update")
    public ResponseEntity<Response.Body<String>> update(
            @RequestBody @Valid UserUpdateRequest userUpdateRequest,
            HttpServletRequest httpServletRequest
    ) {
        // TODO
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        log.info("{}|Withdraw user: {}", httpServletRequest.getRemoteAddr(), details);
        userService.update(userUpdateRequest);
        return new ResponseSuccess<String>().success("정보 수정 되었습니다.");
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<Response.Body<String>> withdraw(HttpServletRequest httpServletRequest) {
        // TODO
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        log.info("{}|Withdraw user: {}", httpServletRequest.getRemoteAddr(), details);
        userService.withdraw("");
        return new ResponseSuccess<String>().success("회원 탈퇴 되었습니다.");
    }
}
