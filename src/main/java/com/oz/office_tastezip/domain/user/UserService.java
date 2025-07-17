package com.oz.office_tastezip.domain.user;

import com.oz.office_tastezip.domain.user.dto.UserRequestDto;
import com.oz.office_tastezip.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void register(UserRequestDto.UserInsertRequest userInsertRequest) {
        User user = User.create(userInsertRequest);
        log.info("Insert user info: {}", user);
        userRepository.save(user);

        // TODO image > s3
    }

    public boolean findByEmail(String email) {
        return userRepository.countByEmail(email) == 0;
    }

    public User findByUserUUID(String id) {
        return userRepository.findByUserUUID(id).orElseThrow(RuntimeException::new);
    }

    @Transactional
    public void withdrawUser(String id) {
        log.info("withdraw user, id: {}", id);
        userRepository.deleteByUserUUID(id);
    }
}
