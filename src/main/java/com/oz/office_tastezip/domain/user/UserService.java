package com.oz.office_tastezip.domain.user;

import com.oz.office_tastezip.domain.user.dto.UserRequestDto;
import com.oz.office_tastezip.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(UserRequestDto.UserInsertRequest userInsertRequest) {
        userRepository.save(User.create(userInsertRequest));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
    }
}
