package com.oz.office_tastezip.domain.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oz.office_tastezip.domain.auth.dto.LoginDto;
import com.oz.office_tastezip.global.response.ResponseCode;
import com.oz.office_tastezip.global.util.RSAUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static com.oz.office_tastezip.global.response.ResponseCode.INVALID_PASSWORD;
import static com.oz.office_tastezip.global.response.ResponseCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Value("${test.server-url}")
    private String host;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginDto loginDto;
    private String requestBaseUri;
    private String rsaPublicKey;

    @BeforeEach
    void fetchRsaKeyAndEncrypt() throws JsonProcessingException {
        requestBaseUri = host + port + "/api/v1/otz/auth";
        String url = requestBaseUri + "/rsa";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        log.info("RSA Body: {}", response.getBody());

        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        rsaPublicKey = jsonNode.get("data").asText();
    }

    @Test
    @DisplayName("로그인 요청 성공")
    void login_success() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        loginDto = new LoginDto("tester@example.com", RSAUtils.encrypt("password123!", rsaPublicKey));
        HttpEntity<LoginDto> request = new HttpEntity<>(loginDto, headers);

        String url = requestBaseUri + "/login";
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(ResponseCode.SUCCESS.getHttpStatus());
        assertThat(response.getBody()).contains("accessToken");

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        JsonNode dataResponse = jsonNode.get("data");
        String accessToken = dataResponse.get("accessToken").asText();

        log.info("Response access token: {}", accessToken);
    }

    @Test
    @DisplayName("로그인 요청 실패 - 존재하지 않는 사용자")
    void login_should_fail_when_user_does_not_exist() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        loginDto = new LoginDto("tester@test.com", RSAUtils.encrypt("password123!", rsaPublicKey));
        HttpEntity<LoginDto> request = new HttpEntity<>(loginDto, headers);

        String url = requestBaseUri + "/login";
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(USER_NOT_FOUND.getHttpStatus());
        assertThat(response.getBody()).contains(USER_NOT_FOUND.getCode());
    }

    @Test
    @DisplayName("로그인 요청 실패 - 비밀번호 오류")
    void login_should_fail_when_password_does_not_match() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        loginDto = new LoginDto("tester@example.com", RSAUtils.encrypt("password", rsaPublicKey));
        HttpEntity<LoginDto> request = new HttpEntity<>(loginDto, headers);

        String url = requestBaseUri + "/login";
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(INVALID_PASSWORD.getHttpStatus());
        assertThat(response.getBody()).contains(INVALID_PASSWORD.getCode());
    }

}
