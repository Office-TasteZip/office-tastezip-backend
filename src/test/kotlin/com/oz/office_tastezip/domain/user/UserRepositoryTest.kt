package com.oz.office_tastezip.domain.user

import com.oz.office_tastezip.domain.user.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
//@Transactional
class UserRepositoryTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `사용자 저장 테스트`() {
        // given
        val user = User(
            name = "테스터",
            email = "tester@example.com"
        )

        // when
        val saved = userRepository.save(user)

        // then
        val found = userRepository.findById(saved.id!!)
        assertEquals("테스터", found.get().name)
        assertEquals("tester@example.com", found.get().email)
    }

}
