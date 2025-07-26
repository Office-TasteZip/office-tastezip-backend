package com.oz.office_tastezip.global.util;

import com.oz.office_tastezip.global.exception.InvalidTokenException;
import com.oz.office_tastezip.global.exception.RequestFailureException;
import com.oz.office_tastezip.global.security.dto.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static CustomUserDetails getAuthenticatedUserDetail() {
        try {
            Object userDetails = SecurityContextHolder.getContext().getAuthentication().getDetails();

            if (userDetails instanceof CustomUserDetails) {
                return (CustomUserDetails) userDetails;
            }

            throw new InvalidTokenException();
        } catch (Exception e) {
            throw new RequestFailureException("SecurityContextHolder parsed error.");
        }
    }

}
