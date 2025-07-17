package com.oz.office_tastezip.api.auth;

import com.oz.office_tastezip.global.util.RSAUtils;
import com.oz.office_tastezip.global.response.Response;
import com.oz.office_tastezip.global.response.ResponseSuccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
