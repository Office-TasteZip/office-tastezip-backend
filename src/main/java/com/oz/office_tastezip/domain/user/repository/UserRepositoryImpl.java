package com.oz.office_tastezip.domain.user.repository;

import com.oz.office_tastezip.domain.user.QUser;
import com.oz.office_tastezip.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(EntityManager em, JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<User> findByName(String name) {
        QUser user = QUser.user;

        return queryFactory
                .selectFrom(user)
                .where(user.name.eq(name))
                .fetch();
    }
}
