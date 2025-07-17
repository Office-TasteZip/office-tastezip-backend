package com.oz.office_tastezip.api.user;

import com.oz.office_tastezip.domain.user.UserService;
import com.oz.office_tastezip.domain.user.dto.UserRequestDto.UserInsertRequest;
import com.oz.office_tastezip.domain.user.dto.UserRequestDto.UserUpdateRequest;
import com.oz.office_tastezip.domain.user.dto.UserResponseDto;
import com.oz.office_tastezip.global.response.Response;
import com.oz.office_tastezip.global.response.ResponseCode;
import com.oz.office_tastezip.global.response.ResponseFail;
import com.oz.office_tastezip.global.response.ResponseSuccess;
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

    @GetMapping("/my-info")
    public ResponseEntity<Response.Body<UserResponseDto>> getMyInfo(@RequestParam(name = "uuid") String uuid) {
        return new ResponseSuccess<UserResponseDto>().success(UserResponseDto.of(userService.findByUserUUID(uuid)));
    }

    @GetMapping("/exists/email")
    public ResponseEntity<Response.Body<String>> checkEmailDuplicate(@RequestParam String email) {
        return userService.countByEmail(email)
                ? new ResponseSuccess<String>().success(String.format("[%s]은 사용 가능한 이메일입니다.", email))
                : new ResponseFail<String>(ResponseCode.DUPLICATED_EMAIL).fail();
    }

    @PostMapping("/register")
    public ResponseEntity<Response.Body<String>> register(@RequestBody @Valid UserInsertRequest userInsertRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return new ResponseFail<String>(ResponseCode.VALIDATION_ERROR, errorMessage).fail();
        }

        userService.register(userInsertRequest);
        return new ResponseSuccess<String>().success("회원 가입 되었습니다.");
    }

    @PutMapping("/update")
    public ResponseEntity<Response.Body<String>> update(@RequestBody @Valid UserUpdateRequest userUpdateRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return new ResponseFail<String>(ResponseCode.VALIDATION_ERROR, errorMessage).fail();
        }

        userService.update(userUpdateRequest);
        return new ResponseSuccess<String>().success("정보 수정 되었습니다.");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Response.Body<String>> withdraw(@PathVariable String userId) {
        userService.withdraw(userId);
        return new ResponseSuccess<String>().success("회원 탈퇴 되었습니다.");
    }
}
