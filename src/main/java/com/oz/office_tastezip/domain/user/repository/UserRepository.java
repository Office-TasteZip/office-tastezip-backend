package com.oz.office_tastezip.domain.user.repository;

import com.oz.office_tastezip.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, UserRepositoryCustom {
    Optional<User> findByEmail(String email);
}
