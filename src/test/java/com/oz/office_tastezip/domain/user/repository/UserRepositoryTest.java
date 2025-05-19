package com.oz.office_tastezip.domain.user.repository;

import com.oz.office_tastezip.domain.user.User;
import com.oz.office_tastezip.domain.user.dto.UserRequest;
import com.oz.office_tastezip.domain.user.enums.UserJob;
import com.oz.office_tastezip.domain.user.enums.UserPosition;
import com.oz.office_tastezip.global.emums.UserRole;
import com.oz.office_tastezip.global.emums.UserStatus;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 저장 테스트")
    void insertUserTest() {
        // given
        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 12, 0);
        User user = User.create(new UserRequest.UserInsertRequest(
                "tester@example.com",
                "password",
                "tester",
                "production",
                "senior",
                "2025",
                false,
                null
        ));

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
}
