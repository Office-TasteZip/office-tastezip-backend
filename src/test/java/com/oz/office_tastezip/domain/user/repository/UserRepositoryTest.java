package com.oz.office_tastezip.domain.user.repository;

import com.oz.office_tastezip.domain.user.User;
import com.oz.office_tastezip.domain.user.dto.UserRequestDto;
import com.oz.office_tastezip.domain.user.enums.UserJob;
import com.oz.office_tastezip.domain.user.enums.UserPosition;
import com.oz.office_tastezip.enums.UserRole;
import com.oz.office_tastezip.enums.UserStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @PersistenceContext
    private EntityManager em;


    @Test
    @DisplayName("사용자 저장 테스트")
    void insertUserTest() {
        // given
        User user = User.create(getUserInsertRequest());

        // when
        User saved = userRepository.save(user);

        // then
        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isPresent();

        User result = found.get();

        // 핵심 필드 검증
        assertThat(result.getEmail()).isEqualTo("tester@example.com");
        assertThat(result.getPasswordHash()).isEqualTo("password");
        assertThat(result.getNickname()).isEqualTo("tester");

        // Enum 필드 검증
        assertThat(result.getJob()).isEqualTo(UserJob.PRODUCTION);
        assertThat(result.getPosition()).isEqualTo(UserPosition.SENIOR);
        assertThat(result.getRole()).isEqualTo(UserRole.ROLE_USER);
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);

        // 숫자, Boolean 필드
        assertThat(result.getJoinYear()).isEqualTo("2025");
        assertThat(result.isMarketingOptIn()).isFalse();

        // 시간 필드 (존재만 확인)
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        assertThat(result.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("사용자 정보(내정보) 조회 테스트")
    void selectMyInfoTest() {
        // given
        User user = User.create(getUserInsertRequest());
        userRepository.save(user);
        userRepository.flush();

        // when
        Optional<User> found = userRepository.findByUserUUID(String.valueOf(user.getId()));

        // then
        assertThat(found).isPresent();
        User result = found.get();
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getNickname()).isEqualTo("tester");
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void test() {
        // Given
        User user = User.create(getUserInsertRequest());
        userRepository.save(user);
        userRepository.flush();

        userRepository.deleteByUserUUID(String.valueOf(user.getId()));

        em.clear();

        // When
        Optional<User> byUserUUID = userRepository.findById(user.getId());

        // Then
        assertThat(byUserUUID).isPresent();
        User result = byUserUUID.get();
        log.info("result user info: {}", result);
        assertThat(result.getDeletedAt()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.WITHDRAWN);
    }

    private UserRequestDto.UserInsertRequest getUserInsertRequest() {
        return new UserRequestDto.UserInsertRequest(
                "tester@example.com",
                "password",
                "tester",
                "production",
                "senior",
                "2025",
                false,
                null
        );
    }
}
