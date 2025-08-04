package com.oz.office_tastezip.global.config

import com.oz.office_tastezip.global.util.MailProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.util.*

@Configuration
class MailConfig(private val properties: MailProperties) {

    @Bean
    fun javaMailSender(): JavaMailSender {
        return JavaMailSenderImpl().apply {
            this.host = properties.host
            this.port = properties.port
            this.username = properties.username
            this.password = properties.password
            javaMailProperties = Properties().apply {
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.timeout", "5000")
            }
        }
    }

    @Bean
    fun templateEngine(): SpringTemplateEngine {
        return SpringTemplateEngine().apply {
            setTemplateResolver(ClassLoaderTemplateResolver().apply {
                prefix = "templates/"
                suffix = ".html"
                characterEncoding = "UTF-8"
                templateMode = TemplateMode.HTML
            }
            )
        }
    }
}
