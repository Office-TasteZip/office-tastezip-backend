package com.oz.office_tastezip.global.security.service;

import com.oz.office_tastezip.domain.user.User;
import com.oz.office_tastezip.domain.user.repository.UserRepository;
import com.oz.office_tastezip.global.exception.DataNotFoundException;
import com.oz.office_tastezip.global.exception.ValidationFailureException;
import com.oz.office_tastezip.global.response.ResponseCode;
import com.oz.office_tastezip.global.security.dto.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CustomUserDetailService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public CustomUserDetails loadUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new DataNotFoundException("죄송합니다. 이 이메일 주소를 사용하는 계정을 찾을 수 없습니다."));

        int loginFailLimitCnt = 10;
        if (user.getLoginFailCount() >= loginFailLimitCnt) {
            throw new ValidationFailureException(ResponseCode.ACCOUNT_LOCK, "10회 이상 로그인 실패하여 계정 잠김 상태입니다.");
        }

        return new CustomUserDetails(user);
    }

}
