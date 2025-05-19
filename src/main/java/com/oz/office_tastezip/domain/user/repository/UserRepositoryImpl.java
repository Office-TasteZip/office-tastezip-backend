package com.oz.office_tastezip.domain.user.repository;

import com.oz.office_tastezip.domain.user.QUser;
import com.oz.office_tastezip.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        QUser user = QUser.user;

        User result = queryFactory.selectFrom(user)
                .where(user.deletedAt.isNull().and(user.email.eq(email)))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
