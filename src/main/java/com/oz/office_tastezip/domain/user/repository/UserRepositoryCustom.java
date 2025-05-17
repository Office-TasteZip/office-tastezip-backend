package com.oz.office_tastezip.domain.user.repository;

import com.oz.office_tastezip.domain.user.User;

import java.util.List;

public interface UserRepositoryCustom {
    List<User> findByName(String name);
}
