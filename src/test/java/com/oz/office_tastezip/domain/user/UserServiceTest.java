package com.oz.office_tastezip.domain.user;

import com.oz.office_tastezip.global.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("존재하지 않는 사용자 조회 시 USER_NOT_FOUND")
    void test() {
        // Given
        String invalidUUID = UUID.randomUUID().toString();

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.findByUserUUID(invalidUUID));
    }
}
