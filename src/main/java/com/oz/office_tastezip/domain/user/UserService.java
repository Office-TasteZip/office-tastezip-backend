package com.oz.office_tastezip.domain.user;

import com.oz.office_tastezip.domain.user.dto.UserRequest;
import com.oz.office_tastezip.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User insertUser(UserRequest.UserInsertRequest userInsertRequest) {
        User user = User.create(userInsertRequest);
        return userRepository.save(user);
    }
}
