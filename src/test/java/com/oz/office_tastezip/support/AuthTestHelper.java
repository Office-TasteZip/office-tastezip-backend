package com.oz.office_tastezip.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oz.office_tastezip.domain.auth.dto.LoginDto;
import com.oz.office_tastezip.global.util.RSAUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class AuthTestHelper {

    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    private String host;
    private int port;
    private String publicKey;

    public void setHostAndPort(String host, int port, String publicKey, TestRestTemplate restTemplate) {
        this.host = host;
        this.port = port;
        this.publicKey = publicKey;
        this.restTemplate = restTemplate;
    }

    public AuthTokens loginAndGetTokens(String email, String password) throws Exception {
        LoginDto loginDto = new LoginDto(email, RSAUtils.encrypt(password, publicKey));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginDto> request = new HttpEntity<>(loginDto, headers);

        ResponseEntity<String> loginRes = restTemplate.postForEntity(host + port + "/api/v1/otz/auth/login", request, String.class);

        String accessToken = objectMapper.readTree(loginRes.getBody()).get("data").get("accessToken").asText();
        String refreshToken = extractRefreshTokenFromSetCookie(Objects.requireNonNull(loginRes.getHeaders().get("Set-Cookie")));

        return new AuthTokens(accessToken, refreshToken);
    }

    private String extractRefreshTokenFromSetCookie(List<String> setCookies) {
        return setCookies.stream()
                .filter(cookie -> cookie.startsWith("refreshToken="))
                .map(cookie -> cookie.split("=")[1].split(";")[0])
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("refreshToken 쿠키 없음"));
    }

}
