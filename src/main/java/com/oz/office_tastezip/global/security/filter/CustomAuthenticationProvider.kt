package com.oz.office_tastezip.global.security.filter;

import com.oz.office_tastezip.global.exception.ValidationFailureException;
import com.oz.office_tastezip.global.security.dto.CustomUserDetails;
import com.oz.office_tastezip.global.security.service.CustomUserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;

import static com.oz.office_tastezip.global.response.ResponseCode.INVALID_PASSWORD;

@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailService customUserDetailService;

    public CustomAuthenticationProvider(
            PasswordEncoder passwordEncoder,
            CustomUserDetailService customUserDetailService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailService = customUserDetailService;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        CustomUserDetails customUserDetails = customUserDetailService.loadUserByEmail(email);

        if (!passwordEncoder.matches(password, customUserDetails.getPasswordHash())) {
            throw new ValidationFailureException(INVALID_PASSWORD, "아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
        customUserDetails.setAuthorities(null);
        return new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities);
    }

}
