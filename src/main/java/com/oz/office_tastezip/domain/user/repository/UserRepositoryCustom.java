package com.oz.office_tastezip.domain.user.repository;

import com.oz.office_tastezip.domain.user.User;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<User> findByUserUUID(String uuid);

    int countByEmail(String email);

    void deleteByUserUUID(String uuid);
}
