package com.oz.office_tastezip.global.config

import com.oz.office_tastezip.infrastructure.s3.S3Properties
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI

@Configuration
class S3Config(
    private val s3Properties: S3Properties,
) {

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .endpointOverride(URI.create(s3Properties.url))  // TODO 실제 S3 연동 시 제거
            .region(Region.AP_SOUTH_1)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(s3Properties.accessKey, s3Properties.secretKey))
            )
            .serviceConfiguration(
                S3Configuration.builder().pathStyleAccessEnabled(true).build()
            )
            .build()
    }

}
