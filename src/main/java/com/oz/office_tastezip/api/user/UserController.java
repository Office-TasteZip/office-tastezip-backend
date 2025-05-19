package com.oz.office_tastezip.api.user;

import com.oz.office_tastezip.domain.user.UserService;
import com.oz.office_tastezip.domain.user.dto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/otz/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequestDto.UserInsertRequest userInsertRequest) {
        return ResponseEntity.ok(userService.register(userInsertRequest));
    }
}
