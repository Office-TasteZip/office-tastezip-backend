package com.oz.office_tastezip.api.user;

import com.oz.office_tastezip.common.response.Response;
import com.oz.office_tastezip.common.response.ResponseSuccess;
import com.oz.office_tastezip.domain.user.UserService;
import com.oz.office_tastezip.domain.user.dto.UserRequestDto;
import com.oz.office_tastezip.domain.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/otz/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/my-info")
    public ResponseEntity<Response.Body<UserResponseDto>> getMyInfo(@RequestParam(name = "email") String email) {
        return new ResponseSuccess<UserResponseDto>().success(UserResponseDto.of(userService.findByEmail(email)));
    }

    @PostMapping("/register")
    public ResponseEntity<Response.Body<String>> register(@RequestBody UserRequestDto.UserInsertRequest userInsertRequest) {
        // TODO Validation check
        userService.register(userInsertRequest);
        return new ResponseSuccess<String>().success("회원가입 되었습니다.");
    }
}
