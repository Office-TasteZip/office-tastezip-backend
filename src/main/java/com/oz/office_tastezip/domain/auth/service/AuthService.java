package com.oz.office_tastezip.domain.auth.service;

import com.oz.office_tastezip.global.security.dto.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    public UserDetails loadUserByUserEmail(String email) {

        return new UserDetails();
    }
}
