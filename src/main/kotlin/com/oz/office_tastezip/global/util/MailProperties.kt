package com.oz.office_tastezip.global.util

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
data class MailProperties(
    var host: String = "",
    var port: Int = 587,
    var username: String = "",
    var password: String = ""
)
