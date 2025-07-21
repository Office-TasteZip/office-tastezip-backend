package com.oz.office_tastezip.api.auth;

import com.oz.office_tastezip.api.auth.dto.EmailVerificationCheckDto;
import com.oz.office_tastezip.api.auth.dto.EmailVerificationRequestDto;
import com.oz.office_tastezip.global.util.RSAUtils;
import com.oz.office_tastezip.global.response.Response;
import com.oz.office_tastezip.global.response.ResponseSuccess;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/otz/auth")
public class AuthController {

    private String privateKey;

    @GetMapping("/rsa")
    public ResponseEntity<Response.Body<String>> generateRsaKeyMap() {
        Map<String, String> keyMap = RSAUtils.generateRsaKeyMap();
        privateKey = keyMap.get("privateKey");
        return new ResponseSuccess<String>().success(keyMap.get("publicKey"));
    }

    @PostMapping("/email/verify")
    public ResponseEntity<Response.Body<String>> sendSignupVerificationEmail(
            @RequestBody EmailVerificationRequestDto emailVerificationRequestDto,
            HttpServletRequest httpServletRequest
    ) {
        // TODO 1. 중복 체크
        // TODO 2. 인증 번호 발송

        return new ResponseSuccess<String>().success();
    }

    @PostMapping("/email/verify/check")
    public ResponseEntity<Response.Body<String>> checkEmailVerification(
            @RequestBody EmailVerificationCheckDto emailVerificationCheckDto,
            HttpServletRequest httpServletRequest
    ) {

        return new ResponseSuccess<String>().success();
    }
}
