package com.oz.office_tastezip.infrastructure.mail

import com.oz.office_tastezip.global.util.MailProperties
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.spring6.SpringTemplateEngine

class MailClientTest : StringSpec({

    val mailSender = mockk<JavaMailSender>(relaxUnitFun = true)
    val mailProps = MailProperties(username = "test@example.com")
    val templateEngine = mockk<SpringTemplateEngine>()
    val mailClient = MailClient(mailSender, mailProps, templateEngine)

    "메일 전송" {
        val to = "recipient@example.com"
        val subject = "Test Subject"
        val content = "Test Content"

        val slot = slot<SimpleMailMessage>()
        every { mailSender.send(capture(slot)) } just runs

        mailClient.sendSimpleMail(to, subject, content)

        verify(exactly = 1) { mailSender.send(any<SimpleMailMessage>()) }

        slot.captured.apply {
            from shouldNotBe null
            to shouldNotBe null
            this.subject shouldNotBe null
            text shouldNotBe null
        }
    }
})
