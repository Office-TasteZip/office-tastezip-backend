package com.oz.office_tastezip.infrastructure.mail

import com.oz.office_tastezip.domain.auth.enums.EmailVerificationPurpose
import com.oz.office_tastezip.global.exception.RequestFailureException
import com.oz.office_tastezip.global.util.MailProperties
import jakarta.mail.internet.InternetAddress
import mu.KotlinLogging
import org.springframework.core.io.FileSystemResource
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import java.io.File

private val log = KotlinLogging.logger {}

@Component
class MailClient(
    private val mailSender: JavaMailSender,
    private val mailProperties: MailProperties,
    private val templateEngine: SpringTemplateEngine
) {

    fun sendSimpleMail(to: String, subject: String, content: String) {
        try {
            val message = SimpleMailMessage().apply {
                from = mailProperties.username
                setTo(to)
                setSubject(subject)
                text = content
            }

            mailSender.send(message)
            log.info { "메일 전송 성공 [to=$to, subject=$subject, content=$content]" }

        } catch (e: Exception) {
            log.error(e) { "메일 전송 실패 [to=$to, subject=$subject]" }
            throw RequestFailureException("메일 전송 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
    }

    fun sendMimeMail(to: String, code: String, purpose: EmailVerificationPurpose) {
        val subject = purpose.subject
        try {
            val content = buildVerificationMail(to, code, purpose)

            val message = MimeMessageHelper(mailSender.createMimeMessage(), true, "UTF-8").apply {
                setFrom(InternetAddress(mailProperties.username, "오피스맛집"))
                setTo(to)
                setSubject(subject)
                setText(content, true)
            }

            mailSender.send(message.mimeMessage)
            log.info { "메일 전송 성공 [to=$to, subject=$subject, code: $code]" }
        } catch (e: Exception) {
            log.error(e) { "메일 전송 실패 [to=$to, subject=$subject]" }
            throw RequestFailureException("메일 전송 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
    }

    fun sendMailWithAttachment(
        to: String,
        subject: String,
        content: String,
        pathToAttachment: String,
        attachmentName: String
    ) {
        try {
            val message = mailSender.createMimeMessage()

            MimeMessageHelper(message, true, "UTF-8").apply {
                setFrom(mailProperties.username)
                setTo(to)
                setSubject(subject)
                setText(content)
                addAttachment(attachmentName, FileSystemResource(File(pathToAttachment)))
            }

            mailSender.send(message)
            log.info { "첨부파일 메일 전송 성공 [to=$to, subject=$subject, file=$attachmentName]" }

        } catch (e: Exception) {
            log.error(e) { "첨부파일 메일 전송 실패 [to=$to, subject=$subject]" }
            throw RequestFailureException("메일 전송 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
        }
    }

    fun buildVerificationMail(email: String, code: String, purpose: EmailVerificationPurpose): String {
        val context = Context().apply {
            setVariable("userEmail", email)
            setVariable("verificationCode", code)
            setVariable("purpose", purpose.name)
        }
        return templateEngine.process("email-verification", context)
    }
}
