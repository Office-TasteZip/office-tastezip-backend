package com.oz.office_tastezip.domain.user.repository;

import com.oz.office_tastezip.domain.user.QUser;
import com.oz.office_tastezip.domain.user.User;
import com.oz.office_tastezip.domain.user.dto.UserRequestDto;
import com.oz.office_tastezip.domain.user.enums.UserJob;
import com.oz.office_tastezip.domain.user.enums.UserPosition;
import com.oz.office_tastezip.domain.auth.enums.UserStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@Transactional
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUserUUID(String uuid) {
        QUser user = QUser.user;

        User result = queryFactory.selectFrom(user)
                .where(user.deletedAt.isNull().and(user.id.eq(UUID.fromString(uuid))))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    @Transactional(readOnly = true)
    public int countByEmail(String email) {
        QUser user = QUser.user;

        List<User> result = queryFactory.selectFrom(user)
                .where(user.email.eq(email))
                .fetch();

        return result.size();
    }

    @Override
    public void deleteByUserUUID(String uuid) {
        QUser user = QUser.user;

        queryFactory.update(user)
                .set(user.deletedAt, LocalDateTime.now())
                .set(user.status, UserStatus.WITHDRAWN)
                .where(user.id.eq(UUID.fromString(uuid)))
                .execute();
    }

    @Override
    public void updateByUserUUID(UserRequestDto.UserUpdateRequest userUpdateRequest) {
        QUser user = QUser.user;

        log.info("Update user info: {}", userUpdateRequest);

        // TODO
        long updated = queryFactory.update(user)
                .set(user.nickname, userUpdateRequest.getNickname())
                .set(user.job, UserJob.fromJobName(userUpdateRequest.getJob()))
                .set(user.position, UserPosition.fromPositionName(userUpdateRequest.getPosition()))
                .set(user.joinYear, userUpdateRequest.getJoinYear())
                .set(user.marketingOptIn, userUpdateRequest.isMarketingOptIn())
                .set(user.profileImageUrl, userUpdateRequest.getProfileImageUrl())
                .where(user.deletedAt.isNull().and(user.id.eq(UUID.fromString(userUpdateRequest.getId()))))
                .execute();

        log.info("Updated rows: {}", updated);
    }

    @Override
    public void updateLastLoginAtByUserUUID(String uuid) {
        QUser user = QUser.user;

        log.info("Update last login at by user uuid: {}", uuid);

        queryFactory.update(user)
                .set(user.lastLoginAt, LocalDateTime.now())
                .where(user.deletedAt.isNull().and(user.id.eq(UUID.fromString(uuid))))
                .execute();
    }

}
