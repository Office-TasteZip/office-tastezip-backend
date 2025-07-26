package com.oz.office_tastezip.domain.auth.service;

import com.oz.office_tastezip.domain.user.User;
import com.oz.office_tastezip.domain.user.repository.UserRepository;
import com.oz.office_tastezip.global.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void updateLastLoginAt(String uuid) {
        userRepository.updateLastLoginAtByUserUUID(uuid);
    }

    public User selectUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

}
