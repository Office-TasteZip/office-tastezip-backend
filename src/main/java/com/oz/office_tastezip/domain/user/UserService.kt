package com.oz.office_tastezip.domain.user;

import com.oz.office_tastezip.domain.user.dto.UserRequestDto;
import com.oz.office_tastezip.domain.user.repository.UserRepository;
import com.oz.office_tastezip.global.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(UserRequestDto.UserInsertRequest userInsertRequest) {
        User user = User.create(userInsertRequest, passwordEncoder);
        log.info("Insert user info: {}", user);

        userRepository.save(user);

        // TODO image > s3
    }

    public boolean countByEmail(String email) {
        log.info("count by email, target: {}", email);
        return userRepository.countByEmail(email) == 0;
    }

    public User findByUserUUID(String id) {
        return userRepository.findByUserUUID(id).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void withdraw(String id) {
        log.info("withdraw user, id: {}", id);
        userRepository.deleteByUserUUID(id);
    }

    @Transactional
    public void update(UserRequestDto.UserUpdateRequest userUpdateRequest) {
        log.info("update user info: {}", userUpdateRequest);
        userRepository.updateByUserUUID(userUpdateRequest);
    }
}
