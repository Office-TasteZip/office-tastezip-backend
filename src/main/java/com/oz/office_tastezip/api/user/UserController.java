package com.oz.office_tastezip.api.user;

import com.oz.office_tastezip.domain.user.UserService;
import com.oz.office_tastezip.domain.user.dto.EmailVerificationCheckDto;
import com.oz.office_tastezip.domain.user.dto.EmailVerificationRequestDto;
import com.oz.office_tastezip.domain.user.dto.UserRequestDto.UserInsertRequest;
import com.oz.office_tastezip.domain.user.dto.UserRequestDto.UserUpdateRequest;
import com.oz.office_tastezip.domain.user.dto.UserResponseDto;
import com.oz.office_tastezip.global.exception.DataExistsException;
import com.oz.office_tastezip.global.response.Response;
import com.oz.office_tastezip.global.response.ResponseCode;
import com.oz.office_tastezip.global.response.ResponseFail;
import com.oz.office_tastezip.global.response.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
        // 1. Email 중복 체크
        String requestEmail = emailVerificationRequestDto.getEmail();
        if (!userService.countByEmail(requestEmail)) throw new DataExistsException(ResponseCode.DUPLICATED_EMAIL);

        // TODO 2. 인증 번호 발송

        return new ResponseSuccess<String>().success();
    }

    @PostMapping("/email/verify/check")
    public ResponseEntity<Response.Body<String>> checkEmailVerification(
            @RequestBody @Valid EmailVerificationCheckDto emailVerificationCheckDto,
            HttpServletRequest httpServletRequest
    ) {

        return new ResponseSuccess<String>().success();
    }

    @GetMapping("/my-info")
    public ResponseEntity<Response.Body<UserResponseDto>> getMyInfo(@RequestParam(name = "uuid") String uuid) {
        return new ResponseSuccess<UserResponseDto>().success(UserResponseDto.of(userService.findByUserUUID(uuid)));
    }

    @PostMapping("/register")
    public ResponseEntity<Response.Body<String>> register(@RequestBody @Valid UserInsertRequest userInsertRequest) {
        userService.register(userInsertRequest);
        return new ResponseSuccess<String>().success("회원 가입 되었습니다.");
    }

    @PutMapping("/update")
    public ResponseEntity<Response.Body<String>> update(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        userService.update(userUpdateRequest);
        return new ResponseSuccess<String>().success("정보 수정 되었습니다.");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Response.Body<String>> withdraw(@PathVariable String userId) {
        userService.withdraw(userId);
        return new ResponseSuccess<String>().success("회원 탈퇴 되었습니다.");
    }
}
