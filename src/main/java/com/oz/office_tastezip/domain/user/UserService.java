package com.oz.office_tastezip.domain.user;

import com.oz.office_tastezip.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> searchByName(String name) {
        return userRepository.findByName(name);
    }
}
