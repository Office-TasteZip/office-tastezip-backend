package com.oz.office_tastezip.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oz.office_tastezip.domain.auth.dto.LoginDto;
import com.oz.office_tastezip.domain.auth.service.AuthService;
import com.oz.office_tastezip.domain.user.User;
import com.oz.office_tastezip.global.exception.DataNotFoundException;
import com.oz.office_tastezip.global.exception.InvalidTokenException;
import com.oz.office_tastezip.global.response.Response;
import com.oz.office_tastezip.global.response.ResponseCode;
import com.oz.office_tastezip.global.response.ResponseSuccess;
import com.oz.office_tastezip.global.security.dto.CustomUserDetails;
import com.oz.office_tastezip.global.security.dto.TokenDto;
import com.oz.office_tastezip.global.security.dto.TokenDto.TokenResponse;
import com.oz.office_tastezip.global.security.jwt.JwtTokenProvider;
import com.oz.office_tastezip.global.util.RSAUtils;
import com.oz.office_tastezip.global.util.RedisUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
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

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static com.oz.office_tastezip.global.constant.AuthConstants.RedisKey.JWT_KEY_PREFIX;

@Tag(name = "인증 컨트롤러", description = "AUTH CONTROLLER")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/otz/auth")
public class AuthController {

    private String privateKey;

    private final RedisUtils redisUtils;
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

    @Operation(summary = "로그인(토큰 발급)")
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
        TokenResponse tokenResponse = getTokenResponse(customUserDetails.getEmail(), customUserDetails.getRole(), httpServletResponse);
        tokenResponse.setNickname(customUserDetails.getNickname());

        authService.updateLastLoginAt(customUserDetails.getUuid());
        return new ResponseSuccess<TokenResponse>().success(tokenResponse);
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<Response.Body<TokenResponse>> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request).orElseThrow(() ->
                new DataNotFoundException(ResponseCode.TOKEN_DOES_NOT_EXIST, "세션이 만료되었습니다. 로그인이 필요합니다."));

        String email = jwtTokenProvider.refreshTokenValidCheck(refreshToken);
        User user = authService.selectUser(email);
        TokenResponse tokenResponse = getTokenResponse(user.getEmail(), user.getRole().name(), response);
        tokenResponse.setNickname(user.getNickname());

        return new ResponseSuccess<TokenResponse>().success(tokenResponse);
    }

    private TokenResponse getTokenResponse(String email, String role, HttpServletResponse response) {
        TokenDto tokenDto = jwtTokenProvider.generateToken(email, role);
        response.setHeader("Set-Cookie", String.format("refreshToken=%s; HttpOnly; Path=/", tokenDto.getRefreshToken()));
        return new TokenResponse(tokenDto.getAccessToken(), email);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Response.Body<String>> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request).orElseThrow(() ->
                new DataNotFoundException(ResponseCode.TOKEN_DOES_NOT_EXIST, "Refresh Token이 없습니다."));

        String email;
        try {
            email = jwtTokenProvider.refreshTokenValidCheck(refreshToken);
        } catch (InvalidTokenException e) {
            log.warn("Invalid refresh token during logout: {}", e.getMessage());
            throw new InvalidTokenException();
        }

        String redisKey = JWT_KEY_PREFIX + email;
        if (redisUtils.getOrNull(redisKey) == null) {
            throw new InvalidTokenException("이미 로그아웃된 사용자이거나 세션이 만료되었습니다.");
        }

        redisUtils.delete(redisKey);
        response.setHeader("Set-Cookie", "refreshToken=; Path=/; HttpOnly; Max-Age=0");
        return new ResponseSuccess<String>().success("로그아웃 되었습니다.");
    }

    private Optional<String> extractRefreshTokenFromCookie(HttpServletRequest request) {
        return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

}
