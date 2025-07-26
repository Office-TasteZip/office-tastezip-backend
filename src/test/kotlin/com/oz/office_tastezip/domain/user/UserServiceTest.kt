package com.oz.office_tastezip.domain.user

import com.oz.office_tastezip.global.exception.UserNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Rollback
@Transactional
@SpringBootTest
class UserServiceTest(
    @Autowired private val userService: UserService
) : FunSpec({

    context("사용자 조회") {
        test("존재하지 않는 사용자 조회 시 USER_NOT_FOUND") {
            val invalidUUID = UUID.randomUUID().toString()

            shouldThrow<UserNotFoundException> {
                userService.findByUserUUID(invalidUUID)
            }
        }
    }

})
