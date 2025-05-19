package com.oz.office_tastezip.api.user;

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
    public ResponseEntity<?> getMyInfo(@RequestParam(name = "email") String email) {
        return ResponseEntity.ok(UserResponseDto.of(userService.findByEmail(email)));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequestDto.UserInsertRequest userInsertRequest) {
        // TODO Validation check
        return ResponseEntity.ok(userService.register(userInsertRequest));
    }
}
