package com.oz.office_tastezip.domain.auth.service

import com.oz.office_tastezip.domain.auth.enums.EmailVerificationPurpose
import com.oz.office_tastezip.global.exception.RequestFailureException
import com.oz.office_tastezip.global.util.RedisUtils
import com.oz.office_tastezip.infrastructure.mail.MailClient
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class MailService(
    private val mailClient: MailClient,
    private val redisUtils: RedisUtils
) {
    private val log = KotlinLogging.logger {}

    fun sendVerificationEmail(email: String, purpose: EmailVerificationPurpose, requestUri: String) {
        val key = "${purpose.attemptKeyPrefix}$email"
        val attempts = redisUtils.get(key)?.toString()?.toIntOrNull() ?: 0
        val ttl = redisUtils.getExpire(key, TimeUnit.SECONDS)

        log.info { "RequestUri: $requestUri, attempts: $attempts, ttl: $ttl" }
        if (attempts >= 3 && ttl > 0) {
            val minutes = (ttl + 59) / 60
            throw RequestFailureException("이메일 요청이 너무 많습니다. ${minutes}분 후 다시 시도해 주세요.")
        }

        val verificationCode = UUID.randomUUID().toString().replace("-", "")
        mailClient.sendMimeMail(email, verificationCode, purpose)

        redisUtils.set(key, (attempts + 1).toString(), 3, TimeUnit.MINUTES)
        redisUtils.set("${purpose.codeKeyPrefix}$email", verificationCode, 5, TimeUnit.MINUTES)
    }

}
