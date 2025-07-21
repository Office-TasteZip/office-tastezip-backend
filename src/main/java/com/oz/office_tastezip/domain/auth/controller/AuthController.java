package com.oz.office_tastezip.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oz.office_tastezip.domain.auth.dto.LoginDto;
import com.oz.office_tastezip.domain.auth.service.AuthService;
import com.oz.office_tastezip.global.response.Response;
import com.oz.office_tastezip.global.response.ResponseSuccess;
import com.oz.office_tastezip.global.security.dto.CustomUserDetails;
import com.oz.office_tastezip.global.security.dto.TokenDto;
import com.oz.office_tastezip.global.security.dto.TokenDto.TokenResponse;
import com.oz.office_tastezip.global.security.jwt.JwtTokenProvider;
import com.oz.office_tastezip.global.util.RSAUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.oz.office_tastezip.global.constant.AuthConstants.RedisKey.JWT_KEY_PREFIX;

@Tag(name = "인증 컨트롤러", description = "AUTH CONTROLLER")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/otz/auth")
public class AuthController {

    private String privateKey;

    private final AuthService authService;
    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Operation(summary = "Generate RSA public key")
    @GetMapping("/rsa")
    public ResponseEntity<Response.Body<String>> generateRsaKeyMap() {
        Map<String, String> keyMap = RSAUtils.generateRsaKeyMap();
        privateKey = keyMap.get("privateKey");
        return new ResponseSuccess<String>().success(keyMap.get("publicKey"));
    }

    @Operation(summary = "로그인 (토큰 발급)")
    @PostMapping("/login")
    public ResponseEntity<Response.Body<TokenResponse>> authorize(
            @RequestBody @Valid LoginDto loginDto,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {
        String remoteAddr = httpServletRequest.getRemoteAddr();
        log.info("{}|Input login payload: {}", remoteAddr, loginDto);
        loginDto.setPassword(RSAUtils.decrypt(loginDto.getPassword(), privateKey));

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails customUserDetails = objectMapper.convertValue(authentication.getPrincipal(), CustomUserDetails.class);
        TokenResponse tokenResponse = getTokenResponse(authentication, httpServletResponse);
        tokenResponse.setNickname(customUserDetails.getNickname());

        authService.updateLastLoginAt(customUserDetails.getUuid());
        return new ResponseSuccess<TokenResponse>().success(tokenResponse);
    }

    private TokenResponse getTokenResponse(Authentication authentication, HttpServletResponse response) {
        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication, JWT_KEY_PREFIX);
        response.setHeader("Set-Cookie", String.format("refreshToken=%s; HttpOnly; Path=/", tokenDto.getRefreshToken()));
        return new TokenResponse(tokenDto.getAccessToken(), authentication.getName());
    }
}
