package com.oz.office_tastezip.global.config

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.jasypt.encryption.StringEncryptor
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableEncryptableProperties
class JasyptConfig {

    @Value("\${jasypt.password}")
    private lateinit var jasyptPassword: String

    @Value("\${jasypt.encryptor.algorithm}")
    private lateinit var jasyptAlgorithm: String

    @Bean("jasyptEncryptor")
    fun stringEncryptor(): StringEncryptor {
        val config = SimpleStringPBEConfig().apply {
            password = jasyptPassword
            algorithm = jasyptAlgorithm
            setKeyObtentionIterations("1000")
            setPoolSize("1")
            setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator")
            setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator")
            stringOutputType = "base64"
        }

        return PooledPBEStringEncryptor().apply {
            setConfig(config)
        }
    }
}
