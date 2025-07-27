package com.oz.office_tastezip.domain.user.repository

import com.oz.office_tastezip.domain.auth.enums.UserRole
import com.oz.office_tastezip.domain.auth.enums.UserStatus
import com.oz.office_tastezip.domain.user.User
import com.oz.office_tastezip.domain.user.dto.UserRequestDto
import com.oz.office_tastezip.domain.user.dto.UserRequestDto.UserUpdateRequest
import com.oz.office_tastezip.domain.user.enums.UserJob
import com.oz.office_tastezip.domain.user.enums.UserPosition
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Rollback
@Transactional
@SpringBootTest
class UserRepositoryTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val em: EntityManager,
) : FunSpec({

    val log = LoggerFactory.getLogger(this::class.java)

    context("회원 가입") {
        test("사용자 저장 테스트") {
            val randomEmail = "tester-${UUID.randomUUID()}@gmail.com"
            val user = User.create(getUserInsertRequest(randomEmail), passwordEncoder)
            val saved = userRepository.save(user)
            val result = userRepository.findById(saved.id).orElse(null)

            result.shouldNotBeNull().let {
                it.email shouldBe randomEmail
                passwordEncoder.matches("password", result.passwordHash).shouldBeTrue()
                it.nickname shouldBe "tester"
                it.job shouldBe UserJob.PRODUCTION
                it.position shouldBe UserPosition.SENIOR
                it.role shouldBe UserRole.ROLE_USER
                it.status shouldBe UserStatus.ACTIVE
                it.joinYear shouldBe "2025"
                it.marketingOptIn.shouldBeFalse()
                it.createdAt.shouldNotBeNull()
                it.updatedAt.shouldNotBeNull()
                it.deletedAt.shouldBeNull()
            }
        }
    }

    context("정보 조회") {
        test("사용자 정보(내정보) 조회 테스트") {
            val user = User.create(getUserInsertRequest(), passwordEncoder)
            val saved = userRepository.save(user)

            val found = userRepository.findByUserUUID(saved.id.toString())

            found.shouldNotBeNull().let {
                it.id shouldBe user.id
                it.email shouldBe user.email
                it.nickname shouldBe user.nickname
                it.deletedAt.shouldBeNull()
            }
        }
    }

    context("회원 탈퇴") {
        test("회원 탈퇴 테스트") {
            val user = User.create(getUserInsertRequest(), passwordEncoder)
            userRepository.save(user)
            userRepository.deleteByUserUUID(user.id.toString())
            em.clear()

            val byUserUUID = userRepository.findById(user.id)
            byUserUUID.shouldBePresent {
                log.info("result user info: {}", it)
                it.deletedAt.shouldNotBeNull()
                it.status shouldBe UserStatus.WITHDRAWN
            }
        }
    }

    context("정보 수정") {
        test("사용자 수정 테스트") {
            val user = User.create(getUserInsertRequest(), passwordEncoder)
            userRepository.save(user)

            val updateRequest = getUserUpdateRequest(user.id.toString())
            userRepository.updateByUserUUID(updateRequest)
            em.clear()

            val updatedUser = userRepository.findById(user.id).orElse(null)

            updatedUser.shouldNotBeNull().let {
                it.nickname shouldBe updateRequest.nickname
                it.job.name.lowercase() shouldBe updateRequest.job.lowercase()
                it.position.name.lowercase() shouldBe updateRequest.position.lowercase()
                it.joinYear shouldBe updateRequest.joinYear
            }
        }

        test("프로필 이미지 수정 테스트") {
            // TODO
        }
    }

}) {
    override fun extensions() = listOf(SpringExtension)
}

private fun getUserInsertRequest(email: String = "tester-${UUID.randomUUID()}@gmail.com"): UserRequestDto.UserInsertRequest {
    return UserRequestDto.UserInsertRequest(
        email,
        "password",
        "password",
        "tester",
        "production",
        "senior",
        "2025",
        false
    )
}

private fun getUserUpdateRequest(id: String?): UserUpdateRequest {
    return UserUpdateRequest(
        id,
        "닉네임234",
        "rnd",
        "head",
        "2024",
        false
    )
}
